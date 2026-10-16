package com.example.data.translation

import com.example.data.gemini.GeminiChatService
import com.example.domain.translation.TranslationState
import com.example.domain.util.WordDictionary
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Gestor especializado en traducción de Español a Inglés de alta precisión.
 * Combina:
 * 1. Diccionario inverso instantáneo de vocabulario NGSL para respuesta inmediata.
 * 2. Motor de red neuronal en dispositivo Google ML Kit (Spanish -> English).
 * 3. Refinamiento pedagógico con Gemini IA cuando hay clave de API para frases complejas.
 */
class SpanishToEnglishTranslationManager(
  private val geminiChatService: GeminiChatService = GeminiChatService()
) {
  private val _translationState = MutableStateFlow<TranslationState>(TranslationState.Idle)
  val translationState: StateFlow<TranslationState> = _translationState.asStateFlow()

  private var isModelDownloaded = false
  private var isDownloading = false
  private var latestPendingText: String? = null
  private var activeTranslationJob: Job? = null

  private val translator: Translator by lazy {
    val options = TranslatorOptions.Builder()
      .setSourceLanguage(TranslateLanguage.SPANISH)
      .setTargetLanguage(TranslateLanguage.ENGLISH)
      .build()
    Translation.getClient(options)
  }

  fun translate(
    text: String,
    apiKey: String = "",
    scope: CoroutineScope
  ) {
    val clean = text.trim()
    if (clean.isBlank()) {
      latestPendingText = null
      activeTranslationJob?.cancel()
      _translationState.value = TranslationState.Idle
      return
    }

    latestPendingText = clean
    activeTranslationJob?.cancel()

    activeTranslationJob = scope.launch(Dispatchers.IO) {
      // 1. Traducción preliminar inmediata con diccionario inverso
      val dictTranslation = lookupInDictionary(clean)
      if (dictTranslation.isNotBlank()) {
        _translationState.value = TranslationState.Success(dictTranslation)
      } else {
        _translationState.value = TranslationState.Translating
      }

      // 2. Modelo neuronal offline de Google ML Kit (Spanish -> English)
      executeMlKitTranslation(clean, dictTranslation)

      // 3. Si se cuenta con API key de Gemini, refinar para lograr precisión casi perfecta
      val cleanKey = apiKey.trim()
      if (cleanKey.isNotBlank() && cleanKey != "your_gemini_api_key_here") {
        try {
          val prompt = "Translate the following Spanish text into natural, idiomatic, accurate English. " +
            "Respond ONLY with the direct English translation without explanations, quotes, or notes:\n$clean"
          val result = geminiChatService.askGemini(emptyList(), prompt, cleanKey)
          result.onSuccess { refined ->
            val cleanRefined = refined.trim().removeSurrounding("\"").removeSurrounding("'")
            if (latestPendingText == clean && cleanRefined.isNotBlank() && !cleanRefined.startsWith("⚠️")) {
              _translationState.value = TranslationState.Success(cleanRefined)
            }
          }
        } catch (_: Exception) {}
      }
    }
  }

  private fun executeMlKitTranslation(text: String, fallback: String) {
    try {
      if (!isModelDownloaded) {
        if (!isDownloading) {
          isDownloading = true
          _translationState.value = TranslationState.DownloadingModel
          val conditions = DownloadConditions.Builder().build()
          translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
              isModelDownloaded = true
              isDownloading = false
              if (text == latestPendingText) {
                runMlKitTranslate(text, fallback)
              }
            }
            .addOnFailureListener {
              isDownloading = false
              if (fallback.isNotBlank() && text == latestPendingText) {
                _translationState.value = TranslationState.Success(fallback)
              }
            }
        }
      } else {
        runMlKitTranslate(text, fallback)
      }
    } catch (_: Exception) {
      if (fallback.isNotBlank() && text == latestPendingText) {
        _translationState.value = TranslationState.Success(fallback)
      }
    }
  }

  private fun runMlKitTranslate(text: String, fallback: String) {
    translator.translate(text)
      .addOnSuccessListener { result ->
        if (text == latestPendingText) {
          val cleanResult = result.trim()
          if (cleanResult.isNotBlank()) {
            _translationState.value = TranslationState.Success(cleanResult)
          } else if (fallback.isNotBlank()) {
            _translationState.value = TranslationState.Success(fallback)
          }
        }
      }
      .addOnFailureListener {
        if (text == latestPendingText && fallback.isNotBlank()) {
          _translationState.value = TranslationState.Success(fallback)
        }
      }
  }

  private fun lookupInDictionary(text: String): String {
    val clean = text.lowercase(Locale.ROOT).trim()
    val reverseMap = WordDictionary.getReverseTranslations()
    val direct = reverseMap[clean]
    if (direct != null) return direct

    val tokens = clean.split("\\s+".toRegex()).filter { it.isNotBlank() }
    if (tokens.size > 1) {
      val translatedTokens = tokens.map { reverseMap[it] ?: it }
      if (translatedTokens != tokens) {
        return translatedTokens.joinToString(" ")
      }
    }
    return ""
  }

  fun reset() {
    latestPendingText = null
    activeTranslationJob?.cancel()
    _translationState.value = TranslationState.Idle
  }

  fun close() {
    try {
      activeTranslationJob?.cancel()
      translator.close()
    } catch (_: Exception) {}
  }
}
