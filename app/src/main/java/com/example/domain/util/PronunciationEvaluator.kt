package com.example.domain.util

/**
 * Resultado de evaluación para una palabra individual pronunciada.
 */
data class SpokenWordMatch(
  val word: String,
  val isMatch: Boolean,
  val expectedWord: String? = null
)

/**
 * Evaluación completa de la pronunciación de la frase armada en el Lienzo de Pruebas.
 */
data class PronunciationEvaluation(
  val spokenText: String,
  val targetWords: List<String>,
  val wordMatches: List<SpokenWordMatch>,
  val accuracyPercentage: Int,
  val isExactMatch: Boolean,
  val feedbackMessage: String,
  val missingWordsCount: Int = 0
)

object PronunciationEvaluator {

  /**
   * Limpia puntuaciones y normaliza para comparación justa sin distinguir mayúsculas ni signos.
   */
  fun normalizeWord(raw: String): String {
    return raw.lowercase()
      .trim()
      .replace(Regex("[^a-z0-9']"), "")
  }

  /**
   * Compara palabra por palabra lo capturado por el motor de voz contra las palabras del borrador.
   */
  fun evaluate(targetWords: List<String>, spokenText: String): PronunciationEvaluation {
    val cleanSpoken = spokenText.trim()
    if (cleanSpoken.isBlank() || targetWords.isEmpty()) {
      return PronunciationEvaluation(
        spokenText = spokenText,
        targetWords = targetWords,
        wordMatches = emptyList(),
        accuracyPercentage = 0,
        isExactMatch = false,
        feedbackMessage = "No se detectaron palabras audibles."
      )
    }

    val spokenTokens = cleanSpoken.split(Regex("\\s+")).filter { it.isNotBlank() }

    val matches = mutableListOf<SpokenWordMatch>()
    var exactMatchesCount = 0

    spokenTokens.forEachIndexed { index, token ->
      val targetExpected = targetWords.getOrNull(index)
      val isMatch = if (targetExpected != null) {
        normalizeWord(token) == normalizeWord(targetExpected)
      } else {
        false
      }

      if (isMatch) {
        exactMatchesCount++
      }

      matches.add(
        SpokenWordMatch(
          word = token,
          isMatch = isMatch,
          expectedWord = if (!isMatch) targetExpected else null
        )
      )
    }

    val missingCount = maxOf(0, targetWords.size - spokenTokens.size)
    val maxLen = maxOf(targetWords.size, spokenTokens.size)
    val percentage = if (maxLen > 0) {
      ((exactMatchesCount.toFloat() / maxLen) * 100).toInt().coerceIn(0, 100)
    } else {
      0
    }

    val isExact = exactMatchesCount == targetWords.size && spokenTokens.size == targetWords.size

    val feedback = when {
      isExact -> "✅ ¡Pronunciación exacta! (100%)"
      percentage >= 70 -> "⚠️ Casi ($percentage%), revisa las palabras en rojo"
      percentage > 0 -> "⚠️ Revisa las palabras en rojo ($percentage%)"
      else -> "⚠️ Pronunciación diferente (0%), inténtalo de nuevo"
    }

    return PronunciationEvaluation(
      spokenText = cleanSpoken,
      targetWords = targetWords,
      wordMatches = matches,
      accuracyPercentage = percentage,
      isExactMatch = isExact,
      feedbackMessage = feedback,
      missingWordsCount = missingCount
    )
  }
}
