package com.example.data.repository

import com.example.data.local.DatabaseInitializer
import com.example.data.local.dao.WordDao
import com.example.data.local.entity.WordEntity
import com.example.domain.model.PartOfSpeech
import com.example.domain.model.Word
import com.example.domain.repository.VocabularyRepository
import com.example.domain.util.ConnectorRulesCatalog
import com.example.domain.util.WordDictionary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyRepositoryImpl @Inject constructor(
  private val wordDao: WordDao,
  private val databaseInitializer: DatabaseInitializer
) : VocabularyRepository {

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
    wordDao.incrementUseCountByWord(word.trim())
  }

  override suspend fun incrementWordUseCount(id: Long, word: String) {
    if (id > 0L) {
      wordDao.incrementUseCount(id)
    } else if (word.isNotBlank()) {
      wordDao.incrementUseCountByWord(word.trim())
    }
  }

  private fun WordEntity.toDomain(): Word {
    val cleanLemma = word.lowercase().trim()

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

    // 2. Clasificación general si no es conector del catálogo
    // PRIORIDAD: Verificar Pronombres, Conectores y Verbos antes de Sustantivo
    val primaryPos = when {
      WordDictionary.isPronoun(cleanLemma) -> PartOfSpeech.PRONOUN
      WordDictionary.isConnector(cleanLemma) -> PartOfSpeech.CONNECTOR_CLAUSE
      WordDictionary.isVerb(cleanLemma) -> PartOfSpeech.VERB
      partOfSpeech.contains("PRON") -> PartOfSpeech.PRONOUN
      partOfSpeech.contains("CONN") -> PartOfSpeech.CONNECTOR_CLAUSE
      partOfSpeech.contains("VERB") || partOfSpeech.contains("AUX") -> PartOfSpeech.VERB
      WordDictionary.isNoun(cleanLemma) -> PartOfSpeech.NOUN
      partOfSpeech.contains("NOUN") -> PartOfSpeech.NOUN
      else -> PartOfSpeech.NOUN
    }

    return Word(
      lemma = word,
      partOfSpeech = primaryPos,
      id = id,
      rank = rank,
      rawPartOfSpeech = partOfSpeech,
      spanishTranslation = WordDictionary.getTranslation(word, primaryPos),
      microExample = WordDictionary.getMicroExample(word, primaryPos),
      interval = interval,
      easeFactor = easeFactor,
      nextReviewDate = nextReviewDate,
      useCount = useCount
    )
  }
}
