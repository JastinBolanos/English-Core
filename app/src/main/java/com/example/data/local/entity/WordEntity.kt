package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "words",
  indices = [
    Index(value = ["partOfSpeech"]),
    Index(value = ["rank"]),
    Index(value = ["nextReviewDate"])
  ]
)
data class WordEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val word: String,
  val rank: Int = 0,
  val partOfSpeech: String = "OTHER",
  val interval: Int = 0,             // Intervalo SRS en días
  val easeFactor: Float = 2.5f,      // Factor de facilidad SuperMemo (por defecto 2.5)
  val nextReviewDate: Long = 0L,     // Timestamp de próxima revisión programada
  val useCount: Int = 0              // Contador de uso persistente al subir al Lienzo de Pruebas
)
