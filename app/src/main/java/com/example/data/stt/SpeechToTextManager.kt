package com.example.data.stt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estados del motor de reconocimiento de voz.
 */
sealed class SpeechRecognitionState {
  object Idle : SpeechRecognitionState()
  object Listening : SpeechRecognitionState()
  data class Results(val text: String) : SpeechRecognitionState()
  data class Error(val message: String, val errorCode: Int) : SpeechRecognitionState()
}

/**
 * Motor nativo de reconocimiento de voz (Speech-to-Text) de Android.
 * Utiliza SpeechRecognizer directamente en segundo plano para evitar ventanas emergentes externas
 * y mantener la experiencia fluida dentro de la UI oscura del Lienzo de Pruebas.
 */
@Singleton
class SpeechToTextManager @Inject constructor(
  private val context: Context
) {

  private val tag = "SpeechToTextManager"
  private val mainHandler = Handler(Looper.getMainLooper())

  private var speechRecognizer: SpeechRecognizer? = null

  private val _state = MutableStateFlow<SpeechRecognitionState>(SpeechRecognitionState.Idle)
  val state: StateFlow<SpeechRecognitionState> = _state.asStateFlow()

  private val _partialText = MutableStateFlow("")
  val partialText: StateFlow<String> = _partialText.asStateFlow()

  private val _rmsVolume = MutableStateFlow(0f)
  val rmsVolume: StateFlow<Float> = _rmsVolume.asStateFlow()

  private val recognitionListener = object : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) {
      Log.d(tag, "onReadyForSpeech: El micrófono está listo y escuchando.")
      _state.value = SpeechRecognitionState.Listening
    }

    override fun onBeginningOfSpeech() {
      Log.d(tag, "onBeginningOfSpeech: Detección de voz iniciada.")
      _state.value = SpeechRecognitionState.Listening
    }

    override fun onRmsChanged(rmsdB: Float) {
      _rmsVolume.value = rmsdB
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
      Log.d(tag, "onEndOfSpeech: Detección de voz finalizada.")
    }

    override fun onError(error: Int) {
      Log.w(tag, "onError: Código $error")
      val userMessage = when (error) {
        SpeechRecognizer.ERROR_AUDIO -> "Error al capturar audio del micrófono."
        SpeechRecognizer.ERROR_CLIENT -> "Error del cliente de reconocimiento."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Se requiere permiso para grabar audio."
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Error de red o sin conexión."
        SpeechRecognizer.ERROR_NO_MATCH -> "No se detectaron palabras claras. Intenta de nuevo."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "El reconocedor de voz está ocupado."
        SpeechRecognizer.ERROR_SERVER -> "Error en el servicio de reconocimiento."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo agotado sin detectar audio."
        else -> "No se pudo reconocer la pronunciación."
      }
      _state.value = SpeechRecognitionState.Error(userMessage, error)
      _rmsVolume.value = 0f
    }

    override fun onResults(results: Bundle?) {
      val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      val text = matches?.firstOrNull()?.trim() ?: ""
      Log.d(tag, "onResults: Transcripción obtenida: '$text'")
      if (text.isNotBlank()) {
        _state.value = SpeechRecognitionState.Results(text)
      } else {
        _state.value = SpeechRecognitionState.Error("No se detectó audio reconocible.", SpeechRecognizer.ERROR_NO_MATCH)
      }
      _rmsVolume.value = 0f
    }

    override fun onPartialResults(partialResults: Bundle?) {
      val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      val partial = matches?.firstOrNull()?.trim() ?: ""
      if (partial.isNotBlank()) {
        _partialText.value = partial
      }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
  }

  /**
   * Inicia la captura de audio en inglés priorizando el motor offline.
   */
  fun startListening() {
    mainHandler.post {
      try {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
          _state.value = SpeechRecognitionState.Error(
            "El reconocimiento de voz no está habilitado en este dispositivo.",
            -1
          )
          return@post
        }

        cleanupRecognizer()

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext).also {
          it.setRecognitionListener(recognitionListener)
          speechRecognizer = it
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
          putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US")
          putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
          putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
          putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        _partialText.value = ""
        _state.value = SpeechRecognitionState.Listening
        recognizer.startListening(intent)
      } catch (e: Exception) {
        Log.e(tag, "Error al iniciar SpeechRecognizer: ${e.message}")
        _state.value = SpeechRecognitionState.Error(
          "Error al activar micrófono: ${e.localizedMessage ?: "desconocido"}",
          -1
        )
      }
    }
  }

  /**
   * Detiene la escucha activa.
   */
  fun stopListening() {
    mainHandler.post {
      cleanupRecognizer()
      if (_state.value is SpeechRecognitionState.Listening) {
        _state.value = SpeechRecognitionState.Idle
      }
    }
  }

  /**
   * Limpia y reinicia el estado a Idle.
   */
  fun reset() {
    mainHandler.post {
      cleanupRecognizer()
      _state.value = SpeechRecognitionState.Idle
      _partialText.value = ""
      _rmsVolume.value = 0f
    }
  }

  private fun cleanupRecognizer() {
    try {
      speechRecognizer?.stopListening()
      speechRecognizer?.cancel()
      speechRecognizer?.destroy()
    } catch (e: Exception) {
      Log.e(tag, "Excepción al limpiar SpeechRecognizer: ${e.message}")
    } finally {
      speechRecognizer = null
      _rmsVolume.value = 0f
    }
  }

  fun destroy() {
    mainHandler.post {
      cleanupRecognizer()
      _state.value = SpeechRecognitionState.Idle
    }
  }
}
