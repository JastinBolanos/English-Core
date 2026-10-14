package com.example.data.local

import android.content.Context
import com.example.R
import com.example.data.local.dao.WordDao
import com.example.data.local.entity.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
  private val context: Context,
  private val wordDao: WordDao
) {

  private val nounSet = setOf(
    "time", "year", "people", "way", "day", "man", "thing", "woman", "life",
    "child", "world", "school", "state", "family", "student", "group", "country",
    "problem", "hand", "part", "place", "case", "week", "company", "system",
    "program", "question", "work", "government", "number", "night", "point",
    "home", "water", "room", "mother", "area", "money", "story", "fact", "month",
    "lot", "right", "study", "book", "eye", "job", "word", "business", "issue",
    "side", "kind", "head", "house", "service", "friend", "father", "power",
    "hour", "game", "line", "end", "member", "law", "car", "city", "community",
    "name", "president", "team", "minute", "idea", "kid", "body", "information",
    "back", "parent", "face", "level", "office", "door", "health", "person",
    "art", "war", "history", "party", "result", "change", "morning", "reason",
    "research", "girl", "guy", "moment", "air", "teacher", "force", "education",
    "rain", "traffic", "weather", "accident", "delay", "noise", "storm", "effort"
  )

  private val pronounSet = setOf(
    "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us",
    "them", "myself", "yourself", "himself", "herself", "itself", "ourselves",
    "themselves", "someone", "anyone", "everyone", "nobody", "something",
    "anything", "everything", "nothing"
  )

  private val auxiliarySet = setOf(
    "can", "could", "will", "would", "should", "must", "may", "might", "shall",
    "ought"
  )

  private val verbSet = setOf(
    "be", "have", "do", "say", "go", "get", "make", "know", "think", "take",
    "see", "come", "want", "look", "use", "find", "give", "tell", "work", "call",
    "try", "ask", "need", "feel", "become", "leave", "put", "mean", "keep",
    "let", "begin", "seem", "help", "talk", "turn", "start", "show", "hear",
    "play", "run", "move", "like", "live", "believe", "hold", "bring", "happen",
    "write", "provide", "sit", "stand", "lose", "pay", "meet", "include",
    "continue", "set", "learn", "change", "lead", "understand", "watch", "follow",
    "stop", "create", "speak", "read", "allow", "add", "spend", "grow", "open",
    "walk", "win", "offer", "remember", "love", "consider", "appear", "buy",
    "wait", "serve", "die", "send", "expect", "build", "stay", "fall", "cut",
    "reach", "kill", "remain", "arrive", "succeed", "improve", "practice"
  )

  init {
    com.example.domain.util.WordDictionary.loadTranslationsFromContext(context)
  }

  suspend fun prePopulateIfNeeded() = withContext(Dispatchers.IO) {
    com.example.domain.util.WordDictionary.loadTranslationsFromContext(context)
    val count = wordDao.getWordCount()
    if (count == 0) {
      loadAndInsertWords()
    } else {
      // Si la base de datos ya fue poblada, no borrarla para preservar useCount (historial de conteo)
      // y datos SRS del usuario.
      // Si faltaba la palabra 'rank' por el bug de encabezados anterior, insertarla sin alterar el resto
      val rankEntity = wordDao.getWordByLemma("rank")
      if (rankEntity == null) {
        wordDao.insertWord(
          WordEntity(
            word = "rank",
            rank = 1535,
            partOfSpeech = inferPartOfSpeech("rank"),
            useCount = 0
          )
        )
      }
    }
  }

  suspend fun forceReload() = withContext(Dispatchers.IO) {
    wordDao.clearAll()
    loadAndInsertWords()
  }

  private suspend fun loadAndInsertWords() {
    val entities = mutableListOf<WordEntity>()
    try {
      val inputStream = context.resources.openRawResource(R.raw.ngsl_stats)
      val reader = BufferedReader(InputStreamReader(inputStream))
      var rankCounter = 1

      reader.useLines { lines ->
        lines.forEach { line ->
          val trimmed = line.trim()
          if (trimmed.isNotEmpty()) {
            val tokens = trimmed.split(",")
            val wordLemma = tokens.getOrNull(0)?.trim() ?: ""

            // Solo ignorar si es un encabezado real (donde token 1 no es número de rank válido)
            val parsedRank = tokens.getOrNull(1)?.trim()?.toIntOrNull()
            val isHeader = parsedRank == null

            if (wordLemma.isNotEmpty() && !isHeader) {
              val finalRank = parsedRank ?: rankCounter
              
              // Parse POS if specified in CSV or infer from dictionary
              val posFromCsv = tokens.firstOrNull { token ->
                val upper = token.trim().uppercase()
                upper.contains("NOUN") || upper.contains("VERB") || upper.contains("PRON") || upper.contains("AUX")
              }?.trim()?.uppercase()

              val finalPos = posFromCsv ?: inferPartOfSpeech(wordLemma)

              entities.add(
                WordEntity(
                  word = wordLemma,
                  rank = finalRank,
                  partOfSpeech = finalPos,
                  useCount = 0
                )
              )
              rankCounter++
            }
          }
        }
      }
    } catch (_: Exception) {
      // Handle file reading
    }

    // If CSV was empty (user hasn't pasted the 2,800 records yet), seed default foundational words
    if (entities.isEmpty()) {
      entities.addAll(generateSeedWords())
    }

    if (entities.isNotEmpty()) {
      // Batch insert into Room
      wordDao.insertAll(entities)
    }
  }

  private fun inferPartOfSpeech(lemma: String): String {
    val lower = lemma.lowercase().trim()
    return when {
      com.example.domain.util.ConnectorRulesCatalog.hasRule(lower) -> "CONNECTOR"
      com.example.domain.util.WordDictionary.isConnector(lower) -> "CONNECTOR"
      com.example.domain.util.WordDictionary.isPronoun(lower) || pronounSet.contains(lower) -> "PRONOUN"
      auxiliarySet.contains(lower) -> "AUXILIARY"
      com.example.domain.util.WordDictionary.isVerb(lower) || verbSet.contains(lower) -> "VERB"
      nounSet.contains(lower) || com.example.domain.util.WordDictionary.isNoun(lower) -> "NOUN"
      else -> "NOUN" // Generic fallback for uncategorized nouns
    }
  }

  private fun generateSeedWords(): List<WordEntity> {
    val list = mutableListOf<WordEntity>()
    var rank = 1

    pronounSet.take(10).forEach {
      list.add(WordEntity(word = it, rank = rank++, partOfSpeech = "PRONOUN"))
    }
    nounSet.take(25).forEach {
      list.add(WordEntity(word = it, rank = rank++, partOfSpeech = "NOUN"))
    }
    verbSet.take(20).forEach {
      list.add(WordEntity(word = it, rank = rank++, partOfSpeech = "VERB"))
    }
    auxiliarySet.take(8).forEach {
      list.add(WordEntity(word = it, rank = rank++, partOfSpeech = "AUXILIARY"))
    }

    return list
  }
}
