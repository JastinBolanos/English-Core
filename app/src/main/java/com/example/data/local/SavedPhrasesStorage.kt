package com.example.data.local

import android.content.Context
import com.example.domain.util.PhraseTranslator
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Representa una frase armada en el lienzo de pruebas y guardada para su reutilización.
 */
data class SavedDraftPhrase(
  val id: String = UUID.randomUUID().toString(),
  val phraseText: String,
  val words: List<String> = emptyList(),
  val spanishTranslation: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

/**
 * Almacenamiento persistente local en archivo JSON 'saved_phrases.json' en el almacenamiento interno de la app
 * (con sincronización y respaldo en SharedPreferences).
 * Permite guardar, cargar, reutilizar y eliminar frases armadas desde el lienzo de pruebas.
 */
class SavedPhrasesStorage(private val context: Context) {

  private val jsonFile: File
    get() = File(context.filesDir, "saved_phrases.json")

  private val prefs by lazy {
    context.getSharedPreferences("english_saved_phrases_prefs", Context.MODE_PRIVATE)
  }

  companion object {
    private const val KEY_SAVED_PHRASES_JSON = "key_saved_draft_phrases_json"
  }

  @Synchronized
  fun loadSavedPhrases(): List<SavedDraftPhrase> {
    val file = jsonFile
    val list = mutableListOf<SavedDraftPhrase>()

    val rawJson = if (file.exists()) {
      try {
        file.readText(StandardCharsets.UTF_8).trim()
      } catch (_: Exception) {
        null
      }
    } else {
      // Migración desde SharedPreferences si el archivo .json aún no existía
      val fromPrefs = prefs.getString(KEY_SAVED_PHRASES_JSON, null)
      if (!fromPrefs.isNullOrBlank()) {
        try {
          file.writeText(fromPrefs, StandardCharsets.UTF_8)
        } catch (_: Exception) {}
      }
      fromPrefs
    }

    if (rawJson.isNullOrBlank()) return emptyList()

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
        }.trim()

        if (text.isNotBlank()) {
          var trans = obj.optString("spanishTranslation", "").trim()
          if (trans.isBlank()) {
            trans = PhraseTranslator.translatePhrase(text)
          }

          list.add(
            SavedDraftPhrase(
              id = obj.optString("id", UUID.randomUUID().toString()),
              phraseText = text,
              words = wordsList,
              spanishTranslation = trans,
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }
    } catch (_: Exception) {
      return emptyList()
    }
    return list
  }

  @Synchronized
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
          put("spanishTranslation", item.spanishTranslation)
          put("createdAt", item.createdAt)
        }
        jsonArray.put(obj)
      }
      val formattedJson = jsonArray.toString(2)
      jsonFile.writeText(formattedJson, StandardCharsets.UTF_8)
      prefs.edit().putString(KEY_SAVED_PHRASES_JSON, formattedJson).apply()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @Synchronized
  fun addSavedPhrase(phrase: SavedDraftPhrase): SavedDraftPhrase {
    val current = loadSavedPhrases().toMutableList()
    val cleanText = phrase.phraseText.trim()
    val cleanWords = if (phrase.words.isNotEmpty()) phrase.words else cleanText.split("\\s+".toRegex()).filter { it.isNotBlank() }
    val translation = if (phrase.spanishTranslation.isNotBlank()) {
      phrase.spanishTranslation.trim()
    } else {
      PhraseTranslator.translatePhrase(cleanText)
    }

    val completePhrase = phrase.copy(
      phraseText = cleanText,
      words = cleanWords,
      spanishTranslation = translation
    )

    // Si ya existía una frase con el mismo texto, reemplazarla y colocarla al inicio
    current.removeAll { it.phraseText.equals(cleanText, ignoreCase = true) }
    current.add(0, completePhrase)
    saveSavedPhrases(current)
    return completePhrase
  }

  @Synchronized
  fun addPhrase(
    phraseText: String,
    words: List<String>,
    spanishTranslation: String = ""
  ): SavedDraftPhrase {
    val cleanText = phraseText.trim().ifBlank { words.joinToString(" ") }
    val translation = if (spanishTranslation.isNotBlank()) {
      spanishTranslation.trim()
    } else {
      PhraseTranslator.translatePhrase(cleanText)
    }
    val newPhrase = SavedDraftPhrase(
      id = UUID.randomUUID().toString(),
      phraseText = cleanText,
      words = words,
      spanishTranslation = translation,
      createdAt = System.currentTimeMillis()
    )
    return addSavedPhrase(newPhrase)
  }

  @Synchronized
  fun deletePhrase(id: String) {
    val current = loadSavedPhrases().filter { it.id != id }
    saveSavedPhrases(current)
  }

  @Synchronized
  fun clearAll() {
    try {
      if (jsonFile.exists()) {
        jsonFile.delete()
      }
      prefs.edit().remove(KEY_SAVED_PHRASES_JSON).apply()
    } catch (_: Exception) {}
  }
}
