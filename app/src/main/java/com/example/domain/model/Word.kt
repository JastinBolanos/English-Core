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
      if (secondaryPartOfSpeech != null && secondaryPartOfSpeech != partOfSpeech) {
        return "${partOfSpeech.displayName} / ${secondaryPartOfSpeech.displayName}"
      }
      return partOfSpeech.displayName
    }

  fun matchesCategory(category: PartOfSpeech): Boolean {
    if (category.isConnector && (partOfSpeech.isConnector || secondaryPartOfSpeech?.isConnector == true)) {
      return true
    }
    return partOfSpeech == category || secondaryPartOfSpeech == category
  }
}
