package com.example.domain.util

import com.example.domain.model.PartOfSpeech
import java.util.Locale

/**
 * Regla de conector del catálogo maestro.
 */
data class ConnectorRule(
  val lemma: String,
  val category: PartOfSpeech,
  val spanishTranslation: String,
  val microExample: String,
  val connectionRuleNote: String
)

/**
 * INTERCEPTOR DE REGLAS DE CONECTORES Y OPERADORES (Diccionario Maestro Local).
 *
 * Clasifica automáticamente los conectores, preposiciones y operadores del NGSL en 3 tipos de imanes:
 * 1. CONNECTOR_CLAUSE: Exige [🟣 Sujeto] + [🔵 Verbo]
 * 2. CONNECTOR_NOUN: PROHIBIDO Sujeto+Verbo. Exige [⚪ Sustantivo] o [🔵 Verbo-ING]
 * 3. CONNECTOR_TRANSITION: Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]
 */
object ConnectorRulesCatalog {

  private val rulesMap: Map<String, ConnectorRule> = listOf(
    // ==========================================
    // GRUPO A: CONNECTOR_CLAUSE (Color Amarillo)
    // Regla: '🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]'
    // ==========================================
    ConnectorRule(
      lemma = "because",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Porque",
      microExample = "because I work",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "although",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Aunque",
      microExample = "although it rains",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "though",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Aunque (informal)",
      microExample = "though he tried",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "since",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Ya que / Desde que",
      microExample = "since you are",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "if",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Si (condicional)",
      microExample = "if you want",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "unless",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "A menos que",
      microExample = "unless you study",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "while",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Mientras / Mientras que",
      microExample = "while I sleep",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "whereas",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Mientras que (contraste)",
      microExample = "whereas she knows",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "whether",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Si (duda/opciones)",
      microExample = "whether it works",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "until",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Hasta que",
      microExample = "until you finish",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "when",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Cuando",
      microExample = "when it starts",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "whenever",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Siempre que / Cuando sea",
      microExample = "whenever you can",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "once",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Una vez que",
      microExample = "once we arrive",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "as",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Como / A medida que",
      microExample = "as I said",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "and",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Y",
      microExample = "and we try",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo] o elemento coordinado"
    ),
    ConnectorRule(
      lemma = "but",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Pero / Sino",
      microExample = "but it is",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "so",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Así que / Por lo tanto",
      microExample = "so I will",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "or",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "O",
      microExample = "or you choose",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo] o alternativa"
    ),
    ConnectorRule(
      lemma = "than",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Que (comparativo)",
      microExample = "better than ever",
      connectionRuleNote = "🔗 Conector comparativo: Conéctalo con un [⚪ Objeto] o [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "also",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "También",
      microExample = "also I can",
      connectionRuleNote = "🔗 Operador aditivo: Conéctalo con un [🟣 Sujeto] o [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "before",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Antes / Antes de que",
      microExample = "before you go",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo] o [⚪ Sustantivo]"
    ),
    ConnectorRule(
      lemma = "after",
      category = PartOfSpeech.CONNECTOR_CLAUSE,
      spanishTranslation = "Después / Después de que",
      microExample = "after we finish",
      connectionRuleNote = "🔗 Conecta con: [🟣 Sujeto] + [🔵 Verbo] o [⚪ Sustantivo]"
    ),

    // ==========================================
    // GRUPO B: CONNECTOR_NOUN (Color Naranja)
    // Regla: '⚠️ PROHIBIDO Sujeto+Verbo. Usa: [⚪ Sustantivo] o [🔵 Verbo-ING]'
    // ==========================================
    ConnectorRule(
      lemma = "into",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Hacia dentro / En",
      microExample = "into the room",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "only",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Solo / Solamente",
      microExample = "only good things",
      connectionRuleNote = "🔗 Modificador restrictivo: Conéctalo con un [⚪ Sustantivo] o [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "in",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "En / Dentro de",
      microExample = "in the room",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "on",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Sobre / En",
      microExample = "on the table",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "at",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "En / A",
      microExample = "at that time",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "to",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "A / Hacia / Para",
      microExample = "to the city",
      connectionRuleNote = "🔗 Preposición / Conector: Conéctalo con un [🔵 Verbo] o [⚪ Objeto]"
    ),
    ConnectorRule(
      lemma = "for",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Para / Por",
      microExample = "for your help",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "with",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Con",
      microExample = "with a friend",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "from",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "De / Desde",
      microExample = "from the start",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "by",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Por / Junto a",
      microExample = "by the sea",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "about",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Acerca de / Sobre",
      microExample = "about this life",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "through",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "A través de",
      microExample = "through the door",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "over",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Sobre / Por encima",
      microExample = "over the world",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "between",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Entre (dos)",
      microExample = "between two parts",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con dos [⚪ Sustantivos] o [🟣 Pronombres]"
    ),
    ConnectorRule(
      lemma = "under",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Bajo / Debajo de",
      microExample = "under the sky",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "against",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Contra",
      microExample = "against the wind",
      connectionRuleNote = "🔗 Preposición de enlace: Conéctalo con un [⚪ Sustantivo/Objeto]"
    ),
    ConnectorRule(
      lemma = "out",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Fuera / Hacia afuera",
      microExample = "out of here",
      connectionRuleNote = "🔗 Operador de enlace: Conéctalo con una preposición o [⚪ Sustantivo]"
    ),
    ConnectorRule(
      lemma = "very",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Muy",
      microExample = "very good day",
      connectionRuleNote = "🔗 Modificador de grado: Conéctalo con un adjetivo o [⚪ Sustantivo]"
    ),
    ConnectorRule(
      lemma = "despite",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "A pesar de",
      microExample = "despite the rain",
      connectionRuleNote = "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "during",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Durante",
      microExample = "during the day",
      connectionRuleNote = "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "without",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Sin",
      microExample = "without any fear",
      connectionRuleNote = "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "besides",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Además de",
      microExample = "besides this job",
      connectionRuleNote = "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "except",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Excepto",
      microExample = "except that part",
      connectionRuleNote = "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "instead",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "En lugar de (instead of)",
      microExample = "instead of that",
      connectionRuleNote = "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
    ),
    ConnectorRule(
      lemma = "due",
      category = PartOfSpeech.CONNECTOR_NOUN,
      spanishTranslation = "Debido a (due to)",
      microExample = "due to this",
      connectionRuleNote = "⚠️ PROHIBIDO Sujeto+Verbo. Siguiente pieza: [⚪ Sustantivo] o [🔵 Verbo-ING]"
    ),

    // ==========================================
    // GRUPO C: CONNECTOR_TRANSITION (Color Dorado)
    // Regla: '🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]'
    // ==========================================
    ConnectorRule(
      lemma = "now",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Ahora",
      microExample = "Now, we begin",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "then",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Entonces / Luego",
      microExample = "Then, we win",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "just",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Solo / Justo",
      microExample = "just you wait",
      connectionRuleNote = "🔗 Operador lógico: Conéctalo con un [🔵 Verbo] o [🟣 Sujeto]"
    ),
    ConnectorRule(
      lemma = "even",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Incluso / Aún",
      microExample = "even we know",
      connectionRuleNote = "🔗 Operador de énfasis: Conéctalo con un [🟣 Sujeto] o [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "still",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Todavía / Aún",
      microExample = "still I try",
      connectionRuleNote = "🔗 Operador de continuidad: Conéctalo con un [🔵 Verbo] o [🟣 Sujeto]"
    ),
    ConnectorRule(
      lemma = "however",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Sin embargo,",
      microExample = "However, it works",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "therefore",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Por lo tanto,",
      microExample = "Therefore, we win",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "furthermore",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Además (formal),",
      microExample = "Furthermore, it is",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "moreover",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Es más / Además,",
      microExample = "Moreover, I agree",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "nevertheless",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "No obstante,",
      microExample = "Nevertheless, he tried",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "otherwise",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "De lo contrario,",
      microExample = "Otherwise, it fails",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "meanwhile",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Mientras tanto,",
      microExample = "Meanwhile, I wait",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "consequently",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "En consecuencia,",
      microExample = "Consequently, it changed",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    ),
    ConnectorRule(
      lemma = "thus",
      category = PartOfSpeech.CONNECTOR_TRANSITION,
      spanishTranslation = "Así / Por consiguiente,",
      microExample = "Thus, we finished",
      connectionRuleNote = "🔗 Inicia frase con pausa: [ , ] + [🟣 Sujeto] + [🔵 Verbo]"
    )
  ).associateBy { it.lemma.lowercase(Locale.ROOT) }

  fun getRule(word: String): ConnectorRule? {
    return rulesMap[word.lowercase(Locale.ROOT).trim()]
  }

  fun hasRule(word: String): Boolean {
    return rulesMap.containsKey(word.lowercase(Locale.ROOT).trim())
  }

  fun getAllConnectors(): Set<String> {
    return rulesMap.keys
  }
}
