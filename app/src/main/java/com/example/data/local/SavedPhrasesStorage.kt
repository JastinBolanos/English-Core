package com.example.data.local

import android.content.Context
import com.example.data.backup.AutoBackupManager
import com.example.domain.util.PhraseTranslator
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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
 * Almacenamiento unificado de frases guardadas dentro del único archivo .json general de la app (vocabflow_backup.json),
 * con caché de acceso inmediato en SharedPreferences.
 * Centraliza todo en un solo archivo maestro para toda la aplicación.
 */
class SavedPhrasesStorage(private val context: Context) {

  private val prefs by lazy {
    context.getSharedPreferences("english_saved_phrases_prefs", Context.MODE_PRIVATE)
  }

  companion object {
    private const val KEY_SAVED_PHRASES_JSON = "key_saved_draft_phrases_json"
  }

  init {
    // Migrar y limpiar archivos sueltos obsoletos si existen
    try {
      val legacyFile = File(context.filesDir, "saved_phrases.json")
      if (legacyFile.exists()) {
        val legacyText = legacyFile.readText().trim()
        if (legacyText.isNotBlank() && legacyText != "[]" && legacyText != "null") {
          val list = parseJsonString(legacyText)
          if (list.isNotEmpty()) {
            saveSavedPhrases(list)
          }
        }
        legacyFile.delete()
      }
      File(context.filesDir, "saved_phrases.json.tmp").delete()
    } catch (_: Exception) {}
  }

  @Synchronized
  fun loadSavedPhrases(): List<SavedDraftPhrase> {
    // 1. Cargar desde el archivo único maestro de toda la app
    val fromBackup = AutoBackupManager.loadLatestBackup(context)
    if (fromBackup != null) {
      return fromBackup.savedDraftPhrases
    }

    // 2. Si aún no se ha generado la copia maestra, consultar el caché local
    val fromPrefs = prefs.getString(KEY_SAVED_PHRASES_JSON, null)?.trim()
    if (!fromPrefs.isNullOrBlank() && fromPrefs != "[]" && fromPrefs != "null") {
      val list = parseJsonString(fromPrefs)
      if (list.isNotEmpty()) {
        return list
      }
    }

    return emptyList()
  }

  @Synchronized
  fun saveSavedPhrases(phrases: List<SavedDraftPhrase>) {
    try {
      val jsonArray = JSONArray()
      phrases.forEach { item ->
        val cleanText = item.phraseText.trim()
        val cleanWords = if (item.words.isNotEmpty()) item.words else cleanText.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val translation = if (item.spanishTranslation.isNotBlank()) {
          item.spanishTranslation.trim()
        } else {
          PhraseTranslator.translatePhrase(cleanText)
        }

        val obj = JSONObject().apply {
          put("id", item.id.ifBlank { UUID.randomUUID().toString() })
          put("phraseText", cleanText)
          val wArr = JSONArray()
          cleanWords.forEach { wArr.put(it) }
          put("words", wArr)
          put("spanishTranslation", translation)
          put("createdAt", if (item.createdAt > 0) item.createdAt else System.currentTimeMillis())
        }
        jsonArray.put(obj)
      }
      val formattedJson = jsonArray.toString(2)

      // Caché rápido reactivo
      prefs.edit().putString(KEY_SAVED_PHRASES_JSON, formattedJson).commit()

      // Guardado centralizado en el ÚNICO archivo .json de toda la app
      AutoBackupManager.updateSavedDraftPhrasesInBackup(context, phrases)
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
      id = phrase.id.ifBlank { UUID.randomUUID().toString() },
      phraseText = cleanText,
      words = cleanWords,
      spanishTranslation = translation,
      createdAt = if (phrase.createdAt > 0) phrase.createdAt else System.currentTimeMillis()
    )

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
      prefs.edit().remove(KEY_SAVED_PHRASES_JSON).commit()
      AutoBackupManager.updateSavedDraftPhrasesInBackup(context, emptyList())
    } catch (_: Exception) {}
  }

  private fun parseJsonString(rawJson: String): List<SavedDraftPhrase> {
    val list = mutableListOf<SavedDraftPhrase>()
    try {
      val jsonArray = JSONArray(rawJson)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.optJSONObject(i) ?: continue
        val wordsArray = obj.optJSONArray("words")
        val wordsList = mutableListOf<String>()
        if (wordsArray != null) {
          for (j in 0 until wordsArray.length()) {
            val w = wordsArray.optString(j, "").trim()
            if (w.isNotBlank()) wordsList.add(w)
          }
        }
        val text = obj.optString("phraseText", "").ifBlank {
          obj.optString("text", "").ifBlank {
            wordsList.joinToString(" ")
          }
        }.trim()

        if (text.isNotBlank() || wordsList.isNotEmpty()) {
          val cleanWords = if (wordsList.isNotEmpty()) wordsList else text.split("\\s+".toRegex()).filter { it.isNotBlank() }
          var trans = obj.optString("spanishTranslation", "").ifBlank {
            obj.optString("translation", "")
          }.trim()
          if (trans.isBlank()) {
            trans = PhraseTranslator.translatePhrase(text)
          }

          list.add(
            SavedDraftPhrase(
              id = obj.optString("id", UUID.randomUUID().toString()).ifBlank { UUID.randomUUID().toString() },
              phraseText = text,
              words = cleanWords,
              spanishTranslation = trans,
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }
    } catch (_: Exception) {}
    return list
  }
}
