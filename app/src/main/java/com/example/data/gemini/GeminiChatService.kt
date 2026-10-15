package com.example.data.gemini

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

data class GeminiChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val text: String,
  val isFromUser: Boolean,
  val timestamp: Long = System.currentTimeMillis()
)

class GeminiChatService(
  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()
) {

  companion object {
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
  }

  suspend fun askGemini(
    history: List<GeminiChatMessage>,
    newPrompt: String,
    apiKey: String
  ): Result<String> = withContext(Dispatchers.IO) {
    val cleanKey = apiKey.trim()
    if (cleanKey.isBlank()) {
      return@withContext Result.failure(
        IllegalStateException("No se ha configurado la clave de API de Gemini. Configúrala en la app o en las variables de entorno.")
      )
    }

    try {
      val url = "$BASE_URL/$MODEL:generateContent?key=$cleanKey"

      val jsonBody = JSONObject().apply {
        // System instruction para contexto pedagógico de inglés
        put("systemInstruction", JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply {
              put(
                "text",
                "Eres un tutor y asistente experto en el aprendizaje de inglés para hispanohablantes. " +
                  "Responde de forma clara, directa, amigable y muy pedagógica en español. " +
                  "Explica el significado de las palabras, matices, errores comunes, reglas de pronunciación y da ejemplos breves en inglés con traducción al español."
              )
            })
          })
        })

        // Historial de conversación para mantener el contexto
        val contentsArray = JSONArray()

        // Filtrar y agregar turnos previos
        history.takeLast(10).forEach { message ->
          contentsArray.put(JSONObject().apply {
            put("role", if (message.isFromUser) "user" else "model")
            put("parts", JSONArray().apply {
              put(JSONObject().apply {
                put("text", message.text)
              })
            })
          })
        }

        // Agregar el nuevo mensaje del usuario
        contentsArray.put(JSONObject().apply {
          put("role", "user")
          put("parts", JSONArray().apply {
            put(JSONObject().apply {
              put("text", newPrompt)
            })
          })
        })

        put("contents", contentsArray)

        // Configuración de generación
        put("generationConfig", JSONObject().apply {
          put("temperature", 0.7)
          put("topP", 0.95)
        })
      }

      val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

      val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

      val response = okHttpClient.newCall(request).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        val errorMessage = try {
          val errorJson = JSONObject(responseString)
          val errorObj = errorJson.optJSONObject("error")
          errorObj?.optString("message", "Error HTTP ${response.code}")
            ?: "Error HTTP ${response.code}: ${response.message}"
        } catch (_: Exception) {
          "Error HTTP ${response.code}: ${response.message}"
        }
        return@withContext Result.failure(Exception(errorMessage))
      }

      val jsonResponse = JSONObject(responseString)
      val candidates = jsonResponse.optJSONArray("candidates")
      if (candidates == null || candidates.length() == 0) {
        return@withContext Result.failure(Exception("Gemini no devolvió ninguna respuesta."))
      }

      val firstCandidate = candidates.getJSONObject(0)
      val contentObj = firstCandidate.optJSONObject("content")
      val parts = contentObj?.optJSONArray("parts")
      val firstPart = parts?.optJSONObject(0)
      val textResult = firstPart?.optString("text", "") ?: ""

      if (textResult.isBlank()) {
        return@withContext Result.failure(Exception("Respuesta vacía recibida de Gemini."))
      }

      Result.success(textResult)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
