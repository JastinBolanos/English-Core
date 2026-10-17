package com.example.domain.repository

import com.example.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
  fun getAllWords(): Flow<List<Word>>
  suspend fun prePopulateDatabaseIfNeeded()
  suspend fun reloadDatabaseFromCsv()
  suspend fun incrementWordUseCount(word: String)
  suspend fun incrementWordUseCount(id: Long, word: String = "")
  suspend fun addCustomWord(
    word: String,
    partOfSpeech: String,
    spanishTranslation: String,
    microExample: String
  ): Word
}
