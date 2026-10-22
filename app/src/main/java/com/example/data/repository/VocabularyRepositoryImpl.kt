package com.example.data.repository

import com.example.data.local.DatabaseInitializer
import com.example.data.local.dao.WordDao
import com.example.data.local.entity.WordEntity
import com.example.domain.model.PartOfSpeech
import com.example.domain.model.Word
import com.example.domain.repository.VocabularyRepository
import com.example.domain.util.ConnectorRulesCatalog
import com.example.domain.util.WordDictionary
import com.example.domain.util.WordGrammarCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyRepositoryImpl @Inject constructor(
  private val wordDao: WordDao,
  private val databaseInitializer: DatabaseInitializer,
  private val context: android.content.Context? = null
) : VocabularyRepository {

  private val customWordsStorage by lazy {
    context?.let { com.example.data.local.CustomWordsStorage(it) }
  }

  /**
   * Obtiene todas las palabras respetando el Orden de Combate (rank ASC).
   */
  override fun getAllWords(): Flow<List<Word>> {
    return wordDao.getAllWords().map { entities ->
      entities.map { it.toDomain() }
    }
  }

  override suspend fun prePopulateDatabaseIfNeeded() {
    databaseInitializer.prePopulateIfNeeded()
  }

  override suspend fun reloadDatabaseFromCsv() {
    databaseInitializer.forceReload()
  }

  override suspend fun incrementWordUseCount(word: String) {
    if (word.isNotBlank()) {
      wordDao.incrementUseCountByWord(word.trim())
    }
  }

  override suspend fun incrementWordUseCount(id: Long, word: String) {
    var rowsUpdated = 0
    if (id > 0L) {
      rowsUpdated = wordDao.incrementUseCount(id)
    }
    if (rowsUpdated == 0 && word.isNotBlank()) {
      wordDao.incrementUseCountByWord(word.trim())
    }
  }

  override suspend fun setWordUseCounts(counts: Map<String, Int>) {
    withContext(Dispatchers.IO) {
      wordDao.updateAllUseCounts(counts)
    }
  }

  override suspend fun restoreCustomWords(customWords: List<com.example.data.local.CustomWordRecord>) {
    withContext(Dispatchers.IO) {
      for (cw in customWords) {
        val clean = cw.word.lowercase(java.util.Locale.ROOT).trim()
        if (clean.isBlank()) continue
        customWordsStorage?.saveCustomWord(cw)
        WordGrammarCatalog.injectGrammarTypes(mapOf(clean to cw.partOfSpeech))
        if (cw.spanishTranslation.isNotBlank()) {
          WordDictionary.injectTranslations(mapOf(clean to cw.spanishTranslation))
        }
        if (cw.microExample.isNotBlank()) {
          WordDictionary.injectExamples(mapOf(clean to cw.microExample))
        }
        val existing = wordDao.getWordByLemma(clean)
        if (existing == null) {
          wordDao.insertWord(
            WordEntity(
              rank = cw.rank,
              word = cw.word,
              partOfSpeech = cw.partOfSpeech,
              interval = 0,
              easeFactor = 2.5f,
              nextReviewDate = 0L,
              useCount = cw.useCount
            )
          )
        } else {
          wordDao.setUseCountByWord(clean, cw.useCount)
        }
      }
    }
  }

  override suspend fun addCustomWord(
    word: String,
    partOfSpeech: String,
    spanishTranslation: String,
    microExample: String
  ): Word {
    val cleanLemma = word.trim()
    val cleanLemmaLower = cleanLemma.lowercase(java.util.Locale.ROOT)
    val currentMaxRank = wordDao.getMaxRank() ?: 2809
    val nextRank = maxOf(2809, currentMaxRank) + 1

    val customRecord = com.example.data.local.CustomWordRecord(
      rank = nextRank,
      word = cleanLemma,
      partOfSpeech = partOfSpeech,
      spanishTranslation = spanishTranslation.trim(),
      microExample = microExample.trim(),
      useCount = 0
    )

    // 1. Guardar en el almacenamiento JSON local del teléfono
    customWordsStorage?.saveCustomWord(customRecord)

    // 2. Inyectar en catálogos en memoria
    WordGrammarCatalog.injectGrammarTypes(mapOf(cleanLemmaLower to partOfSpeech))
    if (spanishTranslation.isNotBlank()) {
      WordDictionary.injectTranslations(mapOf(cleanLemmaLower to spanishTranslation.trim()))
    }
    if (microExample.isNotBlank()) {
      WordDictionary.injectExamples(mapOf(cleanLemmaLower to microExample.trim()))
    }

    // 3. Insertar en Room Database
    val entity = WordEntity(
      rank = nextRank,
      word = cleanLemma,
      partOfSpeech = partOfSpeech,
      interval = 0,
      easeFactor = 2.5f,
      nextReviewDate = 0L,
      useCount = 0
    )
    val id = wordDao.insertWord(entity)

    return entity.copy(id = id).toDomain()
  }

  private fun WordEntity.toDomain(): Word {
    val cleanLemma = word.lowercase().trim()

    // 0. Catálogo Maestro de Tipos Gramaticales (2,809 palabras NGSL)
    val customGrammarType = WordGrammarCatalog.getGrammarType(cleanLemma)
      ?: partOfSpeech.takeIf { it.isNotBlank() && it !in setOf("PRON", "CONN", "VERB", "NOUN", "OTHER") }

    if (!customGrammarType.isNullOrBlank()) {
      val (primaryCat, secondaryCat) = WordGrammarCatalog.parseCategories(customGrammarType, cleanLemma)
      val connectorRule = ConnectorRulesCatalog.getRule(cleanLemma)
      return Word(
        lemma = word,
        partOfSpeech = primaryCat,
        secondaryPartOfSpeech = secondaryCat,
        id = id,
        rank = rank,
        rawPartOfSpeech = customGrammarType,
        spanishTranslation = connectorRule?.spanishTranslation ?: WordDictionary.getTranslation(word, primaryCat),
        microExample = connectorRule?.microExample ?: WordDictionary.getMicroExample(word, primaryCat),
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        overrideConnectionNote = connectorRule?.connectionRuleNote,
        useCount = useCount
      )
    }

    // 1. Interceptor de Reglas de Conectores (Diccionario Maestro Local)
    val connectorRule = ConnectorRulesCatalog.getRule(cleanLemma)
    if (connectorRule != null) {
      return Word(
        lemma = word,
        partOfSpeech = connectorRule.category,
        id = id,
        rank = rank,
        rawPartOfSpeech = partOfSpeech,
        spanishTranslation = connectorRule.spanishTranslation,
        microExample = connectorRule.microExample,
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        overrideConnectionNote = connectorRule.connectionRuleNote,
        useCount = useCount
      )
    }

    // 2. Modificadores (artículos, adjetivos, adverbios)
    if (WordDictionary.isModifier(cleanLemma)) {
      return Word(
        lemma = word,
        partOfSpeech = PartOfSpeech.MODIFIER,
        id = id,
        rank = rank,
        rawPartOfSpeech = partOfSpeech,
        spanishTranslation = WordDictionary.getTranslation(word, PartOfSpeech.MODIFIER),
        microExample = WordDictionary.getMicroExample(word, PartOfSpeech.MODIFIER),
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        useCount = useCount
      )
    }

    // 3. Pronombres / Sujetos
    if (WordDictionary.isPronoun(cleanLemma) || partOfSpeech.contains("PRON")) {
      return Word(
        lemma = word,
        partOfSpeech = PartOfSpeech.PRONOUN,
        id = id,
        rank = rank,
        rawPartOfSpeech = partOfSpeech,
        spanishTranslation = WordDictionary.getTranslation(word, PartOfSpeech.PRONOUN),
        microExample = WordDictionary.getMicroExample(word, PartOfSpeech.PRONOUN),
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        useCount = useCount
      )
    }

    // 4. Conectores generales
    if (WordDictionary.isConnector(cleanLemma) || partOfSpeech.contains("CONN")) {
      return Word(
        lemma = word,
        partOfSpeech = PartOfSpeech.CONNECTOR_CLAUSE,
        id = id,
        rank = rank,
        rawPartOfSpeech = partOfSpeech,
        spanishTranslation = WordDictionary.getTranslation(word, PartOfSpeech.CONNECTOR_CLAUSE),
        microExample = WordDictionary.getMicroExample(word, PartOfSpeech.CONNECTOR_CLAUSE),
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        useCount = useCount
      )
    }

    // 5. Palabras con doble función (Verbo / Sustantivo o Sustantivo / Verbo)
    if (WordDictionary.isVerbDominant(cleanLemma)) {
      return Word(
        lemma = word,
        partOfSpeech = PartOfSpeech.VERB,
        secondaryPartOfSpeech = PartOfSpeech.NOUN,
        id = id,
        rank = rank,
        rawPartOfSpeech = partOfSpeech,
        spanishTranslation = WordDictionary.getTranslation(word, PartOfSpeech.VERB),
        microExample = WordDictionary.getMicroExample(word, PartOfSpeech.VERB),
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        useCount = useCount
      )
    }

    if (WordDictionary.isNounDominant(cleanLemma)) {
      return Word(
        lemma = word,
        partOfSpeech = PartOfSpeech.NOUN,
        secondaryPartOfSpeech = PartOfSpeech.VERB,
        id = id,
        rank = rank,
        rawPartOfSpeech = partOfSpeech,
        spanishTranslation = WordDictionary.getTranslation(word, PartOfSpeech.NOUN),
        microExample = WordDictionary.getMicroExample(word, PartOfSpeech.NOUN),
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        useCount = useCount
      )
    }

    // 6. Verbos
    if (WordDictionary.isVerb(cleanLemma) || partOfSpeech.contains("VERB") || partOfSpeech.contains("AUX")) {
      return Word(
        lemma = word,
        partOfSpeech = PartOfSpeech.VERB,
        id = id,
        rank = rank,
        rawPartOfSpeech = partOfSpeech,
        spanishTranslation = WordDictionary.getTranslation(word, PartOfSpeech.VERB),
        microExample = WordDictionary.getMicroExample(word, PartOfSpeech.VERB),
        interval = interval,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        useCount = useCount
      )
    }

    // 7. Sustantivos por defecto
    return Word(
      lemma = word,
      partOfSpeech = PartOfSpeech.NOUN,
      id = id,
      rank = rank,
      rawPartOfSpeech = partOfSpeech,
      spanishTranslation = WordDictionary.getTranslation(word, PartOfSpeech.NOUN),
      microExample = WordDictionary.getMicroExample(word, PartOfSpeech.NOUN),
      interval = interval,
      easeFactor = easeFactor,
      nextReviewDate = nextReviewDate,
      useCount = useCount
    )
  }
}
