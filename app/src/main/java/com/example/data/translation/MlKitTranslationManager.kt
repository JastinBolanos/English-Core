package com.example.data.translation

import com.example.domain.translation.TranslationManager
import com.example.domain.translation.TranslationState
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

    try {
      if (isDownloading) {
        _translationState.value = TranslationState.DownloadingModel
        return
      }

      if (!isModelDownloaded) {
        _translationState.value = TranslationState.DownloadingModel
        isDownloading = true
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
          .addOnSuccessListener {
            isModelDownloaded = true
            isDownloading = false
            val current = latestPendingText
            if (!current.isNullOrBlank()) {
              executeTranslation(current)
            } else {
              _translationState.value = TranslationState.Idle
            }
          }
          .addOnFailureListener { error ->
            isDownloading = false
            _translationState.value = TranslationState.Error(
              error.localizedMessage ?: "No se pudo descargar el modelo offline de ML Kit"
            )
          }
      } else {
        executeTranslation(clean)
      }
    } catch (e: Exception) {
      _translationState.value = TranslationState.Error(e.localizedMessage ?: "Error con el motor de traducción")
    }
  }

  private fun executeTranslation(text: String) {
    _translationState.value = TranslationState.Translating
    translator.translate(text)
      .addOnSuccessListener { translated ->
        if (text == latestPendingText) {
          _translationState.value = TranslationState.Success(translated)
        }
      }
      .addOnFailureListener { error ->
        if (text == latestPendingText) {
          _translationState.value = TranslationState.Error(
            error.localizedMessage ?: "Error al traducir la frase"
          )
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
