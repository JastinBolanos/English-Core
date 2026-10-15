package com.example.domain.translation

sealed interface TranslationState {
  object Idle : TranslationState
  object DownloadingModel : TranslationState
  object Translating : TranslationState
  data class Success(val translation: String) : TranslationState
  data class Error(val message: String) : TranslationState
}
