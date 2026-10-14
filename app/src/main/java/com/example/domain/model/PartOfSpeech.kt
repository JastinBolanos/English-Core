package com.example.domain.model

enum class PartOfSpeech(
  val displayName: String,
  val shortLabel: String,
  val emojiColor: String
) {
  CONNECTOR_CLAUSE("Conector Cláusula", "CLAUSE", "🟡"),
  CONNECTOR_NOUN("Conector Sustantivo/ING", "NOUN-CONN", "🟡"),
  CONNECTOR_TRANSITION("Transición", "TRANS", "🟡"),
  CONNECTOR("Conector", "CONN", "🟡"),
  PRONOUN("Sujeto", "PRON", "🟣"),
  VERB("Verbo", "VERB", "🔵"),
  NOUN("Sustantivo", "NOUN", "🟢"),
  MODIFIER("Modificador", "MOD", "⚪"),
  OTHER("Otro", "OTHER", "⚪");

  val isConnector: Boolean
    get() = this == CONNECTOR || this == CONNECTOR_CLAUSE || this == CONNECTOR_NOUN || this == CONNECTOR_TRANSITION

  /**
   * PUNTITOS PREDICTIVOS (➔):
   * Conector -> sugiere Sujeto, Modificador, Sustantivo.
   * Sujeto -> sugiere Verbo, Modificador.
   * Verbo -> sugiere Sustantivo, Modificador, Conector.
   * Modificador -> sugiere Sustantivo, Verbo.
   * Sustantivo -> sugiere Verbo, Conector.
   */
  val nextExpectedCategories: List<PartOfSpeech>
    get() = when (this) {
      CONNECTOR, CONNECTOR_CLAUSE, CONNECTOR_NOUN, CONNECTOR_TRANSITION -> listOf(PRONOUN, MODIFIER, NOUN)
      PRONOUN -> listOf(VERB, MODIFIER)
      VERB -> listOf(NOUN, MODIFIER, CONNECTOR)
      MODIFIER -> listOf(NOUN, VERB)
      NOUN -> listOf(VERB, CONNECTOR)
      OTHER -> listOf(NOUN, VERB)
    }

  /**
   * Nota explicativa de conexión sintáctica.
   */
  val connectionRuleNote: String
    get() = when (this) {
      CONNECTOR_CLAUSE -> "🔗 Conecta con: [🟣 Sujeto], [⚪ Modificador] o [🟢 Sustantivo]"
      CONNECTOR_NOUN -> "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [🟢 Sustantivo] o [🔵 Verbo-ING]"
      CONNECTOR_TRANSITION -> "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
      CONNECTOR -> "🔗 Conecta con: [🟣 Sujeto], [⚪ Modificador] o [🟢 Sustantivo]"
      PRONOUN -> "Protagonista/Sujeto: Conéctalo con un Verbo [🔵] o un Modificador [⚪]."
      VERB -> "Motor de la frase: Conéctalo con un Sustantivo [🟢], un Modificador [⚪] o un Conector [🟡]."
      NOUN -> "Concepto u objeto: Conéctalo con un Verbo [🔵] o un Conector [🟡]."
      MODIFIER -> "Modificador: Acompaña a un Sustantivo [🟢] o a un Verbo [🔵]."
      OTHER -> "Modificador de sintaxis. Conéctalo con un Sustantivo [🟢] o un Verbo [🔵]."
    }
}

typealias WordCategory = PartOfSpeech
