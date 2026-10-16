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
 * Diccionario de apoyo para las 4 Reglas de Aprendizaje Autodidacta:
 * - Sintaxis Pasiva (Conectores, Verbos, Sustantivos)
 * - Fricción de Revelación (Traducciones al español para las 2809 palabras)
 * - Micro-Ejemplos contextuales garantizados de máximo 3 o 4 palabras.
 */
object WordDictionary {

  private val loadedTranslations = ConcurrentHashMap<String, String>()

  /**
   * Carga las traducciones al español de las 2809 palabras desde ngsl_translations.json
   */
  fun loadTranslationsFromContext(context: Context) {
    if (loadedTranslations.isNotEmpty()) return
    try {
      val inputStream = context.resources.openRawResource(R.raw.ngsl_translations)
      val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
      val jsonString = reader.readText()
      reader.close()
      val jsonObject = JSONObject(jsonString)
      val keys = jsonObject.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.optString(key, "")
        if (value.isNotBlank()) {
          loadedTranslations[key.lowercase(Locale.ROOT).trim()] = value
        }
      }
    } catch (_: Exception) {
      // Ignorar si ya está en memoria
    }
  }

  fun getLoadedCount(): Int = loadedTranslations.size

  private val reverseTranslations = ConcurrentHashMap<String, String>()

  fun getReverseTranslations(): Map<String, String> {
    if (reverseTranslations.isEmpty() && loadedTranslations.isNotEmpty()) {
      for ((enWord, esTrans) in loadedTranslations) {
        val cleanEn = enWord.trim().lowercase(Locale.ROOT)
        val cleanEsRaw = esTrans.replace(Regex("\\(.*?\\)"), "").trim()
        val parts = cleanEsRaw.split(Regex("[,;/]"))
        for (part in parts) {
          val cleanPart = part.trim().lowercase(Locale.ROOT)
          if (cleanPart.isNotBlank() && !reverseTranslations.containsKey(cleanPart)) {
            reverseTranslations[cleanPart] = cleanEn
          }
        }
      }
    }
    return reverseTranslations
  }

  fun injectTranslations(map: Map<String, String>) {
    loadedTranslations.putAll(map)
    reverseTranslations.clear()
  }

  val connectorWords = setOf(
    "and", "or", "but", "so", "if", "because", "though", "although", "while", "since",
    "unless", "whether", "whereas", "either", "neither", "nor", "not", "no", "than", "then",
    "also", "as", "when", "whenever", "once", "before", "after", "until", "however",
    "therefore", "despite", "during", "without", "instead", "due", "besides", "except",
    "furthermore", "moreover", "nevertheless", "otherwise", "meanwhile", "consequently", "thus",
    "of", "to", "in", "for", "on", "with", "at", "by", "from", "up", "about", "into",
    "over", "under", "between", "through", "against", "out", "only", "now", "very",
    "even", "still", "just", "like", "down", "off", "across", "along", "behind", "beyond"
  )

  val commonVerbs = setOf(
    "be", "is", "are", "was", "were", "been", "being", "am",
    "have", "has", "had", "having",
    "do", "does", "did", "doing", "done",
    "will", "would", "can", "could", "should", "may", "might", "must", "shall", "ought",
    "say", "says", "said", "go", "goes", "went", "gone",
    "get", "gets", "got", "make", "makes", "made",
    "know", "knows", "knew", "known", "think", "thinks", "thought",
    "take", "takes", "took", "taken", "see", "sees", "saw", "seen",
    "come", "comes", "came", "want", "wants", "wanted",
    "look", "looks", "looked", "use", "uses", "used",
    "find", "finds", "found", "give", "gives", "gave", "given",
    "tell", "tells", "told", "work", "works", "worked",
    "call", "calls", "called", "try", "tries", "tried",
    "ask", "asks", "asked", "need", "needs", "needed",
    "feel", "feels", "felt", "become", "becomes", "became",
    "leave", "leaves", "left", "put", "puts", "mean", "means", "meant",
    "keep", "keeps", "kept", "let", "lets",
    "begin", "begins", "began", "begun", "seem", "seems", "seemed",
    "help", "helps", "helped", "talk", "talks", "talked",
    "turn", "turns", "turned", "start", "starts", "started",
    "show", "shows", "showed", "shown", "hear", "hears", "heard",
    "play", "plays", "played", "run", "runs", "ran",
    "move", "moves", "moved", "like", "likes", "liked",
    "live", "lives", "lived", "believe", "believes", "believed",
    "hold", "holds", "held", "bring", "brings", "brought",
    "happen", "happens", "happened", "write", "writes", "wrote", "written",
    "provide", "provides", "provided", "sit", "sits", "sat",
    "stand", "stands", "stood", "lose", "loses", "lost",
    "pay", "pays", "paid", "meet", "meets", "met",
    "include", "includes", "included", "continue", "continues", "continued",
    "set", "sets", "learn", "learns", "learned",
    "change", "changes", "changed", "lead", "leads", "led",
    "understand", "understands", "understood", "watch", "watches", "watched",
    "follow", "follows", "followed", "stop", "stops", "stopped",
    "create", "creates", "created", "speak", "speaks", "spoke", "spoken",
    "read", "reads", "allow", "allows", "allowed",
    "add", "adds", "added", "spend", "spends", "spent",
    "grow", "grows", "grew", "grown", "open", "opens", "opened",
    "walk", "walks", "walked", "win", "wins", "won",
    "offer", "offers", "offered", "remember", "remembers", "remembered",
    "love", "loves", "loved", "consider", "considers", "considered",
    "appear", "appears", "appeared", "buy", "buys", "bought",
    "wait", "waits", "waited", "serve", "serves", "served",
    "die", "dies", "died", "send", "sends", "sent",
    "expect", "expects", "expected", "build", "builds", "built",
    "stay", "stays", "stayed", "fall", "falls", "fell", "fallen",
    "cut", "cuts", "reach", "reaches", "reached",
    "kill", "kills", "killed", "remain", "remains", "remained"
  )

  val commonNouns = setOf(
    "time", "year", "people", "way", "day", "man", "thing", "woman", "life", "child",
    "world", "school", "state", "family", "student", "group", "country", "problem", "hand", "part",
    "place", "case", "week", "company", "system", "program", "question", "work", "government", "number",
    "night", "point", "home", "water", "room", "mother", "area", "money", "story", "fact",
    "month", "lot", "right", "study", "book", "eye", "job", "word", "business", "issue",
    "side", "kind", "head", "house", "service", "friend", "father", "power", "hour", "game",
    "line", "end", "member", "law", "car", "city", "community", "name", "president", "team",
    "minute", "idea", "kid", "body", "information", "back", "parent", "face", "level", "office",
    "door", "health", "person", "art", "war", "history", "party", "result", "change", "morning"
  )

  val pronounWords = setOf(
    "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us",
    "them", "my", "your", "his", "their", "our", "its", "this", "that", "these", "those",
    "who", "what", "which", "whom", "whose",
    "myself", "yourself", "himself", "herself", "itself", "ourselves",
    "themselves", "someone", "anyone", "everyone", "nobody", "something",
    "anything", "everything", "nothing"
  )

  val commonModifiers = setOf(
    "the", "a", "an", "not", "no", "good", "new", "first", "last", "long", "great", "little", "own",
    "other", "old", "right", "big", "high", "different", "small", "large", "next", "early",
    "young", "important", "few", "public", "bad", "same", "able", "very", "really", "already",
    "again", "often", "always", "never", "ever", "much", "more", "most", "many"
  )

  val verbDominantWords = setOf(
    "work", "call", "look", "help", "play", "run", "start", "turn", "talk", "watch",
    "stop", "walk", "love", "stay", "cut", "fall", "move", "need", "try", "use",
    "change", "pay", "visit", "open", "close", "study"
  )

  val nounDominantWords = setOf(
    "control", "plan", "result", "order", "place", "hand", "end", "line", "power",
    "service", "level", "face", "water", "house", "game", "name", "record", "step", "act"
  )

  fun isPronoun(word: String): Boolean = pronounWords.contains(word.lowercase(Locale.ROOT).trim())
  fun isConnector(word: String): Boolean = connectorWords.contains(word.lowercase(Locale.ROOT).trim())
  fun isVerb(word: String): Boolean = commonVerbs.contains(word.lowercase(Locale.ROOT).trim())
  fun isNoun(word: String): Boolean = commonNouns.contains(word.lowercase(Locale.ROOT).trim())
  fun isModifier(word: String): Boolean = commonModifiers.contains(word.lowercase(Locale.ROOT).trim())
  fun isVerbDominant(word: String): Boolean = verbDominantWords.contains(word.lowercase(Locale.ROOT).trim())
  fun isNounDominant(word: String): Boolean = nounDominantWords.contains(word.lowercase(Locale.ROOT).trim())

  private val curatedTranslations = mapOf(
    "the" to "el, la, los, las",
    "be" to "ser, estar",
    "and" to "y",
    "of" to "de",
    "to" to "a, hacia, para",
    "a" to "un, una",
    "in" to "en, dentro de",
    "have" to "tener, haber",
    "it" to "eso, ello",
    "you" to "tú, usted, ustedes",
    "he" to "él",
    "for" to "para, por",
    "they" to "ellos, ellas",
    "not" to "no",
    "that" to "ese, esa, que",
    "we" to "nosotros, nosotras",
    "on" to "sobre, en",
    "with" to "con",
    "this" to "este, esta, esto",
    "i" to "yo",
    "do" to "hacer",
    "as" to "como, mientras",
    "at" to "en, a",
    "she" to "ella",
    "but" to "pero, sino",
    "from" to "desde, de",
    "by" to "por, junto a",
    "will" to "voluntad / auxiliar futuro",
    "or" to "o",
    "say" to "decir",
    "go" to "ir",
    "make" to "hacer, fabricar",
    "can" to "poder",
    "like" to "gustar, como",
    "time" to "tiempo, vez",
    "no" to "no, ninguno",
    "just" to "solo, justo",
    "him" to "a él",
    "know" to "saber, conocer",
    "take" to "tomar, llevar",
    "people" to "gente, personas",
    "into" to "hacia dentro",
    "year" to "año",
    "your" to "tu, su",
    "good" to "bueno, bien",
    "some" to "algunos, algo de",
    "could" to "podría, pudo",
    "them" to "a ellos, les",
    "see" to "ver",
    "other" to "otro, otra",
    "than" to "que (comparación)",
    "then" to "entonces, luego",
    "now" to "ahora",
    "look" to "mirar, parecer",
    "only" to "solo, único",
    "come" to "venir",
    "its" to "su (de ello)",
    "over" to "sobre, terminado",
    "think" to "pensar, creer",
    "also" to "también",
    "back" to "espalda, atrás",
    "after" to "después de",
    "use" to "usar, utilizar",
    "two" to "dos",
    "how" to "cómo",
    "our" to "nuestro, nuestra",
    "work" to "trabajo, trabajar",
    "first" to "primero",
    "well" to "bien",
    "way" to "camino, manera",
    "even" to "incluso, par",
    "new" to "nuevo, nueva",
    "want" to "querer",
    "because" to "porque",
    "any" to "cualquier, alguno",
    "these" to "estos, estas",
    "give" to "dar",
    "day" to "día",
    "most" to "la mayoría, más",
    "us" to "a nosotros, nos",
    "is" to "es, está",
    "are" to "son, están",
    "was" to "era, fue, estaba",
    "were" to "eran, fueron, estaban",
    "been" to "sido, estado",
    "has" to "tiene, ha",
    "had" to "tenía, tuvo, había",
    "did" to "hizo",
    "water" to "agua",
    "food" to "comida, alimento",
    "house" to "casa, hogar",
    "school" to "escuela, colegio",
    "life" to "vida",
    "friend" to "amigo, amiga",
    "book" to "libro",
    "world" to "mundo",
    "family" to "familia",
    "night" to "noche",
    "money" to "dinero",
    "hand" to "mano",
    "part" to "parte",
    "place" to "lugar",
    "child" to "niño, hijo",
    "eye" to "ojo",
    "job" to "trabajo, empleo",
    "head" to "cabeza",
    "car" to "automóvil, carro",
    "city" to "ciudad",
    "name" to "nombre",
    "room" to "habitación, cuarto",
    "door" to "puerta",
    "air" to "aire",
    "word" to "palabra"
  )

  /**
   * Micro-ejemplos curados: estrictamente entre 2 y 4 palabras.
   */
  private val curatedExamples = mapOf(
    "the" to "the big world",
    "be" to "be happy today",
    "and" to "you and me",
    "of" to "piece of cake",
    "to" to "time to go",
    "a" to "have a seat",
    "in" to "in the room",
    "have" to "have some coffee",
    "it" to "it is true",
    "you" to "you can win",
    "he" to "he is ready",
    "for" to "ready for action",
    "they" to "they are here",
    "not" to "not right now",
    "that" to "that looks good",
    "we" to "we can try",
    "on" to "turn it on",
    "with" to "come with us",
    "this" to "look at this",
    "i" to "I can help",
    "do" to "do your best",
    "as" to "as you wish",
    "at" to "at the door",
    "she" to "she can sing",
    "but" to "simple but true",
    "from" to "from day one",
    "by" to "step by step",
    "will" to "will see soon",
    "or" to "now or never",
    "say" to "say it loud",
    "go" to "let us go",
    "make" to "make it happen",
    "can" to "you can fly",
    "like" to "I like that",
    "time" to "take your time",
    "no" to "no problem here",
    "just" to "just in time",
    "him" to "tell him now",
    "know" to "know the truth",
    "take" to "take a break",
    "people" to "people need hope",
    "into" to "look into this",
    "year" to "a great year",
    "your" to "save your work",
    "good" to "have good news",
    "some" to "need some help",
    "could" to "could be better",
    "them" to "see them now",
    "see" to "see you later",
    "other" to "the other day",
    "than" to "better than ever",
    "then" to "see you then",
    "now" to "start right now",
    "look" to "look at me",
    "only" to "only one way",
    "come" to "come here fast",
    "its" to "its true color",
    "over" to "game is over",
    "think" to "think about it",
    "also" to "also very good",
    "back" to "welcome back home",
    "after" to "after the rain",
    "use" to "use your mind",
    "two" to "just two days",
    "how" to "how are you",
    "our" to "this is our",
    "work" to "hard work pays",
    "first" to "first of all",
    "well" to "all is well",
    "way" to "find a way",
    "even" to "even better now",
    "new" to "a new start",
    "want" to "want to know",
    "because" to "because we can",
    "any" to "at any time",
    "these" to "try these out",
    "give" to "give me more",
    "day" to "have a day",
    "most" to "most of all",
    "us" to "join us today",
    "water" to "drink fresh water",
    "food" to "eat good food",
    "house" to "a warm house",
    "school" to "go to school",
    "life" to "enjoy your life",
    "friend" to "my best friend",
    "book" to "read a book",
    "world" to "around the world",
    "family" to "love your family",
    "night" to "good night all",
    "money" to "save some money",
    "hand" to "hold my hand",
    "part" to "part of life",
    "place" to "a quiet place",
    "child" to "a happy child",
    "eye" to "open your eyes",
    "job" to "get the job",
    "head" to "use your head",
    "car" to "drive my car",
    "city" to "visit the city",
    "name" to "what is your name",
    "room" to "in this room",
    "door" to "close the door",
    "air" to "breathe fresh air",
    "word" to "say the word"
  )

  fun getTranslation(lemma: String, partOfSpeech: PartOfSpeech): String {
    val clean = lemma.lowercase(Locale.ROOT).trim()

    // 1. Catálogo de Conectores (traducción pedagógica exacta)
    val connectorRule = ConnectorRulesCatalog.getRule(clean)
    if (connectorRule != null) return connectorRule.spanishTranslation

    // 2. Diccionario curado en memoria
    val curated = curatedTranslations[clean]
    if (curated != null) return curated

    // 3. Diccionario completo de las 2,809 palabras cargado desde ngsl_translations.json
    val fromFile = loadedTranslations[clean]
    if (!fromFile.isNullOrBlank()) return fromFile

    // 4. Traducción limpia sin prefijos
    return clean
  }

  /**
   * Genera o retorna un micro-ejemplo de MÁXIMO 3 o 4 palabras estrictamente.
   */
  fun getMicroExample(lemma: String, partOfSpeech: PartOfSpeech): String {
    val clean = lemma.lowercase(Locale.ROOT).trim()
    val match = curatedExamples[clean]
    if (match != null) return match

    return when (partOfSpeech) {
      PartOfSpeech.VERB -> "I $clean everyday" // 3 palabras
      PartOfSpeech.NOUN -> "a good $clean"    // 3 palabras
      PartOfSpeech.MODIFIER -> "the best $clean" // 3 palabras
      PartOfSpeech.CONNECTOR,
      PartOfSpeech.CONNECTOR_CLAUSE,
      PartOfSpeech.CONNECTOR_NOUN,
      PartOfSpeech.CONNECTOR_TRANSITION -> "$clean you can" // 3 palabras
      PartOfSpeech.PRONOUN -> "$clean can do"    // 3 palabras
      PartOfSpeech.OTHER -> "see $clean now"     // 3 palabras
    }
  }
}
