package com.example.domain.util

import android.content.Context
import com.example.R
import com.example.domain.model.PartOfSpeech
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Catálogo maestro de tipos gramaticales para las 2,809 palabras del NGSL.
 * Actualizado en bloques controlados para preservar la precisión pedagógica.
 */
object WordGrammarCatalog {

  private val grammarTypes = ConcurrentHashMap<String, String>()

  init {
    loadBundledResource()
  }

  private fun loadBundledResource() {
    try {
      val stream = WordGrammarCatalog::class.java.classLoader?.getResourceAsStream("res/raw/ngsl_grammatical_types.json")
        ?: WordGrammarCatalog::class.java.getResourceAsStream("/raw/ngsl_grammatical_types.json")
      if (stream != null) {
        val reader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
        val jsonText = reader.readText()
        reader.close()
        parseAndStoreJson(jsonText)
      }
    } catch (_: Exception) {}
  }

  fun loadGrammarTypesFromContext(context: Context) {
    if (grammarTypes.size >= 2811) return
    try {
      val inputStream = context.resources.openRawResource(R.raw.ngsl_grammatical_types)
      val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
      val jsonText = reader.readText()
      reader.close()
      parseAndStoreJson(jsonText)
    } catch (_: Exception) {}
  }

  private fun parseAndStoreJson(jsonString: String) {
    try {
      val jsonObject = JSONObject(jsonString)
      val keys = jsonObject.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.optString(key, "")
        if (value.isNotBlank()) {
          grammarTypes[key.lowercase(Locale.ROOT).trim()] = value
        }
      }
    } catch (_: Exception) {}
  }

  fun injectGrammarTypes(map: Map<String, String>) {
    grammarTypes.putAll(map)
  }

  fun getGrammarType(lemma: String): String? {
    return grammarTypes[lemma.lowercase(Locale.ROOT).trim()]
  }

  fun hasGrammarType(lemma: String): Boolean {
    return grammarTypes.containsKey(lemma.lowercase(Locale.ROOT).trim())
  }

  fun getLoadedCount(): Int = grammarTypes.size

  fun getAllGrammarTypes(): Map<String, String> = grammarTypes

  /**
   * Mapea el texto del tipo gramatical (ej. "modificador (artículo)", "verbo/sustantivo")
   * a las categorías correspondientes de [PartOfSpeech] para el motor de colores y reglas sintácticas.
   */
  fun getPrimaryCategory(rawType: String, lemma: String = ""): PartOfSpeech {
    val cleanLemma = lemma.lowercase(Locale.ROOT).trim()
    val connectorRule = if (cleanLemma.isNotBlank()) ConnectorRulesCatalog.getRule(cleanLemma) else null

    val clean = rawType.lowercase(Locale.ROOT).trim()
    val firstPart = clean.substringBefore("/").trim()

    return when {
      firstPart.startsWith("conector") -> connectorRule?.category ?: PartOfSpeech.CONNECTOR
      firstPart.startsWith("sujeto") || firstPart.startsWith("pronombre") -> PartOfSpeech.PRONOUN
      firstPart.startsWith("verbo") -> PartOfSpeech.VERB
      firstPart.startsWith("sustantivo") -> PartOfSpeech.NOUN
      firstPart.startsWith("modificador") || firstPart.startsWith("adjetivo") ||
      firstPart.startsWith("adverbio") || firstPart.startsWith("artículo") ||
      firstPart.startsWith("articulo") || firstPart.startsWith("interjección") ||
      firstPart.startsWith("interjeccion") -> PartOfSpeech.MODIFIER
      else -> PartOfSpeech.OTHER
    }
  }

  /**
   * Mapea el texto del tipo gramatical (ej. "modificador (artículo)", "verbo/sustantivo")
   * a las categorías correspondientes de [PartOfSpeech] para el motor de colores y reglas sintácticas.
   */
  fun parseCategories(rawType: String, lemma: String): Pair<PartOfSpeech, PartOfSpeech?> {
    val cleanLemma = lemma.lowercase(Locale.ROOT).trim()
    val connectorRule = ConnectorRulesCatalog.getRule(cleanLemma)

    val parts = rawType.split("/").map { it.trim().lowercase(Locale.ROOT) }
    val primaryText = parts.firstOrNull() ?: ""
    val secondaryText = parts.getOrNull(1)

    fun toCategory(text: String): PartOfSpeech {
      val clean = text.trim().lowercase(Locale.ROOT)
      return when {
        clean.startsWith("conector") -> connectorRule?.category ?: PartOfSpeech.CONNECTOR
        clean.startsWith("sujeto") || clean.startsWith("pronombre") -> PartOfSpeech.PRONOUN
        clean.startsWith("verbo") -> PartOfSpeech.VERB
        clean.startsWith("sustantivo") -> PartOfSpeech.NOUN
        clean.startsWith("modificador") || clean.startsWith("adjetivo") ||
        clean.startsWith("adverbio") || clean.startsWith("artículo") ||
        clean.startsWith("articulo") || clean.startsWith("interjección") ||
        clean.startsWith("interjeccion") -> PartOfSpeech.MODIFIER
        else -> PartOfSpeech.OTHER
      }
    }

    val primary = toCategory(primaryText)
    val secondary = secondaryText?.let { toCategory(it) }?.takeIf { it != primary }

    return Pair(primary, secondary)
  }
}
