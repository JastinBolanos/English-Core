package com.example.data.local

import android.content.Context
import com.example.R
import com.example.data.local.dao.WordDao
import com.example.data.local.entity.WordEntity
import com.example.domain.util.WordDictionary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
  private val context: Context,
  private val wordDao: WordDao
) {

  suspend fun prePopulateIfNeeded() = withContext(Dispatchers.IO) {
    com.example.domain.util.WordGrammarCatalog.loadGrammarTypesFromContext(context)
    val count = wordDao.getWordCount()
    if (count >= 2809) {
      val sampleWord1 = wordDao.getWordByLemma("the")
      val sampleWord701 = wordDao.getWordByLemma("guy")
      val needsReload = (sampleWord1 != null && sampleWord1.partOfSpeech != "modificador (artículo)") ||
                        (sampleWord701 != null && sampleWord701.partOfSpeech != "sustantivo")
      if (needsReload) {
        loadWordsFromCsv(preserveUseCounts = true)
      }
      return@withContext
    }
    loadWordsFromCsv(preserveUseCounts = count > 0)
  }

  suspend fun forceReload() = withContext(Dispatchers.IO) {
    loadWordsFromCsv(preserveUseCounts = true)
  }

  private suspend fun loadWordsFromCsv(preserveUseCounts: Boolean) {
    val existingUseCounts = mutableMapOf<String, Int>()
    if (preserveUseCounts) {
      try {
        // Collect existing use counts by lemma
        val inputStream = context.resources.openRawResource(R.raw.ngsl_stats)
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        reader.useLines { lines ->
          for (line in lines) {
            val lemma = line.split(",").firstOrNull()?.trim()?.lowercase(Locale.ROOT) ?: continue
            val existing = wordDao.getWordByLemma(lemma)
            if (existing != null && existing.useCount > 0) {
              existingUseCounts[lemma] = existing.useCount
            }
          }
        }
      } catch (_: Exception) {}
    }

    val entities = mutableListOf<WordEntity>()
    try {
      val inputStream = context.resources.openRawResource(R.raw.ngsl_stats)
      val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
      var index = 1
      reader.useLines { lines ->
        for (line in lines) {
          if (line.isBlank()) continue
          val parts = line.split(",")
          val word = parts[0].trim()
          val rank = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: index
          val clean = word.lowercase(Locale.ROOT)

          val grammarType = com.example.domain.util.WordGrammarCatalog.getGrammarType(clean)
          val pos = if (!grammarType.isNullOrBlank()) {
            grammarType
          } else {
            when {
              WordDictionary.isPronoun(clean) -> "PRON"
              WordDictionary.isConnector(clean) -> "CONN"
              WordDictionary.isVerb(clean) -> "VERB"
              WordDictionary.isNoun(clean) -> "NOUN"
              else -> "OTHER"
            }
          }

          val useCount = existingUseCounts[clean] ?: 0

          entities.add(
            WordEntity(
              rank = rank,
              word = word,
              partOfSpeech = pos,
              interval = 0,
              easeFactor = 2.5f,
              nextReviewDate = 0L,
              useCount = useCount
            )
          )
          index++
        }
      }

      if (entities.isNotEmpty()) {
        wordDao.clearAll()
        wordDao.insertAll(entities)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
