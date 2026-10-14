package com.example.domain.tts

import kotlinx.coroutines.flow.StateFlow

interface TextToSpeechManager {
  val currentSpeechRate: StateFlow<Float>
  fun speak(text: String)
  fun stop()
  fun shutdown()
  fun cycleSpeechRate(): Float
  fun setSpeechRate(rate: Float)
}

