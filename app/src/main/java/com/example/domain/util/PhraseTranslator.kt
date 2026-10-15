package com.example.domain.util

import com.example.domain.model.PartOfSpeech
import java.util.Locale

/**
 * Motor pedagógico de traducción completa de frases en tiempo real.
 * Garantiza que frases de 1, 2, 3, 4 o más palabras armadas en el Lienzo de Pruebas
 * se traduzcan de forma íntegra al español, evitando que el motor se quede estancado
 * traduciendo únicamente la primera palabra.
 */
object PhraseTranslator {

  // Vocabulario esencial y conectores traducidos contextualmente para frases
  private val curatedPhraseWords = mapOf(
    // Pronombres
    "i" to "yo",
    "you" to "tú",
    "he" to "él",
    "she" to "ella",
    "it" to "eso",
    "we" to "nosotros",
    "they" to "ellos",
    "me" to "mí",
    "him" to "él",
    "her" to "ella",
    "us" to "nosotros",
    "them" to "ellos",
    "my" to "mi",
    "your" to "tu",
    "his" to "su",
    "their" to "su",
    "our" to "nuestro",

    // Verbos esenciales y auxiliares
    "be" to "ser",
    "am" to "soy",
    "is" to "es",
    "are" to "son",
    "was" to "era",
    "were" to "eran",
    "been" to "sido",
    "being" to "siendo",
    "have" to "tener",
    "has" to "tiene",
    "had" to "tenía",
    "having" to "teniendo",
    "do" to "hacer",
    "does" to "hace",
    "did" to "hizo",
    "doing" to "haciendo",
    "done" to "hecho",
    "will" to "va a",
    "would" to "haría",
    "can" to "puede",
    "could" to "podría",
    "should" to "debería",
    "must" to "debe",
    "may" to "puede",
    "might" to "podría",

    // Artículos y Preposiciones clave
    "the" to "el",
    "a" to "un",
    "an" to "un",
    "of" to "de",
    "in" to "en",
    "to" to "a",
    "for" to "para",
    "with" to "con",
    "on" to "en",
    "at" to "en",
    "by" to "por",
    "from" to "de",
    "up" to "arriba",
    "about" to "sobre",
    "into" to "en",
    "over" to "sobre",
    "after" to "después de",
    "before" to "antes de",
    "under" to "bajo",
    "between" to "entre",
    "through" to "a través de",

    // Conectores y partículas
    "and" to "y",
    "or" to "o",
    "but" to "pero",
    "because" to "porque",
    "if" to "si",
    "so" to "así que",
    "that" to "que",
    "this" to "este",
    "these" to "estos",
    "those" to "esos",
    "not" to "no",
    "no" to "no",
    "as" to "como",
    "than" to "que",
    "more" to "más",
    "all" to "todo",
    "some" to "algunos",
    "any" to "cualquier",
    "very" to "muy",
    "now" to "ahora",
    "then" to "entonces",
    "here" to "aquí",
    "there" to "allí",
    "only" to "sólo",
    "just" to "sólo",
    "like" to "como",
    "also" to "también"
  )

  /**
   * Traduce la frase completa palabra por palabra de manera coherente,
   * respetando concordancias gramaticales básicas (ej: de + el = del, a + el = al,
   * artículo femenino ante sustantivos femeninos en español).
   */
  fun translatePhrase(phrase: String): String {
    val clean = phrase.trim()
    if (clean.isBlank()) return ""

    val rawWords = clean.split("\\s+".toRegex()).filter { it.isNotBlank() }
    if (rawWords.isEmpty()) return ""

    val translatedTokens = ArrayList<String>(rawWords.size)

    for (i in rawWords.indices) {
      val word = rawWords[i]
      val nextWord = rawWords.getOrNull(i + 1)
      val translation = translateWordInContext(word, nextWord)
      translatedTokens.add(translation)
    }

    return formatSpanishTokens(translatedTokens)
  }

  private fun translateWordInContext(word: String, nextWord: String?): String {
    val cleanWord = word.lowercase(Locale.ROOT).trim()

    // 1. Reglas de conectores pedagógicos
    val connectorRule = ConnectorRulesCatalog.getRule(cleanWord)
    if (connectorRule != null) {
      return cleanMeaning(connectorRule.spanishTranslation)
    }

    // 2. Concordancia especial para artículos según la siguiente palabra
    if (cleanWord == "the") {
      if (nextWord != null && isLikelyFeminineWord(nextWord)) {
        return "la"
      }
      return "el"
    }

    if (cleanWord == "a" || cleanWord == "an") {
      if (nextWord != null && isLikelyFeminineWord(nextWord)) {
        return "una"
      }
      return "un"
    }

    // 3. Diccionario curado contextual para frases
    val curated = curatedPhraseWords[cleanWord]
    if (curated != null) {
      return curated
    }

    // 4. Diccionario completo de 2809 palabras ngsl_translations
    val general = WordDictionary.getTranslation(cleanWord, PartOfSpeech.OTHER)
    if (general.isNotBlank() && !general.equals(cleanWord, ignoreCase = true)) {
      return cleanMeaning(general)
    }

    return cleanWord
  }

  private fun isLikelyFeminineWord(englishWord: String): Boolean {
    val clean = englishWord.lowercase(Locale.ROOT).trim()
    val trans = cleanMeaning(WordDictionary.getTranslation(clean, PartOfSpeech.OTHER))
    val lowerTrans = trans.lowercase(Locale.ROOT)
    // Palabras en español que terminan en 'a', 'ción', 'sión', 'dad', 'tad', 'tud' suelen ser femeninas
    return lowerTrans.endsWith("a") ||
      lowerTrans.endsWith("ción") ||
      lowerTrans.endsWith("sión") ||
      lowerTrans.endsWith("dad") ||
      lowerTrans.endsWith("tad") ||
      lowerTrans.endsWith("tud")
  }

  private fun cleanMeaning(raw: String): String {
    val withoutParens = raw.replace(Regex("\\(.*?\\)"), "").trim()
    val firstOption = withoutParens.split(",").firstOrNull()?.trim() ?: withoutParens
    return firstOption.split("/").firstOrNull()?.trim() ?: firstOption
  }

  private fun formatSpanishTokens(tokens: List<String>): String {
    if (tokens.isEmpty()) return ""

    val result = ArrayList<String>()
    var i = 0
    while (i < tokens.size) {
      val current = tokens[i]
      val next = tokens.getOrNull(i + 1)

      // Regla de contracción en español: de + el = del
      if (current.equals("de", ignoreCase = true) && next.equals("el", ignoreCase = true)) {
        result.add("del")
        i += 2
        continue
      }

      // Regla de contracción en español: a + el = al
      if (current.equals("a", ignoreCase = true) && next.equals("el", ignoreCase = true)) {
        result.add("al")
        i += 2
        continue
      }

      result.add(current)
      i++
    }

    return result.joinToString(" ")
  }
}
