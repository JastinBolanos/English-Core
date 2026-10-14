package com.example.domain.util

import com.example.domain.model.PartOfSpeech

data class ConnectorRule(
  val lemma: String,
  val category: PartOfSpeech,
  val spanishTranslation: String,
  val microExample: String,
  val connectionRuleNote: String = category.connectionRuleNote
)

object ConnectorRulesCatalog {
  private val rules = mapOf(
    "because" to ConnectorRule(
      lemma = "because",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "porque",
      microExample = "because we can",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "despite" to ConnectorRule(
      lemma = "despite",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "a pesar de",
      microExample = "despite the rain",
      connectionRuleNote = PartOfSpeech.CONNECTOR_NOUN.connectionRuleNote
    ),
    "however" to ConnectorRule(
      lemma = "however",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "sin embargo",
      microExample = "however you want",
      connectionRuleNote = PartOfSpeech.CONNECTOR_TRANSITION.connectionRuleNote
    ),
    "than" to ConnectorRule(
      lemma = "than",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "que",
      microExample = "better than ever",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "now" to ConnectorRule(
      lemma = "now",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "ahora",
      microExample = "start right now",
      connectionRuleNote = PartOfSpeech.CONNECTOR_TRANSITION.connectionRuleNote
    ),
    "then" to ConnectorRule(
      lemma = "then",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "entonces",
      microExample = "see you then",
      connectionRuleNote = PartOfSpeech.CONNECTOR_TRANSITION.connectionRuleNote
    ),
    "also" to ConnectorRule(
      lemma = "also",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "también",
      microExample = "also very good",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "into" to ConnectorRule(
      lemma = "into",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "hacia dentro",
      microExample = "into the room",
      connectionRuleNote = PartOfSpeech.CONNECTOR_NOUN.connectionRuleNote
    ),
    "only" to ConnectorRule(
      lemma = "only",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "solo",
      microExample = "only one way",
      connectionRuleNote = PartOfSpeech.CONNECTOR_NOUN.connectionRuleNote
    ),
    "and" to ConnectorRule(
      lemma = "and",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "y",
      microExample = "you and me",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "but" to ConnectorRule(
      lemma = "but",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "pero",
      microExample = "simple but true",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "so" to ConnectorRule(
      lemma = "so",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "así que",
      microExample = "so be it",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "if" to ConnectorRule(
      lemma = "if",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "si",
      microExample = "if you can",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "although" to ConnectorRule(
      lemma = "although",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "aunque",
      microExample = "although it hurts",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "while" to ConnectorRule(
      lemma = "while",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "mientras",
      microExample = "while we wait",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "since" to ConnectorRule(
      lemma = "since",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "ya que",
      microExample = "since day one",
      connectionRuleNote = PartOfSpeech.CONNECTOR_CLAUSE.connectionRuleNote
    ),
    "therefore" to ConnectorRule(
      lemma = "therefore",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "por lo tanto",
      microExample = "therefore we win",
      connectionRuleNote = PartOfSpeech.CONNECTOR_TRANSITION.connectionRuleNote
    ),
    "during" to ConnectorRule(
      lemma = "during",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "durante",
      microExample = "during the night",
      connectionRuleNote = PartOfSpeech.CONNECTOR_NOUN.connectionRuleNote
    ),
    "without" to ConnectorRule(
      lemma = "without",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "sin",
      microExample = "without any fear",
      connectionRuleNote = PartOfSpeech.CONNECTOR_NOUN.connectionRuleNote
    )
  )

  fun getRule(lemma: String): ConnectorRule? {
    return rules[lemma.lowercase().trim()]
  }
}
