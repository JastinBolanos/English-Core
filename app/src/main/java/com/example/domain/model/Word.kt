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
  val useCount: Int = 0
) {
  val nextExpectedCategories: List<PartOfSpeech>
    get() = partOfSpeech.nextExpectedCategories

  val connectionRuleNote: String
    get() = overrideConnectionNote ?: partOfSpeech.connectionRuleNote

  val displaySpanishTranslation: String
    get() = if (spanishTranslation.isNotBlank()) spanishTranslation else com.example.domain.util.WordDictionary.getTranslation(lemma, partOfSpeech)
}
