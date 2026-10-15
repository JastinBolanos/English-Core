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
  private val speechToTextManager: SpeechToTextManager? = null
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

  // Indica si la frase fue actualizada/reordenada y está lista para sonar en su nueva posición
  private val _isPhraseUpdated = MutableStateFlow(false)
  val isPhraseUpdated: StateFlow<Boolean> = _isPhraseUpdated.asStateFlow()

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
      result = result.filter { filter.matchesWord(it) }
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
    _isPhraseUpdated.value = true
    ttsManager.speak(word.lemma)
    viewModelScope.launch {
      repository.incrementWordUseCount(word.id, word.lemma)
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
    }
  }

  /**
   * Limpia todo el borrador en un solo toque (Papelera 🗑️).
   */
  fun clearDraft() {
    _draftWords.value = emptyList()
    _isPhraseUpdated.value = false
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
  }

  class Factory(
    private val repository: VocabularyRepository,
    private val ttsManager: TtsManager,
    private val speechToTextManager: SpeechToTextManager? = null
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return VocabularyViewModel(repository, ttsManager, speechToTextManager) as T
    }
  }
}
