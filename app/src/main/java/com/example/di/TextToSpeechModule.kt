package com.example.di

import android.content.Context
import com.example.data.tts.TtsManager
import com.example.domain.tts.TextToSpeechManager
import javax.inject.Singleton

/**
 * Módulo de inyección Hilt para el singleton global de Text-to-Speech (TTS) nativo 100% offline.
 *
 * Configuración Hilt estándar:
 * ```kotlin
 * @Module
 * @InstallIn(SingletonComponent::class)
 * object TtsModule {
 *
 *   @Provides
 *   @Singleton
 *   fun provideTtsManager(
 *     @ApplicationContext context: Context
 *   ): TtsManager {
 *     return TtsManager(context)
 *   }
 * }
 * ```
 */
object TtsModule {

  @Volatile
  private var instance: TtsManager? = null

  @Singleton
  fun provideTtsManager(context: Context): TtsManager {
    return instance ?: synchronized(this) {
      instance ?: TtsManager(context.applicationContext).also { instance = it }
    }
  }

  @Singleton
  fun provideTextToSpeechManager(context: Context): TextToSpeechManager {
    return provideTtsManager(context)
  }
}

// Backward-compatible alias
typealias TextToSpeechModule = TtsModule
