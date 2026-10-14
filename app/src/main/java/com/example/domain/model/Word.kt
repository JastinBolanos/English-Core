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
  val secondaryPartOfSpeech: PartOfSpeech? = null
) {
  val nextExpectedCategories: List<PartOfSpeech>
    get() = partOfSpeech.nextExpectedCategories

  val connectionRuleNote: String
    get() = overrideConnectionNote ?: partOfSpeech.connectionRuleNote

  val displaySpanishTranslation: String
    get() = if (spanishTranslation.isNotBlank()) spanishTranslation else com.example.domain.util.WordDictionary.getTranslation(lemma, partOfSpeech)

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

  fun matchesCategory(category: PartOfSpeech): Boolean {
    if (category.isConnector && (partOfSpeech.isConnector || secondaryPartOfSpeech?.isConnector == true || rawPartOfSpeech.contains("conector", ignoreCase = true))) {
      return true
    }
    if (category == PartOfSpeech.PRONOUN && (partOfSpeech == PartOfSpeech.PRONOUN || secondaryPartOfSpeech == PartOfSpeech.PRONOUN || rawPartOfSpeech.contains("sujeto", ignoreCase = true) || rawPartOfSpeech.contains("pronombre", ignoreCase = true))) {
      return true
    }
    if (category == PartOfSpeech.VERB && (partOfSpeech == PartOfSpeech.VERB || secondaryPartOfSpeech == PartOfSpeech.VERB || rawPartOfSpeech.contains("verbo", ignoreCase = true))) {
      return true
    }
    if (category == PartOfSpeech.NOUN && (partOfSpeech == PartOfSpeech.NOUN || secondaryPartOfSpeech == PartOfSpeech.NOUN || rawPartOfSpeech.contains("sustantivo", ignoreCase = true))) {
      return true
    }
    if (category == PartOfSpeech.MODIFIER && (partOfSpeech == PartOfSpeech.MODIFIER || secondaryPartOfSpeech == PartOfSpeech.MODIFIER || rawPartOfSpeech.contains("modificador", ignoreCase = true) || rawPartOfSpeech.contains("adjetivo", ignoreCase = true) || rawPartOfSpeech.contains("adverbio", ignoreCase = true) || rawPartOfSpeech.contains("artículo", ignoreCase = true))) {
      return true
    }
    return partOfSpeech == category || secondaryPartOfSpeech == category
  }
}
