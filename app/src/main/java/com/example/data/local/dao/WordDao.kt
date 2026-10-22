package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

  /**
   * REGLA 1: Orden de Combate (Frecuencia).
   * Las palabras se cargan estrictamente por frecuencia (ORDER BY rank ASC),
   * garantizando que la palabra #1 del CSV aparezca en primera posición
   * y NUNCA en orden alfabético.
   */
  @Query("SELECT * FROM words ORDER BY rank ASC")
  fun getAllWords(): Flow<List<WordEntity>>

  @Query("SELECT * FROM words WHERE partOfSpeech LIKE '%' || :category || '%' ORDER BY rank ASC")
  fun getWordsByCategory(category: String): Flow<List<WordEntity>>

  @Query("""
    SELECT * FROM words 
    WHERE partOfSpeech LIKE '%' || :cat1 || '%'
       OR partOfSpeech LIKE '%' || :cat2 || '%'
    ORDER BY rank ASC
  """)
  fun getWordsByTwoCategories(cat1: String, cat2: String): Flow<List<WordEntity>>

  @Query("SELECT * FROM words WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC, rank ASC LIMIT :limit")
  fun getWordsForReview(currentTime: Long, limit: Int = 20): Flow<List<WordEntity>>

  @Query("UPDATE words SET `interval` = :interval, easeFactor = :easeFactor, nextReviewDate = :nextReviewDate WHERE id = :id")
  suspend fun updateSrsData(id: Long, interval: Int, easeFactor: Float, nextReviewDate: Long)

  @Query("UPDATE words SET useCount = useCount + 1 WHERE LOWER(TRIM(word)) = LOWER(TRIM(:word))")
  suspend fun incrementUseCountByWord(word: String): Int

  @Query("UPDATE words SET useCount = useCount + 1 WHERE id = :id")
  suspend fun incrementUseCount(id: Long): Int

  @Query("UPDATE words SET useCount = :count WHERE LOWER(TRIM(word)) = LOWER(TRIM(:word))")
  suspend fun setUseCountByWord(word: String, count: Int): Int

  @Query("UPDATE words SET useCount = :count WHERE id = :id")
  suspend fun setUseCount(id: Long, count: Int): Int

  @androidx.room.Transaction
  suspend fun updateAllUseCounts(counts: Map<String, Int>) {
    for ((word, count) in counts) {
      if (word.isNotBlank()) {
        setUseCountByWord(word.lowercase().trim(), count)
      }
    }
  }

  @Query("SELECT * FROM words WHERE LOWER(TRIM(word)) = LOWER(TRIM(:word)) LIMIT 1")
  suspend fun getWordByLemma(word: String): WordEntity?

  @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
  suspend fun getWordById(id: Long): WordEntity?

  @Query("SELECT COUNT(*) FROM words")
  suspend fun getWordCount(): Int

  @Query("SELECT MAX(rank) FROM words")
  suspend fun getMaxRank(): Int?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWord(word: WordEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(words: List<WordEntity>)

  @Query("DELETE FROM words")
  suspend fun clearAll()
}
