package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val rank: Int = 0,
  val word: String = "",
  val partOfSpeech: String = "",
  val interval: Int = 0,
  val easeFactor: Float = 2.5f,
  val nextReviewDate: Long = 0L,
  val useCount: Int = 0
)
