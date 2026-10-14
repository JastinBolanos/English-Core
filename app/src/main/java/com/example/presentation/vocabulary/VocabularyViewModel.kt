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

import com.example.data.stt.SpeechRecognitionState
import com.example.data.stt.SpeechToTextManager
import com.example.domain.util.PronunciationEvaluation
import com.example.domain.util.PronunciationEvaluator

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
}

class VocabularyViewModel @Inject constructor(
  private val repository: VocabularyRepository,
  private val ttsManager: TtsManager,
  private val speechToTextManager: SpeechToTextManager
) : ViewModel() {

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

  // Estado del Motor de Voz y Evaluación de Pronunciación (Bandeja Espejo)
  val speechState: StateFlow<SpeechRecognitionState> = speechToTextManager.state
  val partialSpokenText: StateFlow<String> = speechToTextManager.partialText

  val isListening: StateFlow<Boolean> = speechToTextManager.state
    .map { it is SpeechRecognitionState.Listening }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  private val _pronunciationEvaluation = MutableStateFlow<PronunciationEvaluation?>(null)
  val pronunciationEvaluation: StateFlow<PronunciationEvaluation?> = _pronunciationEvaluation.asStateFlow()

  private val _isMirrorTrayVisible = MutableStateFlow(false)
  val isMirrorTrayVisible: StateFlow<Boolean> = _isMirrorTrayVisible.asStateFlow()

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
   * manteniendo estrictamente el orden de combate (rank ASC).
   */
  val filteredWords: StateFlow<List<Word>> = combine(
    words,
    _searchQuery,
    _selectedSyntaxFilter
  ) { list: List<Word>, query: String, filter: SyntaxFilter? ->
    var result = list
    if (filter != null) {
      result = result.filter { word ->
        when (filter) {
          SyntaxFilter.CONNECTOR -> word.matchesCategory(PartOfSpeech.CONNECTOR)
          SyntaxFilter.SUBJECT -> word.matchesCategory(PartOfSpeech.PRONOUN)
          SyntaxFilter.VERB -> word.matchesCategory(PartOfSpeech.VERB)
          SyntaxFilter.NOUN -> word.matchesCategory(PartOfSpeech.NOUN)
          SyntaxFilter.MODIFIER -> word.matchesCategory(PartOfSpeech.MODIFIER)
        }
      }
    }
    if (query.isNotBlank()) {
      result = result.filter { it.lemma.contains(query.trim(), ignoreCase = true) }
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
      _isLoading.value = true
      repository.prePopulateDatabaseIfNeeded()
      _isLoading.value = false
    }

    viewModelScope.launch {
      speechToTextManager.state.collect { state ->
        when (state) {
          is SpeechRecognitionState.Listening -> {
            _isMirrorTrayVisible.value = true
          }
          is SpeechRecognitionState.Results -> {
            _isMirrorTrayVisible.value = true
            evaluateCurrentDraft(state.text)
          }
          is SpeechRecognitionState.Error -> {
            _isMirrorTrayVisible.value = true
          }
          is SpeechRecognitionState.Idle -> {
            // Se mantiene visible si hay una evaluación previa activa
          }
        }
      }
    }
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
  }

  /**
   * onTap: Reproduce la palabra con TTS, la agrega al Borrador (Scratchpad)
   * e incrementa de forma persistente su contador de uso en SQLite (Room).
   */
  fun addWordToDraft(word: Word) {
    _draftWords.value = _draftWords.value + word
    ttsManager.speak(word.lemma)
    viewModelScope.launch {
      repository.incrementWordUseCount(word.id, word.lemma)
    }
    // Si ya había una evaluación, recalcular contra la nueva frase extendida
    _pronunciationEvaluation.value?.let { currentEval ->
      evaluateCurrentDraft(currentEval.spokenText)
    }
  }

  /**
   * Elimina un bloque/chip específico del borrador por índice.
   * Regla de Oro: Si la frase queda vacía, limpia y oculta la bandeja espejo sin fricción.
   */
  fun removeWordFromDraft(index: Int) {
    if (index in _draftWords.value.indices) {
      val updated = _draftWords.value.toMutableList()
      updated.removeAt(index)
      _draftWords.value = updated

      if (updated.isEmpty()) {
        speechToTextManager.reset()
        _pronunciationEvaluation.value = null
        _isMirrorTrayVisible.value = false
      } else {
        _pronunciationEvaluation.value?.let { currentEval ->
          evaluateCurrentDraft(currentEval.spokenText)
        }
      }
    }
  }

  /**
   * Limpia todo el borrador en un solo toque (Papelera 🗑️).
   * Cero fricción: borra la frase, detiene cualquier escucha activa, limpia la transcripción
   * y cierra de inmediato la Bandeja Espejo.
   */
  fun clearDraft() {
    _draftWords.value = emptyList()
    speechToTextManager.reset()
    _pronunciationEvaluation.value = null
    _isMirrorTrayVisible.value = false
  }

  /**
   * Lee la frase completa armada en el borrador de corrido con TTS.
   */
  fun playFullDraftPhrase() {
    val phrase = _draftWords.value.joinToString(" ") { it.lemma }
    if (phrase.isNotBlank()) {
      ttsManager.speak(phrase)
    }
  }

  fun speakWord(word: String) {
    ttsManager.speak(word)
  }

  /**
   * Alterna la escucha del motor de reconocimiento de voz.
   */
  fun toggleSpeechRecognition() {
    if (speechToTextManager.state.value is SpeechRecognitionState.Listening) {
      speechToTextManager.stopListening()
    } else {
      startSpeechRecognition()
    }
  }

  /**
   * Inicia el evaluador de voz en la Bandeja Espejo.
   */
  fun startSpeechRecognition() {
    if (_draftWords.value.isEmpty()) return
    _isMirrorTrayVisible.value = true
    _pronunciationEvaluation.value = null
    speechToTextManager.startListening()
  }

  fun stopSpeechRecognition() {
    speechToTextManager.stopListening()
  }

  fun closeMirrorTray() {
    speechToTextManager.stopListening()
    _isMirrorTrayVisible.value = false
    _pronunciationEvaluation.value = null
  }

  private fun evaluateCurrentDraft(spokenText: String) {
    val targetWords = _draftWords.value.map { it.lemma }
    if (targetWords.isNotEmpty() && spokenText.isNotBlank()) {
      _pronunciationEvaluation.value = PronunciationEvaluator.evaluate(targetWords, spokenText)
    }
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
    speechToTextManager.destroy()
  }

  class Factory(
    private val repository: VocabularyRepository,
    private val ttsManager: TtsManager,
    private val speechToTextManager: SpeechToTextManager
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return VocabularyViewModel(repository, ttsManager, speechToTextManager) as T
    }
  }
}
