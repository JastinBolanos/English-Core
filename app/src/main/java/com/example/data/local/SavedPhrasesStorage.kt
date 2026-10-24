package com.example.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Representa una frase armada en el lienzo de pruebas y guardada para su reutilización.
 */
data class SavedDraftPhrase(
  val id: String = UUID.randomUUID().toString(),
  val phraseText: String,
  val words: List<String> = emptyList(),
  val createdAt: Long = System.currentTimeMillis()
)

/**
 * Almacenamiento persistente local en SharedPreferences (con serialización JSON)
 * para las frases armadas desde el lienzo de pruebas.
 */
class SavedPhrasesStorage(context: Context) {
  private val prefs = context.getSharedPreferences("english_saved_phrases_prefs", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_SAVED_PHRASES_JSON = "key_saved_draft_phrases_json"
  }

  fun loadSavedPhrases(): List<SavedDraftPhrase> {
    val rawJson = prefs.getString(KEY_SAVED_PHRASES_JSON, null) ?: return emptyList()
    val list = mutableListOf<SavedDraftPhrase>()
    try {
      val jsonArray = JSONArray(rawJson)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        val wordsArray = obj.optJSONArray("words")
        val wordsList = mutableListOf<String>()
        if (wordsArray != null) {
          for (j in 0 until wordsArray.length()) {
            wordsList.add(wordsArray.optString(j))
          }
        }
        val text = obj.optString("phraseText", "").ifBlank {
          wordsList.joinToString(" ")
        }
        list.add(
          SavedDraftPhrase(
            id = obj.optString("id", UUID.randomUUID().toString()),
            phraseText = text,
            words = wordsList,
            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
          )
        )
      }
    } catch (_: Exception) {
      return emptyList()
    }
    return list
  }

  fun saveSavedPhrases(phrases: List<SavedDraftPhrase>) {
    try {
      val jsonArray = JSONArray()
      phrases.forEach { item ->
        val obj = JSONObject().apply {
          put("id", item.id)
          put("phraseText", item.phraseText)
          val wArr = JSONArray()
          item.words.forEach { wArr.put(it) }
          put("words", wArr)
          put("createdAt", item.createdAt)
        }
        jsonArray.put(obj)
      }
      prefs.edit().putString(KEY_SAVED_PHRASES_JSON, jsonArray.toString()).apply()
    } catch (_: Exception) {
    }
  }

  fun addSavedPhrase(phrase: SavedDraftPhrase): SavedDraftPhrase {
    val current = loadSavedPhrases().toMutableList()
    current.add(0, phrase)
    saveSavedPhrases(current)
    return phrase
  }

  fun addPhrase(phraseText: String, words: List<String>): SavedDraftPhrase {
    val current = loadSavedPhrases().toMutableList()
    val cleanText = phraseText.trim().ifBlank { words.joinToString(" ") }
    val newPhrase = SavedDraftPhrase(
      id = UUID.randomUUID().toString(),
      phraseText = cleanText,
      words = words,
      createdAt = System.currentTimeMillis()
    )
    current.add(0, newPhrase)
    saveSavedPhrases(current)
    return newPhrase
  }

  fun deletePhrase(id: String) {
    val current = loadSavedPhrases().filter { it.id != id }
    saveSavedPhrases(current)
  }

  fun clearAll() {
    prefs.edit().remove(KEY_SAVED_PHRASES_JSON).apply()
  }
}
