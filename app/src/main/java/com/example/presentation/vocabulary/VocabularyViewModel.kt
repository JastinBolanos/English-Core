package com.example.presentation.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.tts.TtsManager
import com.example.domain.model.PartOfSpeech
import com.example.domain.model.Word
import com.example.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

import com.example.data.stt.SpeechToTextManager
import com.example.data.local.AlphabetStorage
import com.example.data.local.NumbersStorage
import com.example.data.local.PhrasesStorage
import com.example.presentation.alphabet.AlphabetLetter
import com.example.presentation.numbers.NumberEntry
import com.example.domain.model.EverydayPhrase
import com.example.domain.translation.TranslationManager
import com.example.domain.translation.TranslationState

/**
 * Filtro interactivo de sintaxis desde el Guiador Superior (Brújula de Aprendizaje).
 */
enum class SyntaxFilter(val displayName: String, val colorEmoji: String) {
  CONNECTOR("Conector", "🟡"),
  SUBJECT("Sujeto", "🟣"),
  VERB("Verbo", "🔵"),
  NOUN("Sustantivo", "🟢"),
  MODIFIER("Modificador", "⚪");

  companion object {
    val OBJECT get() = NOUN
  }

  fun matchesWord(word: Word): Boolean {
    val primary = word.getPrimaryCategory()
    return when (this) {
      CONNECTOR -> primary.isConnector
      SUBJECT -> primary == PartOfSpeech.PRONOUN
      VERB -> primary == PartOfSpeech.VERB
      NOUN -> primary == PartOfSpeech.NOUN
      MODIFIER -> primary == PartOfSpeech.MODIFIER
    }
  }
}

