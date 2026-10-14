package com.example.di

import android.content.Context
import com.example.data.stt.SpeechToTextManager
import javax.inject.Singleton

/**
 * Proveedor de instancia singleton para SpeechToTextManager.
 */
object SpeechToTextModule {

  @Volatile
  private var instance: SpeechToTextManager? = null

  @Singleton
  fun provideSpeechToTextManager(context: Context): SpeechToTextManager {
    return instance ?: synchronized(this) {
      instance ?: SpeechToTextManager(context.applicationContext).also { instance = it }
    }
  }
}
