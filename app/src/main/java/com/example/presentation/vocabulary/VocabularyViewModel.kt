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
import javax.inject.Inject

import com.example.data.stt.SpeechToTextManager

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
    if (com.example.BuildConfig.GEMINI_API_KEY == "your_gemini_api_key_here") "" else com.example.BuildConfig.GEMINI_API_KEY
  )
  val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

  private val _geminiInputText = MutableStateFlow("")
  val geminiInputText: StateFlow<String> = _geminiInputText.asStateFlow()

  // Gestor del Escritor Traductor de Español a Inglés (Bandeja bajo el Lienzo de Pruebas)
  private val spanishToEnglishTranslator = com.example.data.translation.SpanishToEnglishTranslationManager(geminiChatService)
  val spanishTranslationState: StateFlow<com.example.domain.translation.TranslationState> = spanishToEnglishTranslator.translationState

  private val _spanishInputText = MutableStateFlow("")
  val spanishInputText: StateFlow<String> = _spanishInputText.asStateFlow()

  fun updateSpanishInputText(text: String) {
    _spanishInputText.value = text
    val key = _geminiApiKey.value.takeIf { it.isNotBlank() && it != "your_gemini_api_key_here" }
      ?: com.example.BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "your_gemini_api_key_here" }
      ?: ""
    spanishToEnglishTranslator.translate(text, key, viewModelScope)
  }

  fun translateSpanishNow() {
    val key = _geminiApiKey.value.takeIf { it.isNotBlank() && it != "your_gemini_api_key_here" }
      ?: com.example.BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "your_gemini_api_key_here" }
      ?: ""
    spanishToEnglishTranslator.translate(_spanishInputText.value, key, viewModelScope)
  }

  fun clearSpanishTranslator() {
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
      val key = _geminiApiKey.value.takeIf { it.isNotBlank() && it != "your_gemini_api_key_here" }
        ?: com.example.BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "your_gemini_api_key_here" }
        ?: ""
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

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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
   * Flujo filtrado por búsqueda y categoría seleccionada en el Guiador Educativo Superior,
   * manteniendo el orden y priorizando la coincidencia exacta en primera fila al buscar.
   */
  val filteredWords: StateFlow<List<Word>> = combine(
    words,
    _searchQuery,
    _selectedSyntaxFilter
  ) { list: List<Word>, query: String, filter: SyntaxFilter? ->
    var result = list
    if (filter != null) {
      result = result.filter { filter.matchesWord(it) }
    }
    if (query.isNotBlank()) {
      val trimmed = query.trim()
      val matches = result.filter { word ->
        word.lemma.contains(trimmed, ignoreCase = true) ||
          (trimmed.equals("am", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true)) ||
          (trimmed.equals("is", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true)) ||
          (trimmed.equals("are", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true)) ||
          (trimmed.equals("was", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true)) ||
          (trimmed.equals("were", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true)) ||
          (trimmed.endsWith("ing", ignoreCase = true) && trimmed.length > 3 && word.lemma.equals(trimmed.dropLast(3), ignoreCase = true)) ||
          (trimmed.endsWith("ed", ignoreCase = true) && trimmed.length > 2 && word.lemma.equals(trimmed.dropLast(2), ignoreCase = true))
      }
      result = matches.sortedWith(
        compareBy(
          { word ->
            val isExact = word.lemma.equals(trimmed, ignoreCase = true) ||
              (trimmed.equals("am", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true)) ||
              (trimmed.equals("is", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true)) ||
              (trimmed.equals("are", ignoreCase = true) && word.lemma.equals("be", ignoreCase = true))
            if (isExact) 0 else 1
          },
          { word ->
            if (word.lemma.startsWith(trimmed, ignoreCase = true)) 0 else 1
          },
          { word -> word.rank }
        )
      )
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
    }
  }

  fun speakWord(word: String) {
    ttsManager.speak(word)
  }

  fun reloadFromCsv() {
    viewModelScope.launch {
      _isLoading.value = true
      repository.reloadDatabaseFromCsv()
      _isLoading.value = false
    }
  }

  override fun onCleared() {
    super.onCleared()
    ttsManager.stop()
    speechToTextManager?.destroy()
    translationManager?.close()
    spanishToEnglishTranslator.close()
  }

  class Factory(
    private val repository: VocabularyRepository,
    private val ttsManager: TtsManager,
    private val speechToTextManager: SpeechToTextManager? = null,
    private val translationManager: TranslationManager? = null
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val tManager = translationManager ?: com.example.data.translation.MlKitTranslationManager()
      return VocabularyViewModel(repository, ttsManager, speechToTextManager, tManager) as T
    }
  }
}
