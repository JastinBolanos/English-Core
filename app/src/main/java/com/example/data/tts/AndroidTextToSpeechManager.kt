package com.example.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.domain.tts.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidTextToSpeechManager @Inject constructor(
  private val context: Context
) : TextToSpeechManager, TextToSpeech.OnInitListener {

  private val tag = "TTSManager"
  private var textToSpeech: TextToSpeech? = null
  private var isInitialized = false
  private var pendingSpeechText: String? = null

  val speedLevels = listOf(1.0f, 0.8f, 0.5f)
  private val _currentSpeechRate = MutableStateFlow(1.0f)
  override val currentSpeechRate: StateFlow<Float> = _currentSpeechRate.asStateFlow()

  init {
    initTts()
  }

  private fun initTts() {
    try {
      textToSpeech = TextToSpeech(context.applicationContext, this)
    } catch (e: Exception) {
      Log.e(tag, "Failed to initialize native TextToSpeech: ${e.message}")
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val tts = textToSpeech ?: return
      val result = tts.setLanguage(Locale.US)
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        Log.w(tag, "English (US) not supported or missing data, trying Locale.UK")
        tts.setLanguage(Locale.UK)
      }
      tts.setSpeechRate(_currentSpeechRate.value)
      tts.setPitch(1.0f)
      isInitialized = true

      pendingSpeechText?.let { text ->
        speak(text)
        pendingSpeechText = null
      }
    } else {
      Log.e(tag, "TextToSpeech init failed with status: $status")
    }
  }

  override fun cycleSpeechRate(): Float {
    val currentIndex = speedLevels.indexOf(_currentSpeechRate.value)
    val nextIndex = if (currentIndex in speedLevels.indices) {
      (currentIndex + 1) % speedLevels.size
    } else {
      0
    }
    val nextSpeed = speedLevels[nextIndex]
    setSpeechRate(nextSpeed)
    return nextSpeed
  }

  override fun setSpeechRate(rate: Float) {
    _currentSpeechRate.value = rate
    try {
      textToSpeech?.setSpeechRate(rate)
    } catch (e: Exception) {
      Log.e(tag, "Error setting TTS rate: ${e.message}")
    }
  }

  override fun speak(text: String) {
    if (text.isBlank()) return

    if (!isInitialized) {
      pendingSpeechText = text
      return
    }

    try {
      textToSpeech?.setSpeechRate(_currentSpeechRate.value)
      textToSpeech?.speak(
        text,
        TextToSpeech.QUEUE_FLUSH,
        null,
        "utterance_${System.currentTimeMillis()}"
      )
    } catch (e: Exception) {
      Log.e(tag, "Error speaking text: ${e.message}")
    }
  }

  override fun stop() {
    try {
      textToSpeech?.stop()
    } catch (e: Exception) {
      Log.e(tag, "Error stopping TTS: ${e.message}")
    }
  }

  override fun shutdown() {
    try {
      textToSpeech?.stop()
      textToSpeech?.shutdown()
      textToSpeech = null
      isInitialized = false
    } catch (e: Exception) {
      Log.e(tag, "Error shutting down TTS: ${e.message}")
    }
  }
}

