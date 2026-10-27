package com.example.domain.model

data class Word(
  val lemma: String,
  val partOfSpeech: PartOfSpeech = PartOfSpeech.OTHER,
  val id: Long = 0,
  val rank: Int = 0,
  val rawPartOfSpeech: String = "",
  val spanishTranslation: String = "",
  val microExample: String = "",
  val interval: Int = 0,
  val easeFactor: Float = 2.5f,
  val nextReviewDate: Long = 0L,
  val overrideConnectionNote: String? = null,
  val useCount: Int = 0,
  val secondaryPartOfSpeech: PartOfSpeech? = null,
  val subCategory: String? = null
) {
  val subcategoria: String?
    get() = resolvedSubCategory

  val resolvedSubCategory: String?
    get() = subCategory ?: com.example.domain.util.WordSubcategoryCatalog.getSubcategory(lemma, rank)

  fun getSubCategoryFor(category: String?): String? {
    return subCategory ?: com.example.domain.util.WordSubcategoryCatalog.getSubcategory(lemma, rank, category)
  }

  val nextExpectedCategories: List<PartOfSpeech>
    get() = getPrimaryCategory().nextExpectedCategories

  val connectionRuleNote: String
    get() = overrideConnectionNote ?: getPrimaryCategory().connectionRuleNote

  val displaySpanishTranslation: String
    get() = if (spanishTranslation.isNotBlank()) spanishTranslation else com.example.domain.util.WordDictionary.getTranslation(lemma, getPrimaryCategory())

  val displayCategoryLabel: String
    get() {
      if (rawPartOfSpeech.isNotBlank()) {
        return rawPartOfSpeech
      }
      if (secondaryPartOfSpeech != null && secondaryPartOfSpeech != partOfSpeech) {
        return "${partOfSpeech.displayName} / ${secondaryPartOfSpeech.displayName}"
      }
      return partOfSpeech.displayName
    }

  /**
   * Determina estrictamente la categoría principal evaluando ÚNICAMENTE
   * la primera parte antes de la primera barra (/):
   * - "conector" -> Conector (Amarillo)
   * - "sujeto" o "pronombre" -> Sujeto (Morado)
   * - "verbo" -> Verbo (Azul)
   * - "sustantivo" -> Sustantivo (Verde)
   * - "modificador", "adverbio", "interjección", etc. -> Modificador (Blanco)
   */
  fun getPrimaryCategory(): PartOfSpeech {
    val classification = if (rawPartOfSpeech.isNotBlank()) rawPartOfSpeech else partOfSpeech.displayName
    val clean = classification.lowercase().trim()
    val firstPart = clean.substringBefore("/").trim()
    return when {
      firstPart.startsWith("conector") -> {
        if (partOfSpeech.isConnector) partOfSpeech else PartOfSpeech.CONNECTOR
      }
      firstPart.startsWith("sujeto") || firstPart.startsWith("pronombre") -> PartOfSpeech.PRONOUN
      firstPart.startsWith("verbo") -> PartOfSpeech.VERB
      firstPart.startsWith("sustantivo") -> PartOfSpeech.NOUN
      firstPart.startsWith("modificador") || firstPart.startsWith("adverbio") ||
      firstPart.startsWith("interjección") || firstPart.startsWith("interjeccion") ||
      firstPart.startsWith("artículo") || firstPart.startsWith("articulo") ||
      firstPart.startsWith("adjetivo") -> PartOfSpeech.MODIFIER
      else -> {
        when {
          partOfSpeech.isConnector -> partOfSpeech
          partOfSpeech == PartOfSpeech.PRONOUN -> PartOfSpeech.PRONOUN
          partOfSpeech == PartOfSpeech.VERB -> PartOfSpeech.VERB
          partOfSpeech == PartOfSpeech.NOUN -> PartOfSpeech.NOUN
          else -> PartOfSpeech.MODIFIER
        }
      }
    }
  }

  /**
   * Filtrado puro contra la categoría principal.
   * Evita que palabras con tipo secundario (ej. modificador (...)/conector)
   * se filtren erróneamente en Conector.
   */
  fun matchesCategory(category: PartOfSpeech): Boolean {
    val primary = getPrimaryCategory()
    return if (category.isConnector) {
      primary.isConnector
    } else {
      primary == category
    }
  }
}