class VocabularyViewModel @Inject constructor(
  private val repository: VocabularyRepository,
  private val ttsManager: TtsManager,
  private val speechToTextManager: SpeechToTextManager? = null,
  private val translationManager: TranslationManager? = null,
  private val geminiChatService: com.example.data.gemini.GeminiChatService = com.example.data.gemini.GeminiChatService(),
  private val context: android.content.Context? = null
) : ViewModel() {

  // Estado de la bandeja de consulta a Gemini IA
  private val _showGeminiDialog = MutableStateFlow(false)
  val showGeminiDialog: StateFlow<Boolean> = _showGeminiDialog.asStateFlow()

  private val _geminiMessages = MutableStateFlow<List<com.example.data.gemini.GeminiChatMessage>>(emptyList())
  val geminiMessages: StateFlow<List<com.example.data.gemini.GeminiChatMessage>> = _geminiMessages.asStateFlow()

  private val _isGeminiLoading = MutableStateFlow(false)
  val isGeminiLoading: StateFlow<Boolean> = _isGeminiLoading.asStateFlow()

  private val _geminiApiKey = MutableStateFlow(
    try {
      com.example.BuildConfig.GEMINI_API_KEY.takeIf {
        it.isNotBlank() && it != "your_api_key_here" && it != "your_gemini_api_key_here"
      } ?: ""
    } catch (_: Throwable) {
      ""
    }
  )
  val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

  private val _geminiInputText = MutableStateFlow("")
  val geminiInputText: StateFlow<String> = _geminiInputText.asStateFlow()

  // Gestor del Abecedario (Contador de prácticas y subida al lienzo de pruebas)
  private val alphabetStorage = AlphabetStorage(context)
  private val _alphabetCounts = MutableStateFlow<Map<String, Int>>(alphabetStorage.loadCounts())
  val alphabetCounts: StateFlow<Map<String, Int>> = _alphabetCounts.asStateFlow()

  fun speakAlphabetLetter(letterItem: AlphabetLetter) {
    val newCount = alphabetStorage.incrementCount(letterItem.letter)
    _alphabetCounts.value = _alphabetCounts.value + (letterItem.letter to newCount)
    ttsManager.speak(letterItem.letter)
  }

  fun addAlphabetLetterToDraft(letterItem: AlphabetLetter) {
    val letter = letterItem.letter
    val newCount = alphabetStorage.incrementCount(letter)
    _alphabetCounts.value = _alphabetCounts.value + (letter to newCount)

    val letterWord = Word(
      id = (letter.hashCode().toLong() and 0x7FFFFFFFL),
      lemma = letter,
      partOfSpeech = if (letterItem.isVowel) PartOfSpeech.MODIFIER else PartOfSpeech.OTHER,
      rawPartOfSpeech = if (letterItem.isVowel) "vocal" else "consonante",
      spanishTranslation = letterItem.phoneticSp,
      microExample = letterItem.ipa,
      useCount = newCount
    )
    _draftWords.value = _draftWords.value + letterWord
    _isPhraseUpdated.value = true
    ttsManager.speak(letter)
    if (_isTranslationTrayExpanded.value) {
      translateCurrentDraft()
    }
  }

  fun restoreAlphabetCounts(counts: Map<String, Int>) {
    if (counts.isNotEmpty()) {
      alphabetStorage.saveCounts(counts)
      _alphabetCounts.value = counts
    }
  }

  // Gestor de Números del 1 al 100 (Contador de prácticas y subida al lienzo de pruebas)
  private val numbersStorage = NumbersStorage(context)
  private val _numberCounts = MutableStateFlow<Map<Int, Int>>(numbersStorage.loadCounts())
  val numberCounts: StateFlow<Map<Int, Int>> = _numberCounts.asStateFlow()

  fun speakNumber(entry: NumberEntry) {
    val newCount = numbersStorage.incrementCount(entry.number)
    _numberCounts.value = _numberCounts.value + (entry.number to newCount)
    ttsManager.speak(entry.word)
  }

  fun addNumberToDraft(entry: NumberEntry) {
    val newCount = numbersStorage.incrementCount(entry.number)
    _numberCounts.value = _numberCounts.value + (entry.number to newCount)

    val numWord = Word(
      id = (entry.number.toLong() + 900000L),
      lemma = entry.word,
      partOfSpeech = PartOfSpeech.MODIFIER,
      rawPartOfSpeech = if (entry.isDecade) "número (decena)" else "número",
      spanishTranslation = "${entry.number} (${entry.phoneticSp})",
      microExample = entry.ipa,
      useCount = newCount
    )
    _draftWords.value = _draftWords.value + numWord
    _isPhraseUpdated.value = true
    ttsManager.speak(entry.word)
    if (_isTranslationTrayExpanded.value) {
      translateCurrentDraft()
    }
  }

  fun restoreNumberCounts(counts: Map<Int, Int>) {
    if (counts.isNotEmpty()) {
      numbersStorage.saveCounts(counts)
      _numberCounts.value = counts
    }
  }

  // Gestor de Frases Cotidianas (Contador de prácticas y subida al lienzo de pruebas)
  private val phrasesStorage = PhrasesStorage(context)
  private val _phraseCounts = MutableStateFlow<Map<Int, Int>>(phrasesStorage.loadCounts())
  val phraseCounts: StateFlow<Map<Int, Int>> = _phraseCounts.asStateFlow()

  private fun persistPhrasesToAutoBackup() {
    val ctx = context ?: return
    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
      com.example.data.backup.AutoBackupManager.updatePhraseCountsInBackup(ctx, _phraseCounts.value)
    }
  }

  fun speakPhrase(phrase: EverydayPhrase) {
    val newCount = phrasesStorage.incrementCount(phrase.id)
    _phraseCounts.value = _phraseCounts.value + (phrase.id to newCount)
    ttsManager.speak(phrase.englishHint)
    persistPhrasesToAutoBackup()
  }

  fun addPhraseToDraft(phrase: EverydayPhrase) {
    val newCount = phrasesStorage.incrementCount(phrase.id)
    _phraseCounts.value = _phraseCounts.value + (phrase.id to newCount)

    val clean = phrase.englishHint.trim()
    if (clean.isNotBlank()) {
      val tokens = clean.split(Regex("[^a-zA-Z']+")).filter { it.isNotBlank() }
      val allWords = words.value
      val newWords = tokens.map { token ->
        val cleanToken = token.lowercase(java.util.Locale.ROOT)
        allWords.firstOrNull { it.lemma.equals(cleanToken, ignoreCase = true) }
          ?: Word(
            id = (cleanToken.hashCode().toLong() and 0x7FFFFFFFL),
            lemma = cleanToken,
            partOfSpeech = PartOfSpeech.OTHER,
            spanishTranslation = phrase.spanishText
          )
      }
      _draftWords.value = _draftWords.value + newWords
      _isPhraseUpdated.value = true
      ttsManager.speak(clean)
      if (_isTranslationTrayExpanded.value) {
        translateCurrentDraft()
      }
    }
    persistPhrasesToAutoBackup()
  }

  fun sendPhraseToTranslator(phrase: EverydayPhrase) {
    val newCount = phrasesStorage.incrementCount(phrase.id)
    _phraseCounts.value = _phraseCounts.value + (phrase.id to newCount)
    updateSpanishInputText(phrase.spanishText)
    translateSpanishNow()
    persistPhrasesToAutoBackup()
  }

  fun restorePhraseCounts(counts: Map<Int, Int>) {
    if (counts.isNotEmpty()) {
      phrasesStorage.saveCounts(counts)
      _phraseCounts.value = counts
      persistPhrasesToAutoBackup()
    }
  }

  fun restoreWordCounts(counts: Map<String, Int>) {
    if (counts.isNotEmpty()) {
      viewModelScope.launch {
        context?.let { com.example.data.local.VocabularyStorage(it).saveCounts(counts) }
        repository.setWordUseCounts(counts, resetOthersFirst = true)
      }
    }
  }

  fun restoreCustomWords(customWords: List<com.example.data.local.CustomWordRecord>) {
    if (customWords.isNotEmpty()) {
      viewModelScope.launch {
        repository.restoreCustomWords(customWords)
      }
    }
  }

  fun restoreFullBackup(
    data: com.example.data.backup.RestoredBackupData,
    context: android.content.Context,
    onComplete: (Boolean, String) -> Unit = { _, _ -> }
  ) {
    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
      try {
        if (data.notes.isNotEmpty()) {
          com.example.presentation.notes.LocalNotesStorage(context).saveNotes(data.notes)
        }
        if (data.customWords.isNotEmpty()) {
          repository.restoreCustomWords(data.customWords)
        }
        if (data.vocabularyWordCounts.isNotEmpty()) {
          val vocabStorage = com.example.data.local.VocabularyStorage(context)
          vocabStorage.saveCounts(data.vocabularyWordCounts)
          repository.setWordUseCounts(data.vocabularyWordCounts, resetOthersFirst = true)
        }
        if (data.alphabetCounts.isNotEmpty()) {
          alphabetStorage.saveCounts(data.alphabetCounts)
          _alphabetCounts.value = data.alphabetCounts
        }
        if (data.numberCounts.isNotEmpty()) {
          numbersStorage.saveCounts(data.numberCounts)
          _numberCounts.value = data.numberCounts
        }
        if (data.phraseCounts.isNotEmpty()) {
          phrasesStorage.saveCounts(data.phraseCounts)
          _phraseCounts.value = data.phraseCounts
        }
        com.example.data.backup.AutoBackupManager.saveFullBackup(
          context = context,
          vocabularyWordCounts = data.vocabularyWordCounts,
          customWords = data.customWords,
          usedWordsCount = data.usedWordsCount.takeIf { it > 0 } ?: data.vocabularyWordCounts.values.count { it > 0 },
          notes = data.notes,
          alphabetCounts = data.alphabetCounts,
          numberCounts = data.numberCounts,
          phraseCounts = data.phraseCounts
        )
        val activeWordsCount = data.vocabularyWordCounts.values.count { it > 0 }
        val totalWordUses = data.vocabularyWordCounts.values.sum()
        withContext(kotlinx.coroutines.Dispatchers.Main) {
          onComplete(
            true,
            "¡Progreso restaurado con éxito! $activeWordsCount copas, $totalWordUses usos de vocabulario, ${data.alphabetCounts.size} letras, ${data.numberCounts.size} números, ${data.phraseCounts.size} frases y ${data.notes.size} notas."
          )
        }
      } catch (e: Exception) {
        withContext(kotlinx.coroutines.Dispatchers.Main) {
          onComplete(false, "Error al restaurar: ${e.message}")
        }
      }
    }
  }

  // Gestor del Escritor Traductor de Español a Inglés (Bandeja bajo el Lienzo de Pruebas)
  private val spanishToEnglishTranslator = com.example.data.translation.SpanishToEnglishTranslationManager(geminiChatService)
  val spanishTranslationState: StateFlow<com.example.domain.translation.TranslationState> = spanishToEnglishTranslator.translationState

  private val _spanishInputText = MutableStateFlow("")
  val spanishInputText: StateFlow<String> = _spanishInputText.asStateFlow()

  private var spanishTranslationJob: kotlinx.coroutines.Job? = null

  fun updateSpanishInputText(text: String) {
    _spanishInputText.value = text
    val clean = text.trim()
    if (clean.isBlank()) {
      spanishTranslationJob?.cancel()
      spanishToEnglishTranslator.reset()
      return
    }

    spanishTranslationJob?.cancel()
    spanishTranslationJob = viewModelScope.launch {
      kotlinx.coroutines.delay(350)
      if (_spanishInputText.value.trim() != clean) return@launch
      spanishToEnglishTranslator.translate(clean, _geminiApiKey.value, viewModelScope)
    }
  }

  fun translateSpanishNow() {
    spanishTranslationJob?.cancel()
    val clean = _spanishInputText.value.trim()
    if (clean.isBlank()) return
    spanishToEnglishTranslator.translate(clean, _geminiApiKey.value, viewModelScope)
  }

  fun clearSpanishTranslator() {
    spanishTranslationJob?.cancel()
    _spanishInputText.value = ""
    spanishToEnglishTranslator.reset()
  }

  fun addSpanishTranslationToDraft(englishPhrase: String) {
    val clean = englishPhrase.trim()
    if (clean.isBlank()) return
    val tokens = clean.split(Regex("[^a-zA-Z']+")).filter { it.isNotBlank() }
    if (tokens.isEmpty()) return
    val allWords = words.value
    val newWords = tokens.map { token ->
      val cleanToken = token.lowercase(java.util.Locale.ROOT)
      allWords.firstOrNull { it.lemma.equals(cleanToken, ignoreCase = true) }
        ?: Word(
          id = (cleanToken.hashCode().toLong() and 0x7FFFFFFFL),
          lemma = cleanToken,
          partOfSpeech = PartOfSpeech.OTHER
        )
    }
    _draftWords.value = _draftWords.value + newWords
    _isPhraseUpdated.value = true
    ttsManager.speak(clean)
    viewModelScope.launch {
      newWords.forEach { w ->
        try {
          repository.incrementWordUseCount(w.id, w.lemma)
        } catch (_: Exception) {}
      }
    }
  }

  fun openGeminiDialog() {
    _showGeminiDialog.value = true
  }

  fun closeGeminiDialog() {
    _showGeminiDialog.value = false
  }

  fun updateGeminiInputText(text: String) {
    _geminiInputText.value = text
  }

  fun updateGeminiApiKey(key: String) {
    _geminiApiKey.value = key.trim()
  }

  fun clearGeminiChat() {
    _geminiMessages.value = emptyList()
  }

  fun sendGeminiQuery(customPrompt: String? = null) {
    val prompt = (customPrompt ?: _geminiInputText.value).trim()
    if (prompt.isBlank() || _isGeminiLoading.value) return

    val userMessage = com.example.data.gemini.GeminiChatMessage(
      text = prompt,
      isFromUser = true
    )
    val currentHistory = _geminiMessages.value
    _geminiMessages.value = currentHistory + userMessage
    _geminiInputText.value = ""
    _isGeminiLoading.value = true

    viewModelScope.launch {
      val key = _geminiApiKey.value.takeIf { it.isNotBlank() && it != "your_gemini_api_key_here" } ?: ""
      val result = geminiChatService.askGemini(currentHistory, prompt, key)
      _isGeminiLoading.value = false
      result.onSuccess { responseText ->
        _geminiMessages.value = _geminiMessages.value + com.example.data.gemini.GeminiChatMessage(
          text = responseText,
          isFromUser = false
        )
      }.onFailure { error ->
        _geminiMessages.value = _geminiMessages.value + com.example.data.gemini.GeminiChatMessage(
          text = "⚠️ Error: ${error.localizedMessage ?: "No se pudo conectar con Gemini"}",
          isFromUser = false
        )
      }
    }
  }

  fun askGeminiAboutWord(word: String) {
    openGeminiDialog()
    sendGeminiQuery("¿Cómo se usa la palabra '$word' en inglés? Dame su significado, categoría gramatical y 3 ejemplos breves con traducción.")
  }

  fun askGeminiAboutDraft() {
    val phrase = _draftWords.value.joinToString(" ") { it.lemma }
    if (phrase.isNotBlank()) {
      openGeminiDialog()
      sendGeminiQuery("Explícame la frase '$phrase'. ¿Es gramaticalmente correcta en inglés, qué significa exactamente y cómo sonaría de forma más natural?")
    }
  }

  // Estado para creación de palabras personalizadas (#2810+)
  private val _showAddWordDialog = MutableStateFlow(false)
  val showAddWordDialog: StateFlow<Boolean> = _showAddWordDialog.asStateFlow()

  fun openAddWordDialog() {
    _showAddWordDialog.value = true
  }

  fun closeAddWordDialog() {
    _showAddWordDialog.value = false
  }

  fun addNewCustomWord(
    word: String,
    category: String,
    spanishTranslation: String,
    microExample: String
  ) {
    val clean = word.trim()
    if (clean.isBlank()) return
    viewModelScope.launch {
      try {
        repository.addCustomWord(
          word = clean,
          partOfSpeech = category,
          spanishTranslation = spanishTranslation.trim(),
          microExample = microExample.trim()
        )
        _showAddWordDialog.value = false
      } catch (_: Exception) {}
    }
  }

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Traducción interna/mental de la búsqueda cuando el usuario escribe en español
  private val _translatedSearchQuery = MutableStateFlow("")
  val translatedSearchQuery: StateFlow<String> = _translatedSearchQuery.asStateFlow()

  // Filtro activo por categoría sintáctica desde el Guiador Superior
  private val _selectedSyntaxFilter = MutableStateFlow<SyntaxFilter?>(null)
  val selectedSyntaxFilter: StateFlow<SyntaxFilter?> = _selectedSyntaxFilter.asStateFlow()

  val selectedCategoryFilter: StateFlow<PartOfSpeech?> = _selectedSyntaxFilter
    .map { filter ->
      when (filter) {
        SyntaxFilter.CONNECTOR -> PartOfSpeech.CONNECTOR
        SyntaxFilter.SUBJECT -> PartOfSpeech.PRONOUN
        SyntaxFilter.VERB -> PartOfSpeech.VERB
        SyntaxFilter.NOUN -> PartOfSpeech.NOUN
        SyntaxFilter.MODIFIER -> PartOfSpeech.MODIFIER
        null -> null
      }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Estado del Borrador de Frases (Scratchpad / Lienzo de Pruebas)
  private val _draftWords = MutableStateFlow<List<Word>>(emptyList())
  val draftWords: StateFlow<List<Word>> = _draftWords.asStateFlow()

  // Indica si la frase fue actualizada/reordenada y está lista para sonar en su nueva posición
  private val _isPhraseUpdated = MutableStateFlow(false)
  val isPhraseUpdated: StateFlow<Boolean> = _isPhraseUpdated.asStateFlow()

  // Estado de la bandeja desplegable de traducción con Google ML Kit
  private val _isTranslationTrayExpanded = MutableStateFlow(false)
  val isTranslationTrayExpanded: StateFlow<Boolean> = _isTranslationTrayExpanded.asStateFlow()

  val translationState: StateFlow<TranslationState> = translationManager?.translationState
    ?: MutableStateFlow(TranslationState.Idle)

  // Control global de velocidad TTS (1.0f ➔ 0.8f ➔ 0.5f ➔ 1.0f)
  val currentSpeechSpeed: StateFlow<Float> = ttsManager.currentSpeechRate

  fun onToggleSpeed(): Float {
    return ttsManager.cycleSpeechRate()
  }

  /**
   * Lista completa de palabras cargadas desde Room Database.
   * Cumple con la Regla 1 (Orden de Combate): Frecuencia absoluta (rank ASC).
   */
  val words: StateFlow<List<Word>> = repository.getAllWords()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  /**
   * Contador reactivo de palabras únicas usadas al menos una vez (useCount >= 1).
   * Refleja cuántas palabras distintas han sido incorporadas o utilizadas por el usuario.
   */
  val usedWordsCount: StateFlow<Int> = words
    .map { list -> list.count { it.useCount > 0 } }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  /**
   * Contador reactivo de usos totales acumulados en todas las palabras del vocabulario.
   */
  val vocabularyTotalUses: StateFlow<Int> = words
    .map { list -> list.sumOf { it.useCount } }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  // ==========================================================
  // CRONÓMETRO NATIVO DE ESTUDIO DE INGLÉS
  // ==========================================================
  private val _studyTimerMillis = MutableStateFlow(0L)
  val studyTimerMillis: StateFlow<Long> = _studyTimerMillis.asStateFlow()

  private val _isStudyTimerRunning = MutableStateFlow(false)
  val isStudyTimerRunning: StateFlow<Boolean> = _isStudyTimerRunning.asStateFlow()

  private val _showResetTimerDialog = MutableStateFlow(false)
  val showResetTimerDialog: StateFlow<Boolean> = _showResetTimerDialog.asStateFlow()

  private var timerJob: kotlinx.coroutines.Job? = null
  private var timerBaseRealtime: Long = 0L
  private var timerAccumulatedMillis: Long = 0L

  fun toggleStudyTimer() {
    if (_isStudyTimerRunning.value) {
      pauseStudyTimer()
    } else {
      startStudyTimer()
    }
  }

  fun startStudyTimer() {
    if (_isStudyTimerRunning.value) return
    _isStudyTimerRunning.value = true
    timerBaseRealtime = android.os.SystemClock.elapsedRealtime()
    timerJob?.cancel()
    timerJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
      while (_isStudyTimerRunning.value) {
        val now = android.os.SystemClock.elapsedRealtime()
        _studyTimerMillis.value = timerAccumulatedMillis + (now - timerBaseRealtime)
        kotlinx.coroutines.delay(35)
      }
    }
  }

  fun pauseStudyTimer() {
    if (!_isStudyTimerRunning.value) return
    timerJob?.cancel()
    val now = android.os.SystemClock.elapsedRealtime()
    timerAccumulatedMillis += (now - timerBaseRealtime)
    _studyTimerMillis.value = timerAccumulatedMillis
    _isStudyTimerRunning.value = false
  }

  fun requestResetStudyTimer() {
    _showResetTimerDialog.value = true
  }

  fun dismissResetStudyTimerDialog() {
    _showResetTimerDialog.value = false
  }

  fun confirmResetStudyTimer() {
    timerJob?.cancel()
    _isStudyTimerRunning.value = false
    timerAccumulatedMillis = 0L
    timerBaseRealtime = 0L
    _studyTimerMillis.value = 0L
    _showResetTimerDialog.value = false
  }

  /**
   * Flujo filtrado por búsqueda y categoría seleccionada en el Guiador Educativo Superior.
   * Si el usuario escribe en español (ej. "estar"), el sistema traduce internamente y busca la palabra
   * en inglés ("be"), posicionándola en la primera fila, sin alterar el texto escrito por el usuario.
   * Si escribió en inglés nativo, busca directamente en la lista como siempre.
   */
  val filteredWords: StateFlow<List<Word>> = combine(
    words,
    _searchQuery,
    _translatedSearchQuery,
    _selectedSyntaxFilter
  ) { list: List<Word>, query: String, translatedQuery: String, filter: SyntaxFilter? ->
    var result = list
    if (filter != null) {
      result = result.filter { filter.matchesWord(it) }
    }
    if (query.isNotBlank()) {
      val trimmedQuery = query.trim().lowercase(java.util.Locale.ROOT)
      val cleanTranslated = translatedQuery.trim().lowercase(java.util.Locale.ROOT)
        .removePrefix("to ").trim()

      val isNativeEnglish = result.any { it.lemma.equals(trimmedQuery, ignoreCase = true) }

      if (isNativeEnglish || cleanTranslated.isBlank()) {
        val matches = result.filter { word ->
          word.lemma.contains(trimmedQuery, ignoreCase = true) ||
            (trimmedQuery == "am" && word.lemma.equals("be", ignoreCase = true)) ||
            (trimmedQuery == "is" && word.lemma.equals("be", ignoreCase = true)) ||
            (trimmedQuery == "are" && word.lemma.equals("be", ignoreCase = true)) ||
            (trimmedQuery == "was" && word.lemma.equals("be", ignoreCase = true)) ||
            (trimmedQuery == "were" && word.lemma.equals("be", ignoreCase = true)) ||
            (trimmedQuery.endsWith("ing") && trimmedQuery.length > 3 && word.lemma.equals(trimmedQuery.dropLast(3), ignoreCase = true)) ||
            (trimmedQuery.endsWith("ed") && trimmedQuery.length > 2 && word.lemma.equals(trimmedQuery.dropLast(2), ignoreCase = true)) ||
            word.displaySpanishTranslation.contains(trimmedQuery, ignoreCase = true)
        }
        result = matches.sortedWith(
          compareBy(
            { word ->
              val isExact = word.lemma.equals(trimmedQuery, ignoreCase = true) ||
                (trimmedQuery == "am" && word.lemma.equals("be", ignoreCase = true))
              if (isExact) 0 else 1
            },
            { word ->
              if (word.lemma.startsWith(trimmedQuery, ignoreCase = true)) 0 else 1
            },
            { word -> word.rank }
          )
        )
      } else {
        // Modo traducción interna de español a inglés (ej: "estar" -> "be" en primera fila)
        val translatedTokens = cleanTranslated.split(Regex("[^a-zA-Z']+")).filter { it.isNotBlank() }

        val matches = result.filter { word ->
          val lemmaLower = word.lemma.lowercase(java.util.Locale.ROOT)
          val spanishLower = word.displaySpanishTranslation.lowercase(java.util.Locale.ROOT)

          lemmaLower == cleanTranslated ||
            lemmaLower.contains(cleanTranslated) ||
            translatedTokens.any { lemmaLower == it || lemmaLower.contains(it) } ||
            (cleanTranslated == "be" && (lemmaLower == "be" || lemmaLower == "am" || lemmaLower == "is" || lemmaLower == "are")) ||
            spanishLower.contains(trimmedQuery) ||
            trimmedQuery.contains(spanishLower)
        }

        result = matches.sortedWith(
          compareBy(
            { word ->
              val lemmaLower = word.lemma.lowercase(java.util.Locale.ROOT)
              when {
                lemmaLower == cleanTranslated -> 0
                (cleanTranslated == "be" && lemmaLower == "be") -> 0
                translatedTokens.contains(lemmaLower) -> 1
                lemmaLower.startsWith(cleanTranslated) -> 2
                word.displaySpanishTranslation.equals(trimmedQuery, ignoreCase = true) -> 3
                word.displaySpanishTranslation.startsWith(trimmedQuery, ignoreCase = true) -> 4
                lemmaLower.contains(cleanTranslated) -> 5
                else -> 6
              }
            },
            { word -> word.rank }
          )
        )
      }
    }
    result
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  /**
   * Selecciona o alterna un filtro de sintaxis desde el Guiador Superior.
   */
  fun selectSyntaxFilter(filter: SyntaxFilter) {
    _selectedSyntaxFilter.value = if (_selectedSyntaxFilter.value == filter) null else filter
  }

  /**
   * Elimina todas las selecciones y restablece la lista completa de las 2,809 palabras.
   */
  fun clearSyntaxFilter() {
    _selectedSyntaxFilter.value = null
  }

  fun setCategoryFilter(category: PartOfSpeech?) {
    when {
      category == null -> clearSyntaxFilter()
      category.isConnector -> selectSyntaxFilter(SyntaxFilter.CONNECTOR)
      category == PartOfSpeech.PRONOUN -> selectSyntaxFilter(SyntaxFilter.SUBJECT)
      category == PartOfSpeech.VERB -> selectSyntaxFilter(SyntaxFilter.VERB)
      category == PartOfSpeech.NOUN -> selectSyntaxFilter(SyntaxFilter.NOUN)
      category == PartOfSpeech.MODIFIER -> selectSyntaxFilter(SyntaxFilter.MODIFIER)
      else -> clearSyntaxFilter()
    }
  }

  fun clearCategoryFilter() {
    clearSyntaxFilter()
  }

  init {
    viewModelScope.launch {
      try {
        _isLoading.value = true
        repository.prePopulateDatabaseIfNeeded()
        _isLoading.value = false
      } catch (_: Exception) {}
    }
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
    val clean = query.trim().lowercase(java.util.Locale.ROOT)
    if (clean.isBlank()) {
      _translatedSearchQuery.value = ""
      return
    }

    // Si coincide exactamente con un lemma en inglés nativo, no traducir
    val allWords = words.value
    val isExactEnglish = allWords.any { it.lemma.equals(clean, ignoreCase = true) }
    if (isExactEnglish) {
      _translatedSearchQuery.value = ""
      return
    }

    // Traducción inmediata y ligera usando nuestro catálogo y diccionario inverso en memoria
    val instantTrans = translateSpanishQueryToEnglish(clean)
    _translatedSearchQuery.value = instantTrans
  }

  /**
   * Alterna la visibilidad de la bandeja de traducción offline desplegable.
   * Si se abre, traduce inmediatamente la frase actual armada en el lienzo.
   */
  fun toggleTranslationTray() {
    val nextState = !_isTranslationTrayExpanded.value
    _isTranslationTrayExpanded.value = nextState
    if (nextState) {
      translateCurrentDraft()
    }
  }

  /**
   * Envía la frase completa al motor offline de Google ML Kit para traducirla al español.
   */
  fun translateCurrentDraft() {
    val phrase = _draftWords.value.joinToString(" ") { it.lemma }
    if (phrase.isNotBlank()) {
      translationManager?.translate(phrase)
    } else {
      translationManager?.reset()
    }
  }

  /**
   * onTap: Reproduce la palabra con TTS, la agrega al Borrador (Scratchpad)
   * e incrementa de forma persistente su contador de uso en SQLite (Room).
   */
  fun addWordToDraft(word: Word) {
    _draftWords.value = _draftWords.value + word
    _isPhraseUpdated.value = true
    ttsManager.speak(word.lemma)
    if (_isTranslationTrayExpanded.value) {
      translateCurrentDraft()
    }
    viewModelScope.launch {
      try {
        repository.incrementWordUseCount(word.id, word.lemma)
      } catch (_: Exception) {}
    }
  }

  /**
   * Desplaza o reordena un bloque de palabra de una posición a otra en el lienzo.
   * Marca la frase como actualizada para que el icono de Play notifique el cambio.
   */
  fun moveWordInDraft(fromIndex: Int, toIndex: Int) {
    val current = _draftWords.value
    if (fromIndex in current.indices && toIndex in current.indices && fromIndex != toIndex) {
      val updated = current.toMutableList()
      val item = updated.removeAt(fromIndex)
      updated.add(toIndex, item)
      _draftWords.value = updated
      _isPhraseUpdated.value = true
      if (_isTranslationTrayExpanded.value) {
        translateCurrentDraft()
      }
    }
  }

  /**
   * Mueve el bloque un paso hacia la izquierda en el lienzo.
   */
  fun moveWordLeft(index: Int) {
    if (index > 0) {
      moveWordInDraft(index, index - 1)
    }
  }

  /**
   * Mueve el bloque un paso hacia la derecha en el lienzo.
   */
  fun moveWordRight(index: Int) {
    if (index < _draftWords.value.lastIndex) {
      moveWordInDraft(index, index + 1)
    }
  }

  /**
   * Elimina un bloque/chip específico del borrador por índice.
   */
  fun removeWordFromDraft(index: Int) {
    if (index in _draftWords.value.indices) {
      val updated = _draftWords.value.toMutableList()
      updated.removeAt(index)
      _draftWords.value = updated
      _isPhraseUpdated.value = updated.isNotEmpty()
      if (_isTranslationTrayExpanded.value) {
        translateCurrentDraft()
      }
    }
  }

  /**
   * Limpia todo el borrador en un solo toque (Papelera 🗑️).
   */
  fun clearDraft() {
    _draftWords.value = emptyList()
    _isPhraseUpdated.value = false
    translationManager?.reset()
  }

  /**
   * Lee la frase completa armada en el borrador con la nueva disposición posicional.
   * Restablece el estado de actualización para indicar que la versión actual ya sonó.
   */
  fun playFullDraftPhrase() {
    val phrase = _draftWords.value.joinToString(" ") { it.lemma }
    if (phrase.isNotBlank()) {
      ttsManager.speak(phrase)
      _isPhraseUpdated.value = false
      _draftWords.value.forEach { recordWordUsage(it) }
    }
  }

  fun speakWord(word: Word) {
    ttsManager.speak(word.lemma)
    recordWordUsage(word)
  }

  fun speakWord(word: String) {
    ttsManager.speak(word)
    val tokens = word.split(Regex("[^a-zA-Z']+")).filter { it.isNotBlank() }
    if (tokens.size <= 1) {
      recordWordUsage(word)
    } else {
      tokens.forEach { recordWordUsage(it) }
    }
  }

  fun speakExample(word: Word, example: String) {
    ttsManager.speak(example)
    recordWordUsage(word)
  }

  fun recordWordUsage(word: Word) {
    viewModelScope.launch {
      try {
        repository.incrementWordUseCount(word.id, word.lemma)
      } catch (_: Exception) {}
    }
  }

  fun recordWordUsage(wordLemma: String) {
    val clean = wordLemma.trim()
    if (clean.isBlank()) return
    viewModelScope.launch {
      try {
        repository.incrementWordUseCount(clean)
      } catch (_: Exception) {}
    }
  }

  fun reloadFromCsv() {
    viewModelScope.launch {
      _isLoading.value = true
      repository.reloadDatabaseFromCsv()
      _isLoading.value = false
    }
  }

  private fun translateSpanishQueryToEnglish(query: String): String {
    val clean = query.trim().lowercase(java.util.Locale.ROOT)
    if (clean.isBlank()) return ""
    coreSpanishToEnglishMap[clean]?.let { return it }

    val reverse = com.example.domain.util.WordDictionary.getReverseTranslations()
    reverse[clean]?.let { return it }

    val tokens = clean.split(Regex("\\s+")).filter { it.isNotBlank() }
    if (tokens.size > 1) {
      val mappedTokens = tokens.mapNotNull { token ->
        coreSpanishToEnglishMap[token] ?: reverse[token]
      }
      if (mappedTokens.isNotEmpty()) {
        return mappedTokens.joinToString(" ")
      }
    }
    return ""
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
    ttsManager.stop()
    speechToTextManager?.destroy()
    translationManager?.close()
    spanishToEnglishTranslator.close()
  }

  companion object {
    private val coreSpanishToEnglishMap = mapOf(
      "estar" to "be",
      "ser" to "be",
      "estoy" to "be",
      "estas" to "be",
      "estás" to "be",
      "esta" to "be",
      "está" to "be",
      "estamos" to "be",
      "estan" to "be",
      "están" to "be",
      "estaba" to "be",
      "estuve" to "be",
      "estuvo" to "be",
      "sido" to "be",
      "siendo" to "be",
      "soy" to "be",
      "eres" to "be",
      "es" to "be",
      "somos" to "be",
      "son" to "be",
      "era" to "be",
      "fui" to "be",
      "fue" to "be",
      "haber" to "have",
      "hay" to "have",
      "habia" to "have",
      "había" to "have",
      "hubo" to "have",
      "tener" to "have",
      "tengo" to "have",
      "tienes" to "have",
      "tiene" to "have",
      "tenemos" to "have",
      "tienen" to "have",
      "tuve" to "have",
      "tuvo" to "have",
      "hacer" to "do",
      "hago" to "do",
      "haces" to "do",
      "hace" to "do",
      "hacemos" to "do",
      "hacen" to "do",
      "hice" to "do",
      "hizo" to "do",
      "hecho" to "do",
      "ir" to "go",
      "voy" to "go",
      "vas" to "go",
      "va" to "go",
      "vamos" to "go",
      "van" to "go",
      "fui" to "go",
      "fue" to "go",
      "ido" to "go",
      "decir" to "say",
      "digo" to "say",
      "dices" to "say",
      "dice" to "say",
      "decimos" to "say",
      "dicen" to "say",
      "dije" to "say",
      "dijo" to "say",
      "dicho" to "say",
      "ver" to "see",
      "veo" to "see",
      "ves" to "see",
      "ve" to "see",
      "vemos" to "see",
      "ven" to "see",
      "vi" to "see",
      "vio" to "see",
      "visto" to "see",
      "dar" to "give",
      "doy" to "give",
      "das" to "give",
      "da" to "give",
      "damos" to "give",
      "dan" to "give",
      "di" to "give",
      "dio" to "give",
      "dado" to "give",
      "saber" to "know",
      "se" to "know",
      "sé" to "know",
      "sabes" to "know",
      "sabe" to "know",
      "sabemos" to "know",
      "saben" to "know",
      "supe" to "know",
      "supo" to "know",
      "sabido" to "know",
      "poder" to "can",
      "puedo" to "can",
      "puedes" to "can",
      "puede" to "can",
      "podemos" to "can",
      "pueden" to "can",
      "pude" to "can",
      "pudo" to "can",
      "querer" to "want",
      "quiero" to "want",
      "quieres" to "want",
      "quiere" to "want",
      "queremos" to "want",
      "quieren" to "want",
      "quise" to "want",
      "quiso" to "want",
      "buscar" to "look",
      "busco" to "look",
      "buscas" to "look",
      "busca" to "look",
      "buscamos" to "look",
      "buscan" to "look",
      "comer" to "eat",
      "como" to "eat",
      "comes" to "eat",
      "come" to "eat",
      "comemos" to "eat",
      "comen" to "eat",
      "comida" to "food",
      "gato" to "cat",
      "gatos" to "cat",
      "perro" to "dog",
      "perros" to "dog",
      "hola" to "hello",
      "gracias" to "thank",
      "por favor" to "please",
      "amigo" to "friend",
      "amigos" to "friend",
      "amiga" to "friend",
      "amigas" to "friend",
      "casa" to "house",
      "casas" to "house",
      "tiempo" to "time",
      "año" to "year",
      "anos" to "year",
      "años" to "year",
      "dia" to "day",
      "día" to "day",
      "dias" to "day",
      "días" to "day",
      "noche" to "night",
      "noches" to "night",
      "hombre" to "man",
      "hombres" to "man",
      "mujer" to "woman",
      "mujeres" to "woman",
      "niño" to "child",
      "niños" to "child",
      "chico" to "boy",
      "chica" to "girl",
      "agua" to "water",
      "bueno" to "good",
      "buena" to "good",
      "malo" to "bad",
      "mala" to "bad",
      "nuevo" to "new",
      "nueva" to "new",
      "grande" to "big",
      "grandes" to "big",
      "pequeño" to "small",
      "pequeno" to "small",
      "pequeña" to "small",
      "pequena" to "small"
    )
  }

  class Factory(
    private val repository: VocabularyRepository,
    private val ttsManager: TtsManager,
    private val speechToTextManager: SpeechToTextManager? = null,
    private val translationManager: TranslationManager? = null,
    private val context: android.content.Context? = null
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val tManager = translationManager ?: com.example.data.translation.MlKitTranslationManager()
      return VocabularyViewModel(repository, ttsManager, speechToTextManager, tManager, context = context) as T
    }
  }
}
