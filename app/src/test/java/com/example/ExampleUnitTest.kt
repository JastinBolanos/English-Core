package com.example

import androidx.compose.ui.graphics.Color
import com.example.domain.model.PartOfSpeech
import com.example.domain.model.Word
import com.example.domain.util.ConnectorRulesCatalog
import com.example.domain.util.WordDictionary
import com.example.presentation.vocabulary.getWordSyntaxColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testNgslCsv_ContainsExactly2809Words_IncludingWord217() {
    val file = File("src/main/res/raw/ngsl_stats.csv")
    if (file.exists()) {
      val lines = file.readLines().filter { it.isNotBlank() }
      assertEquals("ngsl_stats.csv debe tener exactamente 2809 palabras", 2809, lines.size)

      val word217Line = lines.firstOrNull { it.startsWith("word,") }
      assertNotNull("La palabra 'word' debe estar presente en el CSV", word217Line)
      assertTrue(word217Line!!.contains("217"))

      assertTrue("Primera palabra debe ser 'the'", lines.first().startsWith("the,1,"))
      assertTrue("Última palabra debe ser 'thirst'", lines.last().startsWith("thirst,2809,"))
    }
  }

  @Test
  fun testNgslTranslationsJson_Has2809Entries() {
    val jsonFile = File("src/main/res/raw/ngsl_translations.json")
    if (jsonFile.exists()) {
      val keyCount = jsonFile.readLines().count { it.trim().matches(Regex("\"[^\"]+\":\\s*\"[^\"]*\",?")) }
      assertEquals("ngsl_translations.json debe contener exactamente 2809 traducciones", 2809, keyCount)
    }
  }

  @Test
  fun testCategoryFilteringLogic() {
    val sampleList = listOf(
      Word(lemma = "because", partOfSpeech = PartOfSpeech.CONNECTOR_CLAUSE),
      Word(lemma = "despite", partOfSpeech = PartOfSpeech.CONNECTOR_NOUN),
      Word(lemma = "I", partOfSpeech = PartOfSpeech.PRONOUN),
      Word(lemma = "work", partOfSpeech = PartOfSpeech.VERB),
      Word(lemma = "time", partOfSpeech = PartOfSpeech.NOUN)
    )

    // Filter connectors
    val connectorsOnly = sampleList.filter { it.partOfSpeech.isConnector }
    assertEquals(2, connectorsOnly.size)
    assertTrue(connectorsOnly.any { it.lemma == "because" })
    assertTrue(connectorsOnly.any { it.lemma == "despite" })

    // Filter pronouns/subjects
    val pronounsOnly = sampleList.filter { it.partOfSpeech == PartOfSpeech.PRONOUN }
    assertEquals(1, pronounsOnly.size)
    assertEquals("I", pronounsOnly.first().lemma)

    // Filter verbs
    val verbsOnly = sampleList.filter { it.partOfSpeech == PartOfSpeech.VERB }
    assertEquals(1, verbsOnly.size)
    assertEquals("work", verbsOnly.first().lemma)

    // Filter nouns
    val nounsOnly = sampleList.filter { it.partOfSpeech == PartOfSpeech.NOUN }
    assertEquals(1, nounsOnly.size)
    assertEquals("time", nounsOnly.first().lemma)
  }

  @Test
  fun testConnectorRulesCatalog_Interception() {
    // Grupo A: CONNECTOR_CLAUSE
    val becauseRule = ConnectorRulesCatalog.getRule("because")
    assertNotNull(becauseRule)
    assertEquals(PartOfSpeech.CONNECTOR_CLAUSE, becauseRule?.category)

    // Grupo B: CONNECTOR_NOUN
    val despiteRule = ConnectorRulesCatalog.getRule("despite")
    assertNotNull(despiteRule)
    assertEquals(PartOfSpeech.CONNECTOR_NOUN, despiteRule?.category)
    assertTrue(despiteRule?.connectionRuleNote?.contains("PROHIBIDO") == true)

    // Grupo C: CONNECTOR_TRANSITION
    val howeverRule = ConnectorRulesCatalog.getRule("however")
    assertNotNull(howeverRule)
    assertEquals(PartOfSpeech.CONNECTOR_TRANSITION, howeverRule?.category)
    assertTrue(howeverRule?.connectionRuleNote?.contains("pausa") == true)
  }

  @Test
  fun testTop100WordsClassification_NotNouns() {
    val thanRule = ConnectorRulesCatalog.getRule("than")
    assertNotNull("than must be in ConnectorRulesCatalog", thanRule)
    assertEquals(PartOfSpeech.CONNECTOR_CLAUSE, thanRule?.category)

    val nowRule = ConnectorRulesCatalog.getRule("now")
    assertNotNull("now must be in ConnectorRulesCatalog", nowRule)
    assertEquals(PartOfSpeech.CONNECTOR_TRANSITION, nowRule?.category)

    val thenRule = ConnectorRulesCatalog.getRule("then")
    assertNotNull("then must be in ConnectorRulesCatalog", thenRule)
    assertEquals(PartOfSpeech.CONNECTOR_TRANSITION, thenRule?.category)

    val alsoRule = ConnectorRulesCatalog.getRule("also")
    assertNotNull("also must be in ConnectorRulesCatalog", alsoRule)
    assertEquals(PartOfSpeech.CONNECTOR_CLAUSE, alsoRule?.category)

    val intoRule = ConnectorRulesCatalog.getRule("into")
    assertNotNull("into must be in ConnectorRulesCatalog", intoRule)
    assertEquals(PartOfSpeech.CONNECTOR_NOUN, intoRule?.category)

    val onlyRule = ConnectorRulesCatalog.getRule("only")
    assertNotNull("only must be in ConnectorRulesCatalog", onlyRule)
    assertEquals(PartOfSpeech.CONNECTOR_NOUN, onlyRule?.category)

    // Pronouns
    assertTrue(WordDictionary.isPronoun("i"))
    assertTrue(WordDictionary.isPronoun("you"))
    assertTrue(WordDictionary.isPronoun("they"))
    assertTrue(WordDictionary.isPronoun("which"))
    assertTrue(WordDictionary.isPronoun("that"))

    // Core verbs
    assertTrue(WordDictionary.isVerb("is"))
    assertTrue(WordDictionary.isVerb("was"))
    assertTrue(WordDictionary.isVerb("were"))
    assertTrue(WordDictionary.isVerb("have"))
    assertTrue(WordDictionary.isVerb("do"))
    assertTrue(WordDictionary.isVerb("will"))
  }

  @Test
  fun testSyntaxColorMapping() {
    assertEquals(Color(0xFFFACC15), getWordSyntaxColor(PartOfSpeech.CONNECTOR_CLAUSE))
    assertEquals(Color(0xFFFACC15), getWordSyntaxColor(PartOfSpeech.CONNECTOR_NOUN))
    assertEquals(Color(0xFFFACC15), getWordSyntaxColor(PartOfSpeech.CONNECTOR_TRANSITION))
    assertEquals(Color(0xFFA855F7), getWordSyntaxColor(PartOfSpeech.PRONOUN))
    assertEquals(Color(0xFF3B82F6), getWordSyntaxColor(PartOfSpeech.VERB))
    assertEquals(Color(0xFF34D399), getWordSyntaxColor(PartOfSpeech.NOUN))
    assertEquals(Color(0xFFF8FAFC), getWordSyntaxColor(PartOfSpeech.MODIFIER))
  }

  @Test
  fun testMicroExampleLengthLimit() {
    val testWords = listOf("take", "water", "because", "the", "be", "house", "run", "fast", "despite", "however")
    for (lemma in testWords) {
      val catalogRule = ConnectorRulesCatalog.getRule(lemma)
      val example = catalogRule?.microExample ?: WordDictionary.getMicroExample(lemma, PartOfSpeech.NOUN)
      val wordCount = example.trim().split("\\s+".toRegex()).size
      assertTrue(
        "Micro-example for '$lemma' (\"$example\") has $wordCount words, expected <= 4 words",
        wordCount in 1..4
      )
    }
  }

  @Test
  fun testSyntaxFilterEnumAndReset() {
    val sampleList = listOf(
      Word(lemma = "because", partOfSpeech = PartOfSpeech.CONNECTOR_CLAUSE),
      Word(lemma = "I", partOfSpeech = PartOfSpeech.PRONOUN),
      Word(lemma = "work", partOfSpeech = PartOfSpeech.VERB),
      Word(lemma = "time", partOfSpeech = PartOfSpeech.NOUN)
    )

    fun filterBySyntax(filter: com.example.presentation.vocabulary.SyntaxFilter?): List<Word> {
      return if (filter == null) sampleList else sampleList.filter {
        when (filter) {
          com.example.presentation.vocabulary.SyntaxFilter.CONNECTOR -> it.partOfSpeech.isConnector
          com.example.presentation.vocabulary.SyntaxFilter.SUBJECT -> it.partOfSpeech == PartOfSpeech.PRONOUN
          com.example.presentation.vocabulary.SyntaxFilter.VERB -> it.partOfSpeech == PartOfSpeech.VERB
          com.example.presentation.vocabulary.SyntaxFilter.NOUN -> it.partOfSpeech == PartOfSpeech.NOUN
          com.example.presentation.vocabulary.SyntaxFilter.MODIFIER -> it.partOfSpeech == PartOfSpeech.MODIFIER
        }
      }
    }

    assertEquals(1, filterBySyntax(com.example.presentation.vocabulary.SyntaxFilter.CONNECTOR).size)
    assertEquals(1, filterBySyntax(com.example.presentation.vocabulary.SyntaxFilter.SUBJECT).size)
    assertEquals(1, filterBySyntax(com.example.presentation.vocabulary.SyntaxFilter.VERB).size)
    assertEquals(1, filterBySyntax(com.example.presentation.vocabulary.SyntaxFilter.NOUN).size)
    // Clear filter (null) returns all words
    assertEquals(4, filterBySyntax(null).size)
  }

  @Test
  fun testPronunciationEvaluator_ExactMatch() {
    val target = listOf("I", "want", "to", "learn", "English")
    val spoken = "I want to learn English"

    val result = com.example.domain.util.PronunciationEvaluator.evaluate(target, spoken)

    assertTrue("Debe ser coincidencia exacta", result.isExactMatch)
    assertEquals(100, result.accuracyPercentage)
    assertEquals(5, result.wordMatches.size)
    assertTrue("Todas las palabras deben coincidir", result.wordMatches.all { it.isMatch })
    assertTrue(result.feedbackMessage.contains("100%"))
  }

  @Test
  fun testPronunciationEvaluator_PartialMismatch() {
    val target = listOf("I", "want", "water")
    val spoken = "I won't water"

    val result = com.example.domain.util.PronunciationEvaluator.evaluate(target, spoken)

    org.junit.Assert.assertFalse("No debe ser exacta", result.isExactMatch)
    assertEquals(3, result.wordMatches.size)
    assertTrue("Primera palabra 'I' debe coincidir", result.wordMatches[0].isMatch)
    org.junit.Assert.assertFalse("Segunda palabra 'won't' no debe coincidir", result.wordMatches[1].isMatch)
    assertEquals("want", result.wordMatches[1].expectedWord)
    assertTrue("Tercera palabra 'water' debe coincidir", result.wordMatches[2].isMatch)
    assertEquals(66, result.accuracyPercentage)
  }

  @Test
  fun testPronunciationEvaluator_PunctuationAndCaseInsensitive() {
    val target = listOf("because", "we", "can")
    val spoken = "Because, we CAN!"

    val result = com.example.domain.util.PronunciationEvaluator.evaluate(target, spoken)

    assertTrue("Debe ignorar mayúsculas y puntuaciones", result.isExactMatch)
    assertEquals(100, result.accuracyPercentage)
  }

  @Test
  fun testWord_UseCountDefaultAndIncrement() {
    val word = Word(lemma = "practice", useCount = 0)
    assertEquals(0, word.useCount)

    val updatedWord = word.copy(useCount = word.useCount + 1)
    assertEquals(1, updatedWord.useCount)

    val frequentWord = word.copy(useCount = 15)
    assertEquals(15, frequentWord.useCount)
  }

  @Test
  fun testNgslGrammaticalTypes_ContainsAll2809WordsFromBlocks1To4() {
    val jsonFile = File("src/main/res/raw/ngsl_grammatical_types.json")
    if (jsonFile.exists()) {
      val keyCount = jsonFile.readLines().count { it.trim().matches(Regex("\"[^\"]+\":\\s*\"[^\"]*\",?")) }
      assertEquals("Debe contener exactamente 2809 palabras en los 4 bloques", 2809, keyCount)
      val content = jsonFile.readText()
      // Bloque 1 (1 al 700)
      assertTrue(content.contains("\"the\": \"modificador (artículo)\""))
      assertTrue(content.contains("\"fit\": \"verbo/modificador (adjetivo)/sustantivo\""))
      // Bloque 2 (701 al 1400)
      assertTrue(content.contains("\"guy\": \"sustantivo\""))
      assertTrue(content.contains("\"construction\": \"sustantivo\""))
      // Bloque 3 (1401 al 2100)
      assertTrue(content.contains("\"dear\": \"modificador (adjetivo/interjección)/sustantivo\""))
      assertTrue(content.contains("\"restore\": \"verbo\""))
      // Bloque 4 (2101 al 2809)
      assertTrue(content.contains("\"formula\": \"sustantivo\""))
      assertTrue(content.contains("\"alter\": \"verbo\""))
      assertTrue(content.contains("\"perceive\": \"verbo\""))
      assertTrue(content.contains("\"thirst\": \"sustantivo/verbo\""))
    }
  }

  @Test
  fun testWordGrammarCatalog_DisplayLabelAndParsing() {
    val word1 = Word(lemma = "the", rawPartOfSpeech = "modificador (artículo)")
    assertEquals("modificador (artículo)", word1.displayCategoryLabel)

    val word700 = Word(lemma = "fit", rawPartOfSpeech = "verbo/modificador (adjetivo)/sustantivo")
    assertEquals("verbo/modificador (adjetivo)/sustantivo", word700.displayCategoryLabel)

    val (pos, sec) = com.example.domain.util.WordGrammarCatalog.parseCategories("verbo/sustantivo", "work")
    assertEquals(PartOfSpeech.VERB, pos)
    assertEquals(PartOfSpeech.NOUN, sec)
  }

  @Test
  fun testStrictPrimaryCategoryAndPureFiltering() {
    val onlyWord = Word(lemma = "only", rawPartOfSpeech = "modificador (adverbio/adjetivo)/conector")
    val firstWord = Word(lemma = "first", rawPartOfSpeech = "modificador (adjetivo/adverbio)/conector")
    val evenWord = Word(lemma = "even", rawPartOfSpeech = "modificador (adverbio)/conector")
    val makeWord = Word(lemma = "make", rawPartOfSpeech = "verbo/sustantivo")
    val timeWord = Word(lemma = "time", rawPartOfSpeech = "sustantivo/verbo")
    val becauseWord = Word(lemma = "because", rawPartOfSpeech = "conector (conjunción)")

    // 1. Categoría primaria estricta
    assertEquals(PartOfSpeech.MODIFIER, onlyWord.getPrimaryCategory())
    assertEquals(PartOfSpeech.MODIFIER, firstWord.getPrimaryCategory())
    assertEquals(PartOfSpeech.MODIFIER, evenWord.getPrimaryCategory())
    assertEquals(PartOfSpeech.VERB, makeWord.getPrimaryCategory())
    assertEquals(PartOfSpeech.NOUN, timeWord.getPrimaryCategory())
    assertTrue(becauseWord.getPrimaryCategory().isConnector)

    // 2. Colores: only, first y even deben ser Blanco (#F8FAFC), NO amarillo
    val whiteColor = Color(0xFFF8FAFC)
    val yellowColor = Color(0xFFFACC15)
    val blueColor = Color(0xFF3B82F6)
    val greenColor = Color(0xFF34D399)

    assertEquals(whiteColor, getWordSyntaxColor(onlyWord))
    assertEquals(whiteColor, getWordSyntaxColor(firstWord))
    assertEquals(whiteColor, getWordSyntaxColor(evenWord))
    assertEquals(blueColor, getWordSyntaxColor(makeWord))
    assertEquals(greenColor, getWordSyntaxColor(timeWord))
    assertEquals(yellowColor, getWordSyntaxColor(becauseWord))

    // 3. Filtrado Puro: only, first y even NO deben entrar en Conector
    assertFalse("only no debe colarse en Conector", onlyWord.matchesCategory(PartOfSpeech.CONNECTOR))
    assertFalse("first no debe colarse en Conector", firstWord.matchesCategory(PartOfSpeech.CONNECTOR))
    assertFalse("even no debe colarse en Conector", evenWord.matchesCategory(PartOfSpeech.CONNECTOR))
    assertTrue("only pertenece a Modificador", onlyWord.matchesCategory(PartOfSpeech.MODIFIER))

    // make no debe colarse en Sustantivo
    assertFalse("make no debe colarse en Sustantivo", makeWord.matchesCategory(PartOfSpeech.NOUN))
    assertTrue("make pertenece a Verbo", makeWord.matchesCategory(PartOfSpeech.VERB))

    // time no debe colarse en Verbo
    assertFalse("time no debe colarse en Verbo", timeWord.matchesCategory(PartOfSpeech.VERB))
    assertTrue("time pertenece a Sustantivo", timeWord.matchesCategory(PartOfSpeech.NOUN))

    // SyntaxFilter enum test
    assertTrue(com.example.presentation.vocabulary.SyntaxFilter.MODIFIER.matchesWord(onlyWord))
    assertFalse(com.example.presentation.vocabulary.SyntaxFilter.CONNECTOR.matchesWord(onlyWord))
    assertTrue(com.example.presentation.vocabulary.SyntaxFilter.CONNECTOR.matchesWord(becauseWord))
  }

  @Test
  fun testDraftReorderingAndUpdatedStateLogic() {
    val word1 = Word(id = 1, lemma = "the", partOfSpeech = PartOfSpeech.MODIFIER)
    val word2 = Word(id = 2, lemma = "month", partOfSpeech = PartOfSpeech.NOUN)
    val word3 = Word(id = 3, lemma = "play", partOfSpeech = PartOfSpeech.VERB)

    var draft = listOf(word1, word2, word3)
    assertEquals("the month play", draft.joinToString(" ") { it.lemma })

    // Mover 'play' (índice 2) a la primera posición (índice 0)
    fun moveWord(list: List<Word>, from: Int, to: Int): List<Word> {
      val mutable = list.toMutableList()
      val item = mutable.removeAt(from)
      mutable.add(to, item)
      return mutable
    }

    draft = moveWord(draft, 2, 0)
    assertEquals("play the month", draft.joinToString(" ") { it.lemma })

    // Mover 'month' (índice 2) a la posición 1
    draft = moveWord(draft, 2, 1)
    assertEquals("play month the", draft.joinToString(" ") { it.lemma })
  }

  @Test
  fun testPhraseTranslator_TranslatesFullPhrasesNotJustSingleWord() {
    // 1. Caso específico del usuario: "be of a"
    val userPhrase = "be of a"
    val userTranslation = com.example.domain.util.PhraseTranslator.translatePhrase(userPhrase)
    assertNotNull(userTranslation)
    assertTrue("Debe traducir toda la frase y no solo 'ser'", userTranslation.contains("ser"))
    assertTrue("Debe incluir la traducción de 'of' ('de')", userTranslation.contains("de"))
    assertTrue("Debe incluir la traducción de 'a' ('un')", userTranslation.contains("un"))
    assertEquals("ser de un", userTranslation)

    // 2. Frase de 4 palabras: "be of a word"
    WordDictionary.injectTranslations(mapOf("word" to "palabra"))
    val fourWordsPhrase = "be of a word"
    val fourWordsTranslation = com.example.domain.util.PhraseTranslator.translatePhrase(fourWordsPhrase)
    assertEquals("ser de una palabra", fourWordsTranslation)

    // 3. Frase con conectores y pronombres: "because they can"
    val clausePhrase = "because they can"
    val clauseTranslation = com.example.domain.util.PhraseTranslator.translatePhrase(clausePhrase)
    assertTrue(clauseTranslation.contains("porque"))
    assertTrue(clauseTranslation.contains("ellos"))
    assertTrue(clauseTranslation.contains("puede"))
  }

  @Test
  fun testEverydayPhrasesCatalog_HasExactly50Phrases() {
    val phrases = com.example.domain.model.EverydayPhrasesCatalog.phrases
    assertEquals("El catálogo debe contener exactamente 50 frases cotidianas y de hogar", 50, phrases.size)

    // IDs del 1 al 50
    for (i in 1..50) {
      val phrase = phrases.firstOrNull { it.id == i }
      assertNotNull("Debe existir la frase con id $i", phrase)
      assertTrue("El texto en español de la frase $i no puede estar vacío", phrase!!.spanishText.isNotBlank())
      assertTrue("La categoría de la frase $i no puede estar vacía", phrase.category.isNotBlank())
    }

    // Verificar temáticas cotidianas y de hogar
    val allText = phrases.joinToString(" ") { it.spanishText.lowercase() }
    assertTrue(allText.contains("café") || allText.contains("cocina"))
    assertTrue(allText.contains("platos") || allText.contains("limpiar"))
    assertTrue(allText.contains("puerta") || allText.contains("llaves"))
    assertTrue(allText.contains("dormir") || allText.contains("buenas noches"))
  }

  @Test
  fun testStudyStopwatch_FormattingAnd1HourGoal() {
    fun formatStopwatchTime(millis: Long): String {
      val totalSeconds = millis / 1000
      val minutes = (totalSeconds / 60) % 60
      val seconds = totalSeconds % 60
      val centiseconds = (millis % 1000) / 10
      val hours = totalSeconds / 3600
      return if (hours > 0) {
        String.format(java.util.Locale.US, "%d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds)
      } else {
        String.format(java.util.Locale.US, "%02d:%02d.%02d", minutes, seconds, centiseconds)
      }
    }

    // 0 milisegundos
    assertEquals("00:00.00", formatStopwatchTime(0L))

    // 12 minutos, 34 segundos y 56 centisegundos
    val millis1 = (12 * 60 + 34) * 1000L + 560L
    assertEquals("12:34.56", formatStopwatchTime(millis1))

    // 1 hora, 5 minutos, 20 segundos y 30 centisegundos
    val millis2 = (3600 + 5 * 60 + 20) * 1000L + 300L
    assertEquals("1:05:20.30", formatStopwatchTime(millis2))

    // Meta de 1 hora (3,600,000 ms)
    val oneHourMillis = 3_600_000L
    val halfHourMillis = 1_800_000L
    val halfHourProgress = (halfHourMillis.toFloat() / oneHourMillis)
    assertEquals(0.5f, halfHourProgress, 0.001f)

    val remainingMinutes = maxOf(0L, (oneHourMillis - halfHourMillis + 59_999L) / 60_000L)
    assertEquals(30L, remainingMinutes)
  }

  @Test
  fun testMainSectionTab_EnumValues() {
    val tabs = com.example.presentation.vocabulary.MainSectionTab.values()
    assertEquals(4, tabs.size)
    assertTrue(tabs.contains(com.example.presentation.vocabulary.MainSectionTab.VOCABULARY))
    assertTrue(tabs.contains(com.example.presentation.vocabulary.MainSectionTab.ALPHABET))
    assertTrue(tabs.contains(com.example.presentation.vocabulary.MainSectionTab.NUMBERS))
    assertTrue(tabs.contains(com.example.presentation.vocabulary.MainSectionTab.PHRASES))
  }

  @Test
  fun testNumbersCatalog_Has100Entries() {
    val numbers = com.example.presentation.numbers.NumbersCatalog.allNumbers
    assertEquals(100, numbers.size)
    assertEquals("One", numbers.first().word)
    assertEquals(1, numbers.first().number)
    assertEquals("One hundred", numbers.last().word)
    assertEquals(100, numbers.last().number)
  }

  @Test
  fun testTtsSpeedLevels_CycleValues() {
    val speedLevels = listOf(1.0f, 0.5f, 0.3f)
    assertEquals(3, speedLevels.size)
    assertEquals(1.0f, speedLevels[0])
    assertEquals(0.5f, speedLevels[1])
    assertEquals(0.3f, speedLevels[2])

    fun getNextSpeed(current: Float): Float {
      val currentIndex = speedLevels.indexOf(current)
      val nextIndex = if (currentIndex in speedLevels.indices) (currentIndex + 1) % speedLevels.size else 0
      return speedLevels[nextIndex]
    }

    assertEquals(0.5f, getNextSpeed(1.0f))
    assertEquals(0.3f, getNextSpeed(0.5f))
    assertEquals(1.0f, getNextSpeed(0.3f))
  }
}


