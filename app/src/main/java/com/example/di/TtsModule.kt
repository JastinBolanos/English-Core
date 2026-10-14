package com.example.di

import android.content.Context
import com.example.data.tts.AndroidTextToSpeechManager
import com.example.domain.tts.TextToSpeechManager
import javax.inject.Singleton

object TtsModule {
  @Volatile
  private var instance: TextToSpeechManager? = null

  @Singleton
  fun provideTtsManager(context: Context): TextToSpeechManager {
    return instance ?: synchronized(this) {
      instance ?: AndroidTextToSpeechManager(context.applicationContext).also { instance = it }
    }
  }
}
