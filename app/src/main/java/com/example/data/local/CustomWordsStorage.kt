package com.example.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

data class CustomWordRecord(
  val rank: Int,
  val word: String,
  val partOfSpeech: String,
  val spanishTranslation: String = "",
  val microExample: String = "",
  val useCount: Int = 0
)

class CustomWordsStorage(private val context: Context) {

  private val jsonFile: File
    get() = File(context.filesDir, "custom_words.json")

  init {
    clearStorage()
  }

  @Synchronized
  fun clearStorage() {
    try {
      if (jsonFile.exists()) {
        jsonFile.delete()
      }
    } catch (_: Exception) {}
  }

  @Synchronized
  fun loadCustomWords(): List<CustomWordRecord> {
    clearStorage()
    return emptyList()
  }

  @Synchronized
  fun saveCustomWord(record: CustomWordRecord) {
    // Método de guardado en .json eliminado a petición del usuario.
    clearStorage()
  }

  @Synchronized
  fun deleteCustomWord(rank: Int) {
    clearStorage()
  }
}
