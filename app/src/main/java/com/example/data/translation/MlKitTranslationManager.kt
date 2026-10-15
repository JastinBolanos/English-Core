package com.example.data.translation

import com.example.domain.translation.TranslationManager
import com.example.domain.translation.TranslationState
import com.example.domain.util.PhraseTranslator
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MlKitTranslationManager : TranslationManager {
  private val _translationState = MutableStateFlow<TranslationState>(TranslationState.Idle)
  override val translationState: StateFlow<TranslationState> = _translationState.asStateFlow()

  private var isModelDownloaded = false
  private var isDownloading = false
  private var latestPendingText: String? = null

  private val translator: Translator by lazy {
    val options = TranslatorOptions.Builder()
      .setSourceLanguage(TranslateLanguage.ENGLISH)
      .setTargetLanguage(TranslateLanguage.SPANISH)
      .build()
    Translation.getClient(options)
  }

  override fun translate(text: String) {
    val clean = text.trim()
    if (clean.isEmpty()) {
      latestPendingText = null
      _translationState.value = TranslationState.Idle
      return
    }

    latestPendingText = clean

    // 1. Obtener de inmediato la traducción completa pedagógica de la frase entera
    val phraseFallback = PhraseTranslator.translatePhrase(clean)
    if (phraseFallback.isNotBlank()) {
      _translationState.value = TranslationState.Success(phraseFallback)
    } else {
      _translationState.value = TranslationState.Translating
    }

    // 2. Intentar refinamiento mediante el modelo ML Kit si está disponible
    try {
      if (!isModelDownloaded) {
        if (!isDownloading) {
          isDownloading = true
          val conditions = DownloadConditions.Builder().build()
          translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
              isModelDownloaded = true
              isDownloading = false
              val current = latestPendingText
              if (!current.isNullOrBlank()) {
                val currentFallback = PhraseTranslator.translatePhrase(current)
                executeTranslation(current, currentFallback)
              }
            }
            .addOnFailureListener {
              isDownloading = false
              // Si falla la descarga de ML Kit, conservamos la traducción completa de PhraseTranslator
              if (phraseFallback.isNotBlank()) {
                _translationState.value = TranslationState.Success(phraseFallback)
              }
            }
        }
      } else {
        executeTranslation(clean, phraseFallback)
      }
    } catch (_: Exception) {
      if (phraseFallback.isNotBlank()) {
        _translationState.value = TranslationState.Success(phraseFallback)
      }
    }
  }

  private fun executeTranslation(text: String, fallback: String) {
    translator.translate(text)
      .addOnSuccessListener { translated ->
        if (text == latestPendingText) {
          val cleanTranslated = translated.trim()
          val inputWordsCount = text.split("\\s+".toRegex()).count { it.isNotBlank() }
          val outputWordsCount = cleanTranslated.split("\\s+".toRegex()).count { it.isNotBlank() }

          // Si la frase de entrada contiene 2 o más palabras pero ML Kit devolvió solo 1 palabra,
          // significa que truncó la frase (e.g. tradujo solo 'be' en 'be of a ...').
          // En ese caso preservamos la traducción completa de toda la frase.
          if (inputWordsCount >= 2 && outputWordsCount < 2 && fallback.isNotBlank()) {
            _translationState.value = TranslationState.Success(fallback)
          } else if (cleanTranslated.isNotBlank()) {
            _translationState.value = TranslationState.Success(cleanTranslated)
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

  override fun reset() {
    latestPendingText = null
    _translationState.value = TranslationState.Idle
  }

  override fun close() {
    try {
      translator.close()
    } catch (_: Exception) {}
  }
}
