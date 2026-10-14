package com.example.domain.model

enum class PartOfSpeech(
  val displayName: String,
  val shortLabel: String,
  val emojiColor: String
) {
  CONNECTOR_CLAUSE("Conector Cláusula", "CLAUSE", "🟡"),
  CONNECTOR_NOUN("Conector Sustantivo/ING", "NOUN-CONN", "🟠"),
  CONNECTOR_TRANSITION("Transición", "TRANS", "🟢"),
  PRONOUN("Sujeto", "PRON", "🟣"),
  VERB("Verbo", "VERB", "🔵"),
  NOUN("Sustantivo", "NOUN", "⚪"),
  OTHER("Otro", "OTHER", "⚪");

  val isConnector: Boolean
    get() = this == CONNECTOR_CLAUSE || this == CONNECTOR_NOUN || this == CONNECTOR_TRANSITION

  /**
   * REGLA DE CONEXIÓN (IMÁN):
   * Retorna las categorías visuales que se recomienda colocar después de esta palabra.
   */
  val nextExpectedCategories: List<PartOfSpeech>
    get() = when (this) {
      CONNECTOR_CLAUSE -> listOf(PRONOUN, VERB)
      CONNECTOR_NOUN -> listOf(NOUN, VERB)
      CONNECTOR_TRANSITION -> listOf(PRONOUN, VERB)
      PRONOUN -> listOf(VERB)
      VERB -> listOf(NOUN, CONNECTOR_CLAUSE, CONNECTOR_NOUN)
      NOUN -> listOf(VERB, CONNECTOR_CLAUSE, CONNECTOR_TRANSITION)
      OTHER -> listOf(NOUN, VERB)
    }

  /**
   * Nota explicativa de conexión sintáctica por defecto.
   */
  val connectionRuleNote: String
    get() = when (this) {
      CONNECTOR_CLAUSE -> "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
      CONNECTOR_NOUN -> "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
      CONNECTOR_TRANSITION -> "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
      PRONOUN -> "Protagonista/Sujeto: Quién realiza la acción. Exige obligatoriamente un Verbo [🔵] inmediatamente después."
      VERB -> "Motor de la frase: Acción o estado. Conéctalo con un Objeto/Sustantivo [⚪] o un Conector [🟡]."
      NOUN -> "Concepto u objeto: Actúa como sujeto o receptor. Conéctalo con un Verbo [🔵] o un Conector [🟡]."
      OTHER -> "Modificador de sintaxis. Conéctalo con un Sustantivo [⚪] o un Verbo [🔵]."
    }
}

typealias WordCategory = PartOfSpeech
