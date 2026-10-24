package com.example.domain.repository

import com.example.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
  fun getAllWords(): Flow<List<Word>>
  suspend fun prePopulateDatabaseIfNeeded()
  suspend fun reloadDatabaseFromCsv()
  suspend fun incrementWordUseCount(word: String)
  suspend fun incrementWordUseCount(id: Long, word: String = "")
  suspend fun setWordUseCounts(counts: Map<String, Int>, resetOthersFirst: Boolean = false)
  suspend fun restoreCustomWords(customWords: List<com.example.data.local.CustomWordRecord>)
  suspend fun addCustomWord(
    word: String,
    partOfSpeech: String,
    spanishTranslation: String,
    microExample: String
  ): Word
  suspend fun updateCustomWord(
    rank: Int,
    word: String,
    partOfSpeech: String,
    spanishTranslation: String,
    microExample: String
  ): Word?
}
