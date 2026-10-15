package com.example.domain.translation

import kotlinx.coroutines.flow.StateFlow

interface TranslationManager {
  val translationState: StateFlow<TranslationState>
  fun translate(text: String)
  fun reset()
  fun close()
}
