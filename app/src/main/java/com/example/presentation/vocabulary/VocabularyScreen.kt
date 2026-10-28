package com.example.presentation.vocabulary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import com.example.presentation.youtube.RawYouTubeTray
import com.example.presentation.youtube.FloatingYouTubeTray
import com.example.presentation.gemini.FloatingGeminiWebTray
import com.example.presentation.translate.FloatingGoogleTranslateWebTray
import com.example.presentation.roadmap.FloatingRoadmap8MonthsTray
import com.example.presentation.roadmap.MinimalistDrawing
import com.example.presentation.schedule.FloatingDailyScheduleTray
import com.example.presentation.notes.FloatingLocalNotesTray
import com.example.presentation.alphabet.AlphabetListSection
import com.example.presentation.numbers.NumbersSection
import com.example.presentation.settings.FloatingSettingsBackupTray
import com.example.presentation.notes.LocalNotesStorage
import com.example.presentation.notes.NoteBlock
import com.example.data.backup.AutoBackupManager
import com.example.data.local.CustomWordRecord
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudSync
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import com.example.data.local.SavedDraftPhrase
import com.example.data.local.SavedPhrasesStorage
import com.example.presentation.phrases.FloatingSavedPhrasesTray
import com.example.data.wallpaper.WallpaperManager
import com.example.data.wallpaper.WallpaperStorage
import com.example.presentation.wallpaper.FloatingWallpaperTray
import com.example.domain.translation.TranslationState
import com.example.domain.model.EverydayPhrase
import com.example.domain.model.EverydayPhrasesCatalog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextButton

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import com.example.ui.theme.FrederickaTheGreatFontFamily
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import kotlin.math.roundToInt
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import com.example.R
import com.example.domain.model.PartOfSpeech
import com.example.domain.model.Word

/**
 * SISTEMA DE COLORES DE SINTAXIS PASIVA Y CONECTORES
 *
 * 🟡 CONNECTOR_CLAUSE     (Amarillo #FBBF24) -> Exige Sujeto + Verbo
 * 🟠 CONNECTOR_NOUN       (Naranja #FB923C)  -> Exige Sustantivo o Verbo-ING
 * 🟢 CONNECTOR_TRANSITION (Dorado #F59E0B)   -> Exige [ , ] + Sujeto + Verbo
 * 🟣 PRONOUN              (Morado #C084FC)   -> Sujeto
 * 🔵 VERB                 (Azul #60A5FA)     -> Acción / Motor
 * ⚪ NOUN                 (Menta #A7F3D0)    -> Sustantivo / Objeto
 * ⚪ OTHER                (Gris #E2E8F0)     -> Otro
 */
fun getWordSyntaxColor(partOfSpeech: PartOfSpeech): Color {
  return when (partOfSpeech) {
    PartOfSpeech.CONNECTOR,
    PartOfSpeech.CONNECTOR_CLAUSE,
    PartOfSpeech.CONNECTOR_NOUN,
    PartOfSpeech.CONNECTOR_TRANSITION -> Color(0xFFFACC15) // Amarillo #FACC15
    PartOfSpeech.PRONOUN -> Color(0xFFA855F7)              // Morado #A855F7
    PartOfSpeech.VERB -> Color(0xFF3B82F6)                 // Azul #3B82F6
    PartOfSpeech.NOUN -> Color(0xFF34D399)                 // Verde Esmeralda / Menta #34D399
    PartOfSpeech.MODIFIER -> Color(0xFFF8FAFC)             // Blanco #F8FAFC
    PartOfSpeech.OTHER -> Color(0xFFF8FAFC)                // Blanco suave
  }
}

/**
 * Obtiene el color de la palabra basado ESTRICTAMENTE en su categoría principal.
 */
fun getWordSyntaxColor(word: Word): Color {
  return getWordSyntaxColor(word.getPrimaryCategory())
}

/**
 * Selector de bandeja principal entre el Vocabulario (2,809 palabras)
 * y la lista de Frases cotidianas y del hogar (50 frases prearmadas).
 */
enum class MainSectionTab {
  VOCABULARY,
  ALPHABET,
  NUMBERS,
  PHRASES
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VocabularyScreen(
  viewModel: VocabularyViewModel,
  onBack: () -> Unit = {},
  wallpaperManager: WallpaperManager? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeWallpaperManager = wallpaperManager ?: remember(context) {
    val ctx = context.applicationContext
    val storage = WallpaperStorage(ctx)
    WallpaperManager(storage)
  }
  var selectedSectionTab by rememberSaveable { mutableStateOf(MainSectionTab.VOCABULARY) }

  BackHandler(enabled = (selectedSectionTab != MainSectionTab.VOCABULARY)) {
    selectedSectionTab = MainSectionTab.VOCABULARY
  }
  BackHandler(enabled = (selectedSectionTab == MainSectionTab.VOCABULARY), onBack = onBack)

  val words by viewModel.words.collectAsStateWithLifecycle()
  val filteredWords by viewModel.filteredWords.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val draftWords by viewModel.draftWords.collectAsStateWithLifecycle()
  val canUndoDraft by viewModel.canUndoDraft.collectAsStateWithLifecycle()
  val canRedoDraft by viewModel.canRedoDraft.collectAsStateWithLifecycle()
  val isSpanishToEnglish by viewModel.isSpanishToEnglish.collectAsStateWithLifecycle()
  val isPhraseUpdated by viewModel.isPhraseUpdated.collectAsStateWithLifecycle()
  val isTranslationTrayExpanded by viewModel.isTranslationTrayExpanded.collectAsStateWithLifecycle()
  val translationState by viewModel.translationState.collectAsStateWithLifecycle()
  val selectedSyntaxFilter by viewModel.selectedSyntaxFilter.collectAsStateWithLifecycle()
  val selectedSubCategory by viewModel.selectedSubCategory.collectAsStateWithLifecycle()
  val currentSpeechSpeed by viewModel.currentSpeechSpeed.collectAsStateWithLifecycle()
  val spanishInputText by viewModel.spanishInputText.collectAsStateWithLifecycle()
  val spanishTranslationState by viewModel.spanishTranslationState.collectAsStateWithLifecycle()
  val showAddWordDialog by viewModel.showAddWordDialog.collectAsStateWithLifecycle()
  val editingCustomWord by viewModel.editingCustomWord.collectAsStateWithLifecycle()
  val wordToDelete by viewModel.wordToDelete.collectAsStateWithLifecycle()
  val nextRank = remember(words) { maxOf(2811, words.maxOfOrNull { it.rank } ?: 2811) + 1 }
  val usedWordsCount by viewModel.usedWordsCount.collectAsStateWithLifecycle()
  val vocabularyTotalUses by viewModel.vocabularyTotalUses.collectAsStateWithLifecycle()
  val alphabetCounts by viewModel.alphabetCounts.collectAsStateWithLifecycle()
  val numberCounts by viewModel.numberCounts.collectAsStateWithLifecycle()
  val phraseCounts by viewModel.phraseCounts.collectAsStateWithLifecycle()
  val haptic = LocalHapticFeedback.current
  var isSpanishTranslatorCardVisible by rememberSaveable { mutableStateOf(false) }
  var isCloudSaving by remember { mutableStateOf(false) }
  var backupStatusMessage by rememberSaveable { mutableStateOf<String?>(null) }

  // Disparador reactivo de celebración de logro premium cuando una palabra pasa de 0 a 1 contador
  var celebrationTrigger by remember { mutableLongStateOf(0L) }

  // Escuchar evento inmediato desde ViewModel únicamente al tocar/llamar una palabra con contador 0
  LaunchedEffect(Unit) {
    viewModel.wordMasteredCelebrationEvent.collectLatest {
      celebrationTrigger = System.currentTimeMillis()
    }
  }

  // Auto-backup reactivo al detectar cambios en vocabulario, abecedario, números o frases
  LaunchedEffect(usedWordsCount, alphabetCounts, numberCounts, phraseCounts) {
    if (usedWordsCount > 0 || alphabetCounts.isNotEmpty() || numberCounts.isNotEmpty() || phraseCounts.isNotEmpty()) {
      delay(1200)
      val allWords = viewModel.words.value
      if (allWords.isNotEmpty()) {
        isCloudSaving = true
        delay(350)
        val vocabCounts = allWords.associate { it.lemma to it.useCount }
        val customWords = allWords.filter { it.rank > 2811 }.map { w ->
          CustomWordRecord(
            rank = w.rank,
            word = w.lemma,
            partOfSpeech = w.rawPartOfSpeech.ifBlank { w.partOfSpeech.name },
            spanishTranslation = w.spanishTranslation,
            microExample = w.microExample,
            useCount = w.useCount
          )
        }
        AutoBackupManager.saveFullBackup(
          context = context,
          vocabularyWordCounts = vocabCounts,
          allWordsDetails = allWords,
          customWords = customWords,
          usedWordsCount = usedWordsCount,
          notes = LocalNotesStorage(context).loadNotes(),
          alphabetCounts = alphabetCounts,
          numberCounts = numberCounts,
          phraseCounts = phraseCounts,
          savedDraftPhrases = SavedPhrasesStorage(context).loadSavedPhrases(),
          wallpaperId = activeWallpaperManager.selectedWallpaper.value.id,
          wallpaperDimAlpha = activeWallpaperManager.dimAlpha.value
        )
        isCloudSaving = false
      }
    }
  }

  // Auto-recuperación si la app fue reinstalada pero el archivo persiste en Documentos/VocabFlow/
  LaunchedEffect(Unit) {
    val notesStorage = LocalNotesStorage(context)
    val existingNotes = notesStorage.loadNotes()
    if (existingNotes.isEmpty() && usedWordsCount == 0 && AutoBackupManager.hasSurvivingBackup(context)) {
      val restored = AutoBackupManager.loadLatestBackup(context)
      if (restored != null) {
        viewModel.restoreFullBackup(restored, context) { _, msg ->
          backupStatusMessage = msg
          if (restored.wallpaperId != null) {
            activeWallpaperManager.applyWallpaperSettings(
              wallpaperId = restored.wallpaperId,
              dimAlpha = restored.wallpaperDimAlpha
            )
          } else {
            activeWallpaperManager.refresh()
          }
        }
      }
    }
  }
  var explainedFilter by rememberSaveable { mutableStateOf<SyntaxFilter?>(null) }
  var currentVideoId by rememberSaveable { mutableStateOf<String?>("84kLpP7j9iQ") }
  var isVideoPlayerVisible by rememberSaveable { mutableStateOf(false) }

  fun onVideoSelected(videoId: String) {
    currentVideoId = videoId
    isVideoPlayerVisible = true
  }

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  var jumpRankInput by rememberSaveable { mutableStateOf("") }

  var isYouTubeTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isGeminiWebTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isGoogleTranslateWebTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isRoadmap8MonthsTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isDailyScheduleTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isLocalNotesTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isSettingsTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isProficiencyLevelsTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isWallpaperTrayOpen by rememberSaveable { mutableStateOf(false) }
  var isSavedPhrasesTrayOpen by rememberSaveable { mutableStateOf(false) }
  val currentWallpaper by activeWallpaperManager.selectedWallpaper.collectAsStateWithLifecycle()

  // SAF Launcher para exportar copia .json
  val exportBackupLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/json")
  ) { uri ->
    if (uri != null) {
      try {
        val notesStorage = LocalNotesStorage(context)
        val currentNotes = notesStorage.loadNotes()
        val allWords = viewModel.words.value
        val vocabCounts = allWords.associate { it.lemma to it.useCount }
        val customWords = allWords.filter { it.rank > 2811 }.map { w ->
          CustomWordRecord(
            rank = w.rank,
            word = w.lemma,
            partOfSpeech = w.rawPartOfSpeech.ifBlank { w.partOfSpeech.name },
            spanishTranslation = w.spanishTranslation,
            microExample = w.microExample,
            useCount = w.useCount
          )
        }

        val completeAlphabetCounts = com.example.data.backup.standardAlphabetCatalog.associate { std ->
          std.letter to (alphabetCounts[std.letter] ?: 0)
        }

        val completeNumbersCounts = com.example.data.backup.standardNumbersCatalog.associate { std ->
          std.number to (numberCounts[std.number] ?: 0)
        }

        val completePhrasesCounts = com.example.data.backup.standardPhrasesCatalog.associate { std ->
          std.id to (phraseCounts[std.id] ?: 0)
        }

        val currentWallpaperId = activeWallpaperManager.selectedWallpaper.value.id
        val currentDimAlpha = activeWallpaperManager.dimAlpha.value

        val rootJson = JSONObject().apply {
          put("appName", "VocabFlow")
          put("version", 2)
          put("exportedAt", System.currentTimeMillis())
          put("totalWords", allWords.size)
          put("usedWordsCount", usedWordsCount)
          put("totalVocabularyUses", vocabCounts.values.sum())
          put("totalAlphabetUses", alphabetCounts.values.sum())
          put("totalNumberUses", completeNumbersCounts.values.sum())
          put("totalPhraseUses", completePhrasesCounts.values.sum())

          // 0. Fondo de pantalla activo y tonalidad / atenuación
          put("wallpaperId", currentWallpaperId)
          put("wallpaperDimAlpha", currentDimAlpha.toDouble())
          val activeWpObj = JSONObject().apply {
            put("id", currentWallpaperId)
            put("title", activeWallpaperManager.selectedWallpaper.value.title)
            val fileName = activeWallpaperManager.selectedWallpaper.value.fileName
            if (fileName != null) put("fileName", fileName)
            val assetPath = activeWallpaperManager.selectedWallpaper.value.assetPath
            if (assetPath != null) put("assetPath", assetPath)
            put("dimAlpha", currentDimAlpha.toDouble())
            put("dimPercentage", (currentDimAlpha * 100).toInt())
          }
          put("activeWallpaper", activeWpObj)

          // 1. Array completo de todas las palabras (2809 base + personalizadas) con sus contadores reales
          val allWordsArray = JSONArray()
          allWords.forEach { w ->
            allWordsArray.put(JSONObject().apply {
              put("rank", w.rank)
              put("word", w.lemma)
              put("useCount", w.useCount)
            })
          }
          put("allVocabularyWords", allWordsArray)

          // 2. Diccionario palabra -> conteo de uso
          val vocabObj = JSONObject()
          vocabCounts.forEach { (lemma, count) -> vocabObj.put(lemma, count) }
          put("vocabularyWordCounts", vocabObj)

          // 3. Palabras personalizadas añadidas después de la 2809 (2810, 2811...) con definición completa
          val customWordsArray = JSONArray()
          customWords.forEach { cw ->
            customWordsArray.put(JSONObject().apply {
              put("rank", cw.rank)
              put("word", cw.word)
              put("partOfSpeech", cw.partOfSpeech)
              put("spanishTranslation", cw.spanishTranslation)
              put("microExample", cw.microExample)
              put("useCount", cw.useCount)
            })
          }
          put("customWords", customWordsArray)

          // 4. Detalle completo de las 26 letras del abecedario (A a la Z) con sus contadores reales
          val alphabetLettersArray = JSONArray()
          com.example.data.backup.standardAlphabetCatalog.forEach { std ->
            val count = completeAlphabetCounts[std.letter] ?: 0
            alphabetLettersArray.put(JSONObject().apply {
              put("index", std.index)
              put("letter", std.letter)
              put("ipa", std.ipa)
              put("phoneticSp", std.phoneticSp)
              put("isVowel", std.isVowel)
              if (std.note.isNotBlank()) put("note", std.note)
              put("useCount", count)
            })
          }
          put("allAlphabetLetters", alphabetLettersArray)

          put("totalAlphabetLetters", completeAlphabetCounts.size)
          put("usedAlphabetLettersCount", completeAlphabetCounts.values.count { it > 0 })
          put("totalAlphabetUses", completeAlphabetCounts.values.sum())

          val alphabetObj = JSONObject()
          completeAlphabetCounts.forEach { (k, v) -> alphabetObj.put(k, v) }
          put("alphabetCounts", alphabetObj)

          // 5. Detalle completo de los 100 números (1 al 100) con sus contadores reales
          val numbersArray = JSONArray()
          com.example.data.backup.standardNumbersCatalog.forEach { std ->
            val count = completeNumbersCounts[std.number] ?: 0
            numbersArray.put(JSONObject().apply {
              put("number", std.number)
              put("word", std.word)
              put("ipa", std.ipa)
              put("phoneticSp", std.phoneticSp)
              put("isDecade", std.isDecade)
              put("useCount", count)
            })
          }
          put("allNumbers", numbersArray)

          put("totalNumbers", completeNumbersCounts.size)
          put("usedNumbersCount", completeNumbersCounts.values.count { it > 0 })
          put("totalNumberUses", completeNumbersCounts.values.sum())

          val numbersObj = JSONObject()
          completeNumbersCounts.forEach { (k, v) -> numbersObj.put(k.toString(), v) }
          put("numberCounts", numbersObj)

          // 6. Detalle completo de las 50 frases cotidianas (1 al 50) con sus contadores reales
          val phrasesArray = JSONArray()
          com.example.data.backup.standardPhrasesCatalog.forEach { std ->
            val count = completePhrasesCounts[std.id] ?: 0
            phrasesArray.put(JSONObject().apply {
              put("id", std.id)
              put("spanishText", std.spanishText)
              put("category", std.category)
              put("englishHint", std.englishHint)
              put("useCount", count)
            })
          }
          put("allPhrases", phrasesArray)

          put("totalPhrases", completePhrasesCounts.size)
          put("usedPhrasesCount", completePhrasesCounts.values.count { it > 0 })
          put("totalPhraseUses", completePhrasesCounts.values.sum())

          val phrasesObj = JSONObject()
          completePhrasesCounts.forEach { (k, v) -> phrasesObj.put(k.toString(), v) }
          put("phraseCounts", phrasesObj)

          // 7. Notas locales con su contenido íntegro (texto, estado, id, fecha)
          val notesArray = JSONArray()
          currentNotes.forEach { n ->
            notesArray.put(JSONObject().apply {
              put("id", n.id)
              put("text", n.text)
              put("isCompleted", n.isCompleted)
              put("createdAt", n.createdAt)
            })
          }
          put("localNotes", notesArray)

          // 8. Frases armadas guardadas desde el lienzo de pruebas
          val savedPhrasesStorage = SavedPhrasesStorage(context)
          val currentSavedPhrases = savedPhrasesStorage.loadSavedPhrases()
          val savedPhrasesArray = JSONArray()
          currentSavedPhrases.forEach { sp ->
            savedPhrasesArray.put(JSONObject().apply {
              put("id", sp.id)
              put("phraseText", sp.phraseText)
              val wArr = JSONArray()
              sp.words.forEach { wArr.put(it) }
              put("words", wArr)
              put("spanishTranslation", sp.spanishTranslation)
              put("createdAt", sp.createdAt)
            })
          }
          put("savedDraftPhrases", savedPhrasesArray)
        }

        val currentSavedPhrases = SavedPhrasesStorage(context).loadSavedPhrases()
        com.example.data.local.VocabularyStorage(context).saveCounts(vocabCounts)
        com.example.data.backup.AutoBackupManager.saveFullBackup(
          context = context,
          vocabularyWordCounts = vocabCounts,
          allWordsDetails = allWords,
          customWords = customWords,
          usedWordsCount = usedWordsCount,
          notes = currentNotes,
          alphabetCounts = alphabetCounts,
          numberCounts = numberCounts,
          phraseCounts = phraseCounts,
          savedDraftPhrases = currentSavedPhrases,
          wallpaperId = currentWallpaperId,
          wallpaperDimAlpha = currentDimAlpha
        )
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
          outputStream.write(rootJson.toString(2).toByteArray(Charsets.UTF_8))
        }
        val wpTitle = activeWallpaperManager.selectedWallpaper.value.title
        val wpDimPercent = (currentDimAlpha * 100).toInt()
        backupStatusMessage = "¡Copia exportada con éxito! (Fondo: $wpTitle $wpDimPercent%, $usedWordsCount copas, ${vocabCounts.values.sum()} usos de palabras, ${allWords.size} palabras totales, ${completeAlphabetCounts.size} letras, ${completeNumbersCounts.size} números, ${completePhrasesCounts.size} frases, ${currentSavedPhrases.size} frases guardadas, ${currentNotes.size} notas)"
      } catch (e: Exception) {
        backupStatusMessage = "Error al exportar: ${e.message}"
      }
    }
  }

  // SAF Launcher para importar y restaurar copia .json
  val importBackupLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri ->
    if (uri != null) {
      try {
        val fileContent = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
        if (fileContent.isNotBlank()) {
          val parsed = AutoBackupManager.parseJsonContent(fileContent)
          if (parsed != null) {
            viewModel.restoreFullBackup(parsed, context) { _, msg ->
              backupStatusMessage = msg
              if (parsed.wallpaperId != null) {
                activeWallpaperManager.applyWallpaperSettings(
                  wallpaperId = parsed.wallpaperId,
                  dimAlpha = parsed.wallpaperDimAlpha
                )
              } else {
                activeWallpaperManager.refresh()
              }
            }
          } else {
            backupStatusMessage = "Error: formato de archivo .json incompatible."
          }
        }
      } catch (e: Exception) {
        backupStatusMessage = "Error al restaurar archivo: formato incompatible."
      }
    }
  }

  androidx.compose.runtime.LaunchedEffect(searchQuery) {
    if (searchQuery.isNotBlank()) {
      listState.scrollToItem(0)
    }
  }

  fun performJumpToRank(input: String) {
    val targetRank = input.trim().toIntOrNull() ?: return
    if (words.isEmpty()) return

    // Si había búsqueda de texto o filtro de categoría activo, limpiarlo para tener la lista de palabras completa visible
    if (searchQuery.isNotEmpty()) {
      viewModel.updateSearchQuery("")
    }
    if (selectedSyntaxFilter != null) {
      viewModel.clearSyntaxFilter()
    }

    val targetIndex = words.indexOfFirst { it.rank == targetRank }
      .takeIf { it >= 0 }
      ?: words.indexOfFirst { it.rank >= targetRank }
        .takeIf { it >= 0 }
      ?: (words.size - 1)

    coroutineScope.launch {
      val scrollItem = targetIndex.coerceIn(0, maxOf(0, words.size - 1))
      listState.scrollToItem(scrollItem)
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("screen_vocabulario"),
    containerColor = if (!currentWallpaper.isDefaultNone) Color.Transparent else MaterialTheme.colorScheme.background
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = innerPadding.calculateTopPadding())
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        // BANDEJA FIJA: Brújula sintáctica, traductor, lienzo de pruebas y buscadores (siempre visibles)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              if (!currentWallpaper.isDefaultNone)
                Color.Transparent
              else
                MaterialTheme.colorScheme.background
            )
            .padding(top = 8.dp, bottom = 2.dp),
          verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          // Espacio superior encima de todo: "English" centrado con selector de velocidad a la izquierda y trofeo de logros a la derecha
          AnimatedEnglishHeader(
            usedWordsCount = usedWordsCount,
            celebrationTrigger = celebrationTrigger,
            currentSpeed = currentSpeechSpeed,
            onToggleSpeed = { viewModel.onToggleSpeed() },
            onOpenDrawer = { },
            onOpenProficiencyLevels = {
              isProficiencyLevelsTrayOpen = true
            }
          )

          // BARRA HORIZONTAL FLOTANTE DE HERRAMIENTAS: 7 ICONOS CON SUS FUNCIONES INTACTAS, SIN TEXTO, FLOTANDO ENTRE EL TÍTULO Y EL LIENZO DE PRUEBAS
          FloatingToolsHorizontalBar(
            onOpenRoadmap8Months = { isRoadmap8MonthsTrayOpen = true },
            onOpenSchedule = { isDailyScheduleTrayOpen = true },
            onOpenSettings = { isSettingsTrayOpen = true },
            onOpenWallpaper = { isWallpaperTrayOpen = true },
            onOpenNotes = { isLocalNotesTrayOpen = true },
            onToggleTranslator = { isSpanishTranslatorCardVisible = !isSpanishTranslatorCardVisible },
            onOpenSavedPhrases = { isSavedPhrasesTrayOpen = true }
          )

          // BANDEJA ESCRITOR TRADUCTOR ESPAÑOL ⇄ INGLÉS - OCULTO POR DEFECTO, VISIBLE AL TOCAR EL BOTÓN TRASLATE DE ARRIBA
          AnimatedVisibility(
            visible = isSpanishTranslatorCardVisible,
            enter = expandVertically(animationSpec = tween(280)) + fadeIn(animationSpec = tween(220)),
            exit = shrinkVertically(animationSpec = tween(280)) + fadeOut(animationSpec = tween(220))
          ) {
            SpanishToEnglishWriterCard(
              spanishText = spanishInputText,
              onSpanishTextChanged = { viewModel.updateSpanishInputText(it) },
              onTranslateNow = { viewModel.translateSpanishNow() },
              onClear = { viewModel.clearSpanishTranslator() },
              translationState = spanishTranslationState,
              onSpeakEnglish = { viewModel.speakWord(it) },
              onAddToCanvas = { viewModel.addSpanishTranslationToDraft(it) },
              isSpanishToEnglish = isSpanishToEnglish,
              onSwapLanguages = { viewModel.swapTranslationDirection() },
              onClose = { isSpanishTranslatorCardVisible = false },
              onWordClick = { word ->
                if (jumpRankInput.isNotEmpty()) {
                  jumpRankInput = ""
                }
                viewModel.clearSyntaxFilter()
                viewModel.updateSearchQuery(word)
                viewModel.recordWordUsage(word)
                coroutineScope.launch {
                  listState.scrollToItem(0)
                }
              }
            )
          }

          // LA BARRA DE BORRADOR ("Scratchpad" de Frases) - LIENZO DE PRUEBAS
          // Se muestra y abre cuando hay palabras añadidas al lienzo o cuando se puede retroceder
          AnimatedVisibility(
            visible = draftWords.isNotEmpty() || canUndoDraft,
            enter = expandVertically(animationSpec = tween(280, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(220)),
            exit = shrinkVertically(animationSpec = tween(280, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(220))
          ) {
            DraftScratchpadHeader(
              draftWords = draftWords,
              canUndoDraft = canUndoDraft,
              canRedoDraft = canRedoDraft,
              onUndo = { viewModel.undoDraft() },
              onRedo = { viewModel.redoDraft() },
              isPhraseUpdated = isPhraseUpdated,
              isTranslationTrayExpanded = isTranslationTrayExpanded,
              translationState = translationState,
              onToggleTranslationTray = { viewModel.toggleTranslationTray() },
              onTranslate = { viewModel.toggleTranslationTray() },
              onRemoveWord = { index -> viewModel.removeWordFromDraft(index) },
              onMoveLeft = { index -> viewModel.moveWordLeft(index) },
              onMoveRight = { index -> viewModel.moveWordRight(index) },
              onPlayPhrase = { viewModel.playFullDraftPhrase() },
              onClearDraft = { viewModel.clearDraft() },
              onSavePhrase = {
                if (draftWords.isNotEmpty()) {
                  val savedStorage = SavedPhrasesStorage(context)
                  val phraseText = draftWords.joinToString(" ") { it.lemma }
                  val wordsList = draftWords.map { it.lemma }
                  val translation = (translationState as? TranslationState.Success)?.translation
                    ?.takeIf { it.isNotBlank() }
                    ?: com.example.domain.util.PhraseTranslator.translatePhrase(phraseText)
                  val newSavedPhrase = SavedDraftPhrase(
                    id = java.util.UUID.randomUUID().toString(),
                    phraseText = phraseText,
                    words = wordsList,
                    spanishTranslation = translation,
                    createdAt = System.currentTimeMillis()
                  )
                  savedStorage.addSavedPhrase(newSavedPhrase)
                  Toast.makeText(context, "Frase guardada: \"$phraseText\"", Toast.LENGTH_SHORT).show()

                  // Actualiza la copia de seguridad local automática con las frases guardadas
                  val allWords = viewModel.words.value
                  val vocabCounts = allWords.associate { it.lemma to it.useCount }
                  val customWords = allWords.filter { it.rank > 2811 }.map { w ->
                    CustomWordRecord(
                      rank = w.rank,
                      word = w.lemma,
                      partOfSpeech = w.rawPartOfSpeech.ifBlank { w.partOfSpeech.name },
                      spanishTranslation = w.spanishTranslation,
                      microExample = w.microExample,
                      useCount = w.useCount
                    )
                  }
                  val currentNotes = LocalNotesStorage(context).loadNotes()
                  val allSavedPhrases = savedStorage.loadSavedPhrases()
                  AutoBackupManager.saveFullBackup(
                    context = context,
                    vocabularyWordCounts = vocabCounts,
                    allWordsDetails = allWords,
                    customWords = customWords,
                    usedWordsCount = usedWordsCount,
                    notes = currentNotes,
                    alphabetCounts = alphabetCounts,
                    numberCounts = numberCounts,
                    phraseCounts = phraseCounts,
                    savedDraftPhrases = allSavedPhrases,
                    wallpaperId = activeWallpaperManager.selectedWallpaper.value.id,
                    wallpaperDimAlpha = activeWallpaperManager.dimAlpha.value
                  )
                }
              },
              onSpeakWord = { word -> viewModel.speakWord(word) }
            )
          }

          // SELECTOR EN MEDIO DEL LIENZO DE PRUEBAS Y EL BUSCADOR DE VOCABULARIO:
          // Botones de solo texto con su rayita baja que lo señala (Vocabulario, Abecedario en el medio, Frases)
          // Y engranaje flotante minimalista pegado a la pared derecha
          // SELECTOR EN MEDIO DEL LIENZO DE PRUEBAS Y EL BUSCADOR DE VOCABULARIO:
          // 1. Icono de nube flotante reactivo SIN CAJA a la izquierda (CenterStart)
          // 2. Pestañas de solo texto con rayita baja (Vocabulario, Abecedario, Números, Frases)
          BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
          ) {
            val tabsScrollState = rememberScrollState()
            val isOverflowing = (tabsScrollState.maxValue > 0 && tabsScrollState.maxValue != Int.MAX_VALUE) || maxWidth < 380.dp

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tabsScrollState)
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("tab_selector_vocabulario_abecedario_frases"),
              horizontalArrangement = if (isOverflowing) Arrangement.Start else Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
            // 1. Botón de solo texto "Vocabulario" con su rayita baja
            Column(
              modifier = Modifier
                .clickable(
                  interactionSource = remember { MutableInteractionSource() },
                  indication = null
                ) { selectedSectionTab = MainSectionTab.VOCABULARY }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("tab_vocabulario_btn"),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Vocabulario",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = if (selectedSectionTab == MainSectionTab.VOCABULARY) FontWeight.Bold else FontWeight.SemiBold,
                  fontSize = 16.sp,
                  color = if (selectedSectionTab == MainSectionTab.VOCABULARY) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                )
              )
              Spacer(modifier = Modifier.height(3.dp))
              Box(
                modifier = Modifier
                  .width(48.dp)
                  .height(3.5.dp)
                  .background(
                    color = if (selectedSectionTab == MainSectionTab.VOCABULARY) Color(0xFF38BDF8) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                  )
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Botón de solo texto "Abecedario" con su rayita baja
            Column(
              modifier = Modifier
                .clickable(
                  interactionSource = remember { MutableInteractionSource() },
                  indication = null
                ) { selectedSectionTab = MainSectionTab.ALPHABET }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("tab_abecedario_btn"),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Abecedario",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = if (selectedSectionTab == MainSectionTab.ALPHABET) FontWeight.Bold else FontWeight.SemiBold,
                  fontSize = 16.sp,
                  color = if (selectedSectionTab == MainSectionTab.ALPHABET) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                )
              )
              Spacer(modifier = Modifier.height(3.dp))
              Box(
                modifier = Modifier
                  .width(46.dp)
                  .height(3.5.dp)
                  .background(
                    color = if (selectedSectionTab == MainSectionTab.ALPHABET) Color(0xFF38BDF8) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                  )
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 3. Botón de solo texto "Números" con su rayita baja (1 al 100)
            Column(
              modifier = Modifier
                .clickable(
                  interactionSource = remember { MutableInteractionSource() },
                  indication = null
                ) { selectedSectionTab = MainSectionTab.NUMBERS }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("tab_numeros_btn"),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Números",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = if (selectedSectionTab == MainSectionTab.NUMBERS) FontWeight.Bold else FontWeight.SemiBold,
                  fontSize = 16.sp,
                  color = if (selectedSectionTab == MainSectionTab.NUMBERS) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                )
              )
              Spacer(modifier = Modifier.height(3.dp))
              Box(
                modifier = Modifier
                  .width(42.dp)
                  .height(3.5.dp)
                  .background(
                    color = if (selectedSectionTab == MainSectionTab.NUMBERS) Color(0xFF38BDF8) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                  )
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 4. Botón de solo texto "Frases" con su rayita baja
            Column(
              modifier = Modifier
                .clickable(
                  interactionSource = remember { MutableInteractionSource() },
                  indication = null
                ) { selectedSectionTab = MainSectionTab.PHRASES }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("tab_frases_btn"),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Frases",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = if (selectedSectionTab == MainSectionTab.PHRASES) FontWeight.Bold else FontWeight.SemiBold,
                  fontSize = 16.sp,
                  color = if (selectedSectionTab == MainSectionTab.PHRASES) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                )
              )
              Spacer(modifier = Modifier.height(3.dp))
              Box(
                modifier = Modifier
                  .width(36.dp)
                  .height(3.5.dp)
                  .background(
                    color = if (selectedSectionTab == MainSectionTab.PHRASES) Color(0xFF38BDF8) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                  )
              )
            }
          }
        }

          if (selectedSectionTab == MainSectionTab.VOCABULARY) {
            // FILA DE BUSCADORES DE PALABRAS Y SALTO NUMÉRICO (# 1-2809) - Mismo alto que el traductor (34dp)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 1.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Buscador de texto tradicional (Filtro) - Altura aumentada a 42dp con lupa adecuada y palabra fantasma 'Buscar'
              BasicTextField(
                value = searchQuery,
                onValueChange = {
                  if (it.isNotEmpty() && jumpRankInput.isNotEmpty()) {
                    jumpRankInput = ""
                  }
                  viewModel.updateSearchQuery(it)
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                  color = Color.White,
                  fontSize = 13.sp
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                  .weight(1f)
                  .testTag("search_word_input"),
                decorationBox = { innerTextField ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(42.dp)
                      .background(Color(0xFF1E293B).copy(alpha = 0.5f), shape = RoundedCornerShape(10.dp))
                      .border(1.dp, Color(0xFF334155).copy(alpha = 0.7f), shape = RoundedCornerShape(10.dp))
                      .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Search,
                      contentDescription = "Buscar",
                      tint = Color(0xFF94A3B8),
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                      modifier = Modifier.weight(1f),
                      contentAlignment = Alignment.CenterStart
                    ) {
                      if (searchQuery.isEmpty()) {
                        Text(
                          text = "Buscar",
                          style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                          )
                        )
                      }
                      innerTextField()
                    }
                    if (searchQuery.isNotEmpty()) {
                      IconButton(
                        onClick = { viewModel.updateSearchQuery("") },
                        modifier = Modifier.size(24.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Close,
                          contentDescription = "Limpiar búsqueda",
                          tint = Color(0xFF94A3B8),
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }
                }
              )

              // Mini-Buscador de Salto Numérico (#) (Teletransporte sin filtrar) - Altura aumentada a 42dp (ancho intacto)
              BasicTextField(
                value = jumpRankInput,
                onValueChange = { raw ->
                  val digits = raw.filter { it.isDigit() }.take(4)
                  jumpRankInput = digits
                  if (digits.isNotEmpty()) {
                    performJumpToRank(digits)
                  }
                },
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Number,
                  imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                  onGo = { performJumpToRank(jumpRankInput) }
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                  color = Color(0xFF38BDF8),
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(Color(0xFF38BDF8)),
                modifier = Modifier
                  .width(86.dp)
                  .testTag("jump_rank_input"),
                decorationBox = { innerTextField ->
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(42.dp)
                      .background(Color(0xFF1E293B).copy(alpha = 0.5f), shape = RoundedCornerShape(10.dp))
                      .border(1.dp, Color(0xFF334155).copy(alpha = 0.7f), shape = RoundedCornerShape(10.dp))
                      .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    if (jumpRankInput.isEmpty()) {
                      Text(
                        text = "1-${words.maxOfOrNull { it.rank } ?: 2811}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          fontSize = 11.5.sp
                        ),
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                      )
                    }
                    innerTextField()
                  }
                }
              )
            }

            // BRÚJULA SINTÁCTICA ULTRA-FINA (Conector > Sujeto > Verbo > Sustantivo > Modificador)
            // Ubicado justo abajo del buscador de palabras y selector del número de palabras (# 1-2809)
            SyntaxCompassHeader(
              selectedFilter = selectedSyntaxFilter,
              onSelectFilter = { filter -> viewModel.selectSyntaxFilter(filter) },
              onExplainFilter = { filter -> explainedFilter = filter },
              onClearFilter = { viewModel.clearSyntaxFilter() }
            )

            // SEGUNDA BANDEJA HORIZONTAL ("El tipo del tipo") - Subcategorías
            AnimatedVisibility(
              visible = selectedSyntaxFilter != null,
              enter = fadeIn() + expandVertically(),
              exit = fadeOut() + shrinkVertically()
            ) {
              selectedSyntaxFilter?.let { filter ->
                SubCategoryHorizontalTray(
                  filter = filter,
                  selectedSubCategory = selectedSubCategory,
                  onSelectSubCategory = { subCat -> viewModel.selectSubCategory(subCat) }
                )
              }
            }
          }

        }

        if (selectedSectionTab == MainSectionTab.VOCABULARY) {
          // 3. LISTA DE LAS 2,809 PALABRAS
          LazyColumn(
            state = listState,
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .testTag("vocabulary_list"),
            contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 2.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            if (filteredWords.isEmpty()) {
              item(key = "empty_state") {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                  contentAlignment = Alignment.Center
                ) {
                  val emptyText = when {
                    searchQuery.isNotBlank() -> "No se encontraron coincidencias para \"$searchQuery\""
                    selectedSyntaxFilter != null && (selectedSubCategory == SyntaxFilter.SUBCAT_MOST_USED || selectedSubCategory.isBlank() || selectedSubCategory == "Todos") ->
                      "Aún no tienes palabras con usos en ${selectedSyntaxFilter?.displayName ?: "esta categoría"}.\n¡Toca o practica palabras para verlas aquí ordenadas de mayor a menor uso!"
                    else -> "No hay palabras en esta subcategoría."
                  }
                  Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            } else {
              itemsIndexed(
                items = filteredWords,
                key = { _, item -> item.id },
                contentType = { _, _ -> "word_card" }
              ) { _, word ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                  WordHudCard(
                    word = word,
                    onTap = { viewModel.addWordToDraft(word) },
                    onSpeak = { viewModel.speakWord(word) },
                    onSpeakExample = { example -> viewModel.speakExample(word, example) },
                    onOpenDetails = { viewModel.recordWordUsage(word) },
                    onEdit = if (word.rank >= 2812) { { viewModel.openEditWordDialog(word) } } else null,
                    onDelete = if (word.rank >= 2812) { { viewModel.requestDeleteCustomWord(word) } } else null
                  )
                }
              }
            }
          }
        } else if (selectedSectionTab == MainSectionTab.ALPHABET) {
          // SECCIÓN DEL ABECEDARIO EN INGLÉS (26 LETRAS, FONÉTICA, CONTADOR Y BOTÓN LIENZO)
          AlphabetListSection(
            letterCounts = alphabetCounts,
            onSpeakLetter = { letterItem -> viewModel.speakAlphabetLetter(letterItem) },
            onAddLetterToCanvas = { letterItem -> viewModel.addAlphabetLetterToDraft(letterItem) },
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          )
        } else if (selectedSectionTab == MainSectionTab.NUMBERS) {
          // SECCIÓN DE NÚMEROS DEL 1 AL 100 EN INGLÉS (ESCRITURA, FONÉTICA, CONTADOR Y BOTÓN LIENZO)
          NumbersSection(
            numberCounts = numberCounts,
            onSpeakNumber = { entry -> viewModel.speakNumber(entry) },
            onAddNumberToCanvas = { entry -> viewModel.addNumberToDraft(entry) },
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          )
        } else {
          // BANDEJA DE FRASES COTIDIANAS Y DE HOGAR (50 FRASES)
          // Diseño libre sin cajas con reproductor de audio, contador y sincronización local
          EverydayPhrasesSection(
            phraseCounts = phraseCounts,
            onSpeakPhrase = { phrase -> viewModel.speakPhrase(phrase) },
            onAddPhraseToCanvas = { phrase -> viewModel.addPhraseToDraft(phrase) },
            onSendToTranslator = { phrase ->
              viewModel.sendPhraseToTranslator(phrase)
              Toast.makeText(
                context,
                "Frase enviada al traductor",
                Toast.LENGTH_SHORT
              ).show()
            },
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          )
        }
    }

    // BANDEJA FLOTANTE DE YOUTUBE: Vive activada desde el botón flotante inferior
    // Permanece en el árbol de composición para no perder NUNCA el estado web ni el audio (anti-reseteo)
      FloatingYouTubeTray(
        isOpen = isYouTubeTrayOpen,
        onClose = { isYouTubeTrayOpen = false }
      )
      FloatingGeminiWebTray(
        isOpen = isGeminiWebTrayOpen,
        onClose = { isGeminiWebTrayOpen = false }
      )
      FloatingGoogleTranslateWebTray(
        isOpen = isGoogleTranslateWebTrayOpen,
        onClose = { isGoogleTranslateWebTrayOpen = false }
      )
      FloatingRoadmap8MonthsTray(
        isOpen = isRoadmap8MonthsTrayOpen,
        onClose = { isRoadmap8MonthsTrayOpen = false }
      )
      FloatingDailyScheduleTray(
        isOpen = isDailyScheduleTrayOpen,
        onClose = { isDailyScheduleTrayOpen = false }
      )
      FloatingLocalNotesTray(
        isOpen = isLocalNotesTrayOpen,
        onClose = { isLocalNotesTrayOpen = false }
      )
      val currentNotesCount = remember(isSettingsTrayOpen, isLocalNotesTrayOpen) {
        if (isSettingsTrayOpen) LocalNotesStorage(context).loadNotes().size else 0
      }
      val vocabularyTotalUses = remember(words) {
        words.sumOf { it.useCount }
      }
      val alphabetTotalUses = remember(alphabetCounts) {
        alphabetCounts.values.sum()
      }
      val alphabetActiveCount = remember(alphabetCounts) {
        alphabetCounts.count { it.value > 0 }
      }
      val numbersTotalUses = remember(numberCounts) {
        numberCounts.values.sum()
      }
      val numbersActiveCount = remember(numberCounts) {
        numberCounts.count { it.value > 0 }
      }
      val phrasesTotalUses = remember(phraseCounts) {
        phraseCounts.values.sum()
      }
      val phrasesActiveCount = remember(phraseCounts) {
        phraseCounts.count { it.value > 0 }
      }

      FloatingSettingsBackupTray(
        isOpen = isSettingsTrayOpen,
        onClose = { isSettingsTrayOpen = false },
        vocabularyActiveWords = usedWordsCount,
        vocabularyTotalUses = vocabularyTotalUses,
        alphabetActiveCount = alphabetActiveCount,
        alphabetTotalUses = alphabetTotalUses,
        numbersActiveCount = numbersActiveCount,
        numbersTotalUses = numbersTotalUses,
        phrasesActiveCount = phrasesActiveCount,
        phrasesTotalUses = phrasesTotalUses,
        localNotesCount = currentNotesCount,
        statusMessage = backupStatusMessage,
        onClearStatusMessage = { backupStatusMessage = null },
        onExportBackup = {
          exportBackupLauncher.launch("vocabflow_progreso_backup.json")
        },
        onImportBackup = {
          importBackupLauncher.launch(arrayOf("application/json", "*/*"))
        },
        hasDirectBackup = AutoBackupManager.hasSurvivingBackup(context),
        onDirectRestore = {
          val data = AutoBackupManager.loadLatestBackup(context)
          if (data != null) {
            viewModel.restoreFullBackup(data, context) { _, msg ->
              backupStatusMessage = msg
              if (data.wallpaperId != null) {
                activeWallpaperManager.applyWallpaperSettings(
                  wallpaperId = data.wallpaperId,
                  dimAlpha = data.wallpaperDimAlpha
                )
              } else {
                activeWallpaperManager.refresh()
              }
            }
          } else {
            backupStatusMessage = "No se pudo leer la copia de Documentos/VocabFlow"
          }
        },
        onOpenProficiencyLevels = {
          isSettingsTrayOpen = false
          isProficiencyLevelsTrayOpen = true
        }
      )

      ProficiencyLevelsTray(
        isOpen = isProficiencyLevelsTrayOpen,
        onClose = { isProficiencyLevelsTrayOpen = false },
        masteredWordsCount = usedWordsCount
      )

      FloatingWallpaperTray(
        isOpen = isWallpaperTrayOpen,
        onClose = { isWallpaperTrayOpen = false },
        wallpaperManager = activeWallpaperManager
      )

      FloatingSavedPhrasesTray(
        isOpen = isSavedPhrasesTrayOpen,
        onClose = { isSavedPhrasesTrayOpen = false },
        onSpeakPhrase = { text -> viewModel.speakWord(text) },
        onAddToCanvas = { phrase ->
          viewModel.addSavedPhraseToDraft(phrase)
          isSavedPhrasesTrayOpen = false
        }
      )

      // BANDEJA EN PASTILLA FLOTANTE (Solo la pastilla flotante y sus 3 botones: Gemini, YouTube, Traductor)
      // Flotando limpiamente sobre el contenido sin ninguna tabla ni barra de fondo
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .navigationBarsPadding()
          .padding(bottom = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Surface(
          modifier = Modifier
            .width(175.dp)
            .testTag("giant_bottom_pill"),
          shape = CircleShape,
          color = Color(0xFF101923),
          border = BorderStroke(1.2.dp, Color(0xFF334155)),
          shadowElevation = 8.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // 1. Gemini Web
            GeminiWebFloatingActionButton(
              isOpen = isGeminiWebTrayOpen,
              onClick = {
                isGeminiWebTrayOpen = !isGeminiWebTrayOpen
                if (isGeminiWebTrayOpen) {
                  isYouTubeTrayOpen = false
                  isGoogleTranslateWebTrayOpen = false
                }
              }
            )
            // 2. YouTube Web
            YouTubeFloatingActionButton(
              isOpen = isYouTubeTrayOpen,
              onClick = {
                isYouTubeTrayOpen = !isYouTubeTrayOpen
                if (isYouTubeTrayOpen) {
                  isGeminiWebTrayOpen = false
                  isGoogleTranslateWebTrayOpen = false
                }
              }
            )
            // 3. Google Translate Web
            GoogleTranslateFloatingActionButton(
              isOpen = isGoogleTranslateWebTrayOpen,
              onClick = {
                isGoogleTranslateWebTrayOpen = !isGoogleTranslateWebTrayOpen
                if (isGoogleTranslateWebTrayOpen) {
                  isYouTubeTrayOpen = false
                  isGeminiWebTrayOpen = false
                }
              }
            )
          }
        }
      }
    }
  }


  if (showAddWordDialog && editingCustomWord != null) {
    AddCustomWordDialog(
      nextRank = nextRank,
      editingWord = editingCustomWord,
      onDismiss = { viewModel.closeAddWordDialog() },
      onSaveWord = { word, category, spanish, example ->
        viewModel.updateExistingCustomWord(
          rank = editingCustomWord!!.rank,
          word = word,
          category = category,
          spanishTranslation = spanish,
          microExample = example
        )
      },
      onPreviewSpeak = { text -> viewModel.speakWord(text) }
    )
  }

  if (wordToDelete != null) {
    DeleteWordConfirmationDialog(
      word = wordToDelete!!,
      onConfirm = { viewModel.confirmDeleteCustomWord() },
      onDismiss = { viewModel.cancelDeleteCustomWord() }
    )
  }

  if (explainedFilter != null) {
    SyntaxExplanationTray(
      filter = explainedFilter!!,
      onDismiss = { explainedFilter = null }
    )
  }
}

/**
 * Botón Flotante Inferior de YouTube.
 * Reemplaza la flecha anterior. Al tocarlo, abre o cierra la bandeja flotante de YouTube
 * sin reiniciar la reproducción ni perder la búsqueda web (anti-reseteo total).
 */
@Composable
fun YouTubeFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("fab_youtube_toggle"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFFFF0000)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = Color(0xFFFF0000),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = if (isOpen) "Ocultar YouTube" else "Abrir YouTube",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}



/**
 * URL directa a Géminis Web (Google AI Search / Gemini Mode) para resolver cualquier consulta en vivo.
 */
const val GEMINI_WEB_URL =
  "https://www.google.com/search?client=ms-android-zte&hs=8eqq&sca_esv=f805f121b9f8e54d&sxsrf=APpeQnsqh0udAtTBjMevx4L_BM-liBl99w%3A1792494801229&udm=50&fbs=ABfTbFVyMZGZf1hfvX9uKjN_-G8c4u0nXx4bEIpwm1lnNH832VTJOOCxW_fyN-Q_ezyf8gKKYi6DYGSj2Ng7gxGZN3oaoqNkvqcw0gjT8z58Q3rjcd-DOsrGjYtbRDd9MfcP_OXc30v1Zb9LanikIgCzV0VsqA2nqB9np11qGHG-trzG67oLJE90WZyf8oIateP_ijZNgZeza-znfZx1_bnEMEQw6UX1Dg&vsint=&ntc=1&cs=1&sa=X&ved=0CAEQoo4PahcKEwjQ7NXAu8iXAxUAAAAAHQAAAAAQGg&biw=423&bih=834&dpr=1.7&zs=1"

/**
 * Botón Inferior de Géminis Web.
 * Ubicado en la pastilla gigante baja para consultar en tiempo real.
 */
@Composable
fun GeminiWebFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("fab_gemini_web"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF60A5FA)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF2563EB) else Color(0xFF1A73E8),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = if (isOpen) "Cerrar Géminis Web" else "Abrir Géminis Web Flotante",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

/**
 * Botón Inferior de Google Traductor Web.
 * Ubicado en la pastilla gigante baja.
 */
@Composable
fun GoogleTranslateFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("fab_google_translate_web"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF4285F4)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF1967D2) else Color(0xFF1A73E8),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = if (isOpen) "Cerrar Traductor Web" else "Abrir Google Traductor Web",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

/**
 * Botón Flotante Inferior del Traductor Español a Inglés.
 * Abre o pliega la bandeja de traducción interactiva.
 */
@Composable
fun SpanishTranslatorFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("btn_toggle_spanish_translator_header"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF00E5FF)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF0891B2) else Color(0xFF1E293B),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = if (isOpen) "Ocultar Traductor Idioma" else "Abrir Traductor Idioma",
            tint = if (isOpen) Color.White else Color(0xFF00E5FF),
            modifier = Modifier
              .size(18.dp)
              .testTag("icon_spanish_translator_header")
          )
        }
      }
    }
  }
}

/**
 * Botón Flotante Inferior de Horario de Aprendizaje Diario (Reloj).
 */
@Composable
fun DailyScheduleFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("btn_open_schedule_tray"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF38BDF8)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF0284C7) else Color(0xFF1E293B),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            painter = painterResource(R.drawable.ic_radar_sonar),
            contentDescription = "Horario de aprendizaje",
            tint = if (isOpen) Color.White else Color(0xFF38BDF8),
            modifier = Modifier
              .size(18.dp)
              .testTag("icon_schedule_clock")
          )
        }
      }
    }
  }
}

/**
 * Botón Flotante Inferior de Meta de 8 Meses (Bandera).
 */
@Composable
fun Roadmap8MonthsFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("btn_open_roadmap_8months"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF38BDF8)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF0284C7) else Color(0xFF1E293B),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.Flag,
            contentDescription = "Meta de 8 meses",
            tint = if (isOpen) Color.White else Color(0xFF38BDF8),
            modifier = Modifier
              .size(18.dp)
              .testTag("icon_roadmap_flag")
          )
        }
      }
    }
  }
}

/**
 * Botón Flotante Inferior de Notas Locales (Hoja).
 */
@Composable
fun LocalNotesFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("btn_open_local_notes"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF38BDF8)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF0284C7) else Color(0xFF1E293B),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = "Notas locales",
            tint = if (isOpen) Color.White else Color(0xFF38BDF8),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

/**
 * Botón Flotante Inferior de Ajustes y Copia de Seguridad (Engranaje).
 */
@Composable
fun SettingsFloatingActionButton(
  isOpen: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("btn_settings_gear"),
    shape = CircleShape,
    color = if (isOpen) Color(0xFF1E293B) else Color.Transparent,
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF38BDF8)) else null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF0284C7) else Color(0xFF1E293B),
        modifier = Modifier.size(30.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Ajustes de la app",
            tint = if (isOpen) Color.White else Color(0xFF38BDF8),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

/**
 * Cabecera superior con la palabra "English" centrada, en tipografía Fredericka la Grande,
 * con animaciones y efectos de luz luminosos:
 * - A la izquierda: Botón de tres rayitas para abrir la bandeja lateral de Herramientas.
 * - Al centro: Palabra "English" con efecto de resplandor y barrido luminoso.
 * - A la derecha: Indicador de Logros ("28 🏆") con reacción premium (brillo, escala elástica, aura dorada y +1 flotante) + Selector de velocidad TTS.
 */
@Composable
fun AnimatedEnglishHeader(
  usedWordsCount: Int = 0,
  celebrationTrigger: Long = 0L,
  currentSpeed: Float = 0.3f,
  onToggleSpeed: () -> Unit = {},
  onOpenDrawer: () -> Unit = {},
  onOpenProficiencyLevels: () -> Unit = {},
  onOpenRoadmap8Months: () -> Unit = {},
  onOpenDailySchedule: () -> Unit = {},
  onOpenLocalNotes: () -> Unit = {},
  onOpenSettings: () -> Unit = {},
  isSpanishTranslatorVisible: Boolean = false,
  onToggleSpanishTranslator: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(top = 8.dp, bottom = 8.dp, start = 6.dp, end = 6.dp)
      .testTag("header_english_space"),
    contentAlignment = Alignment.Center
  ) {
    // Esquina superior izquierda: Selector / reductor de velocidad TTS
    SpeedFloatingActionButton(
      currentSpeed = currentSpeed,
      onClick = onToggleSpeed,
      modifier = Modifier
        .align(Alignment.CenterStart)
        .padding(start = 6.dp)
        .testTag("header_speed_control")
    )

    // Título "English" estático y nítido
    Text(
      text = "English",
      fontFamily = FrederickaTheGreatFontFamily,
      fontSize = 27.sp,
      fontWeight = FontWeight.Normal,
      letterSpacing = 2.sp,
      textAlign = TextAlign.Center,
      color = Color(0xFFE2E8F0),
      style = TextStyle(
        shadow = Shadow(
          color = Color(0xFF38BDF8).copy(alpha = 0.35f),
          offset = Offset(0f, 1f),
          blurRadius = 4f
        )
      ),
      modifier = Modifier.testTag("text_english_title")
    )

    // Esquina superior derecha: Indicador de Logros ("28 🏆") libre sin caja ni recuadro
    PremiumTrophyBadge(
      count = usedWordsCount,
      celebrationTrigger = celebrationTrigger,
      onClick = onOpenProficiencyLevels,
      onOpenDrawer = onOpenDrawer,
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 6.dp)
        .testTag("header_trophy_achievement_badge")
    )
  }
}

/**
 * Insignia de logros ("28 🏆") para la cabecera principal de aprendizaje.
 * Totalmente libre y sin caja ni recuadro amarillo tenue, conservando intacto todo el efecto
 * de animación reactivo festivo (rebote elástico, giro festivo de trofeo, destello áureo en celebración
 * y partícula flotante ascendente "+1 🏆").
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PremiumTrophyBadge(
  count: Int,
  celebrationTrigger: Long,
  onClick: () -> Unit,
  onOpenDrawer: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current

  // Estados de animación para el efecto reactivo premium
  val scaleAnim = remember { Animatable(1.0f) }
  val glowAnim = remember { Animatable(0f) }
  val rotationAnim = remember { Animatable(0f) }
  val floatingOffsetY = remember { Animatable(0f) }
  val floatingAlpha = remember { Animatable(0f) }
  var showFloatingPlusOne by remember { mutableStateOf(false) }

  // Disparo de la reacción visual festiva al detectar palabra nueva (0 -> 1)
  var lastHandledTrigger by remember { mutableLongStateOf(0L) }
  LaunchedEffect(celebrationTrigger) {
    if (celebrationTrigger > 0L && celebrationTrigger != lastHandledTrigger) {
      lastHandledTrigger = celebrationTrigger
      try {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      } catch (_: Throwable) {}

      // Disparar secuencia coreografiada de animaciones
      launch {
        showFloatingPlusOne = true
        floatingOffsetY.snapTo(0f)
        floatingAlpha.snapTo(1f)
        launch {
          floatingOffsetY.animateTo(-42f, tween(1100, easing = FastOutSlowInEasing))
        }
        launch {
          delay(450)
          floatingAlpha.animateTo(0f, tween(650, easing = LinearEasing))
        }
        delay(1150)
        showFloatingPlusOne = false
      }

      // 1. Efecto elástico de escala (Pop & Spring bounce)
      launch {
        scaleAnim.animateTo(1.36f, tween(180, easing = FastOutSlowInEasing))
        scaleAnim.animateTo(
          1.0f,
          spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
          )
        )
      }

      // 2. Resplandor festivo momentáneo al celebrar
      launch {
        glowAnim.animateTo(1.0f, tween(160, easing = FastOutSlowInEasing))
        glowAnim.animateTo(0f, tween(750, easing = LinearEasing))
      }

      // 3. Bamboleo festivo del trofeo (Playful tilt)
      launch {
        rotationAnim.animateTo(-16f, tween(90, easing = FastOutSlowInEasing))
        rotationAnim.animateTo(16f, tween(120, easing = FastOutSlowInEasing))
        rotationAnim.animateTo(-8f, tween(90, easing = FastOutSlowInEasing))
        rotationAnim.animateTo(0f, tween(100, easing = FastOutSlowInEasing))
      }
    }
  }

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    // Contenido libre sin caja ni recuadro: Número + Trofeo ("28 🏆")
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .combinedClickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onClick,
          onLongClick = onOpenDrawer
        )
        .padding(horizontal = 4.dp, vertical = 4.dp)
        .testTag("counter_used_words_badge")
        .graphicsLayer {
          scaleX = scaleAnim.value
          scaleY = scaleAnim.value
        }
    ) {
      Text(
        text = "$count",
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.ExtraBold,
          fontSize = 15.sp,
          color = if (glowAnim.value > 0.05f) Color(0xFFFDE047) else Color(0xFFF8FAFC),
          shadow = if (glowAnim.value > 0.05f) Shadow(
            color = Color(0xFFEAB308),
            blurRadius = 8f
          ) else null
        ),
        modifier = Modifier.testTag("text_used_words_count")
      )
      Text(
        text = "🏆",
        fontSize = 15.sp,
        modifier = Modifier
          .testTag("icon_trophy")
          .graphicsLayer {
            rotationZ = rotationAnim.value
          }
      )
    }

    // Partícula flotante ascendente "+1 🏆"
    if (showFloatingPlusOne) {
      Text(
        text = "+1 🏆",
        style = TextStyle(
          fontWeight = FontWeight.Black,
          fontSize = 13.sp,
          color = Color(0xFFFDE047),
          shadow = Shadow(
            color = Color(0xFFB45309),
            blurRadius = 8f
          )
        ),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .offset(y = floatingOffsetY.value.dp)
          .alpha(floatingAlpha.value)
      )
    }
  }
}

/**
 * Barra horizontal flotante de herramientas: 7 logos/iconos con sus funciones intactas
 * sin textos, flotando elegantemente entre el título ("English") y el lienzo de pruebas.
 */
@Composable
fun FloatingToolsHorizontalBar(
  onOpenRoadmap8Months: () -> Unit,
  onOpenSchedule: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenWallpaper: () -> Unit,
  onOpenNotes: () -> Unit,
  onToggleTranslator: () -> Unit,
  onOpenSavedPhrases: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 6.dp, vertical = 2.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier
        .horizontalScroll(scrollState)
        .padding(horizontal = 4.dp, vertical = 2.dp)
        .testTag("floating_horizontal_tools_bar"),
      horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Meta 8 Meses
      ToolIconButton(
        icon = Icons.Outlined.Flag,
        iconTint = Color(0xFF38BDF8),
        contentDescription = "Meta 8 Meses",
        testTag = "drawer_item_roadmap_8months",
        onClick = onOpenRoadmap8Months
      )

      // 2. Horario (Logo radar/sonar anaranjado)
      ToolIconButton(
        painter = painterResource(R.drawable.ic_radar_sonar),
        iconTint = Color(0xFFFBBF24),
        contentDescription = "Horario",
        testTag = "drawer_item_schedule",
        onClick = onOpenSchedule
      )

      // 4. Ajustes
      ToolIconButton(
        icon = Icons.Outlined.Settings,
        iconTint = Color(0xFF94A3B8),
        contentDescription = "Ajustes",
        testTag = "drawer_item_settings",
        onClick = onOpenSettings
      )

      // 5. Fondos
      ToolIconButton(
        icon = Icons.Default.Wallpaper,
        iconTint = Color(0xFFE879F9),
        contentDescription = "Fondos",
        testTag = "drawer_item_wallpaper",
        onClick = onOpenWallpaper
      )

      // 6. Notas
      ToolIconButton(
        icon = Icons.Outlined.Description,
        iconTint = Color(0xFFFB923C),
        contentDescription = "Notas",
        testTag = "drawer_item_notes",
        onClick = onOpenNotes
      )

      // 7. Traductor
      ToolIconButton(
        icon = Icons.Default.Translate,
        iconTint = Color(0xFF00E5FF),
        contentDescription = "Traductor",
        testTag = "drawer_item_spanish_translator",
        onClick = onToggleTranslator
      )

      // 8. Frases
      ToolIconButton(
        icon = Icons.Default.Bookmarks,
        iconTint = Color(0xFFFBBF24),
        contentDescription = "Frases",
        testTag = "drawer_item_saved_phrases",
        onClick = onOpenSavedPhrases
      )
    }
  }
}

@Composable
private fun ToolIconButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
  painter: androidx.compose.ui.graphics.painter.Painter? = null,
  iconTint: Color,
  contentDescription: String,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(36.dp)
      .background(
        color = Color(0xFF0D1527).copy(alpha = 0.85f),
        shape = RoundedCornerShape(10.dp)
      )
      .border(
        border = BorderStroke(1.dp, iconTint.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(10.dp)
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = androidx.compose.material3.ripple(bounded = true, radius = 20.dp),
        onClick = onClick
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    if (painter != null) {
      Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = iconTint,
        modifier = Modifier.size(19.dp)
      )
    } else if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = iconTint,
        modifier = Modifier.size(19.dp)
      )
    }
  }
}





/**
 * Sección de 50 frases armadas en español (cotidianas y del hogar).
 * Al tocar cualquier frase, se envía al traductor nativo de la app para modificarla,
 * escuchar su pronunciación en inglés o enviarla al lienzo de pruebas con el botón (+ Lienzo).
 */
@Composable
fun EverydayPhrasesSection(
  phraseCounts: Map<Int, Int> = emptyMap(),
  onSpeakPhrase: (EverydayPhrase) -> Unit,
  onAddPhraseToCanvas: (EverydayPhrase) -> Unit,
  onSendToTranslator: ((EverydayPhrase) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var phraseSearchQuery by rememberSaveable { mutableStateOf("") }
  var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

  val allPhrases = remember { EverydayPhrasesCatalog.phrases }
  val categories = remember(allPhrases) {
    listOf("Todas") + allPhrases.map { it.category }.distinct()
  }

  val filteredPhrases = remember(phraseSearchQuery, selectedCategory, allPhrases) {
    allPhrases.filter { phrase ->
      val matchesCategory = selectedCategory == null || selectedCategory == "Todas" || phrase.category == selectedCategory
      val matchesQuery = phraseSearchQuery.isBlank() ||
        phrase.spanishText.contains(phraseSearchQuery, ignoreCase = true) ||
        phrase.englishHint.contains(phraseSearchQuery, ignoreCase = true)
      matchesCategory && matchesQuery
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("everyday_phrases_section")
  ) {
    // 1. Buscador rápido de frases cotidianas agrandado
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      BasicTextField(
        value = phraseSearchQuery,
        onValueChange = { phraseSearchQuery = it },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
          color = Color.White,
          fontSize = 14.sp
        ),
        cursorBrush = SolidColor(Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_phrases_input"),
        decorationBox = { innerTextField ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .background(Color(0xFF1E293B).copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
              .border(1.dp, Color(0xFF334155).copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Buscar",
              tint = Color(0xFF38BDF8),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier.weight(1f),
              contentAlignment = Alignment.CenterStart
            ) {
              if (phraseSearchQuery.isEmpty()) {
                Text(
                  text = "Buscar",
                  style = MaterialTheme.typography.bodyMedium,
                  color = Color(0xFF64748B),
                  fontSize = 13.5.sp
                )
              }
              innerTextField()
            }
            if (phraseSearchQuery.isNotEmpty()) {
              IconButton(
                onClick = { phraseSearchQuery = "" },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Limpiar búsqueda de frases",
                  tint = Color(0xFF94A3B8),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      )
    }

    // 2. Chips de categorías (sin contadores y sin etiqueta de usos)
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      items(categories.size) { index ->
        val cat = categories[index]
        val isSelected = (cat == "Todas" && selectedCategory == null) || selectedCategory == cat
        Surface(
          onClick = { selectedCategory = if (cat == "Todas") null else cat },
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
          border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)),
          modifier = Modifier.testTag("phrase_category_chip_$cat")
        ) {
          Text(
            text = cat,
            color = if (isSelected) Color.White else Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    }

    // 3. Lista de las 50 frases sin caja, textos libres (idéntico al abecedario y números)
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .testTag("everyday_phrases_list"),
      contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 80.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      itemsIndexed(filteredPhrases, key = { _, phrase -> phrase.id }) { index, phrase ->
        val count = phraseCounts[phrase.id] ?: 0
        EverydayPhraseRowItem(
          index = phrase.id,
          phrase = phrase,
          count = count,
          onSpeak = { onSpeakPhrase(phrase) },
          onAddToCanvas = { onAddPhraseToCanvas(phrase) }
        )
      }
    }
  }
}

/**
 * Fila individual para una frase cotidiana:
 * - Sin caja exterior ni fondo pesado (idéntico a AlphabetLetterRowItem y NumberRowItem).
 * - Separado por una línea fina horizontal al final.
 * - Muestra el número índice (#1 .. #50), categoría, frase en español, frase en inglés, contador crudo.
 * - Al tocar la fila se sube la frase directamente al lienzo de pruebas (y se pronuncia).
 * - Botón de sonido de la frase en inglés con TTS ("🔊").
 */
@Composable
private fun EverydayPhraseRowItem(
  index: Int,
  phrase: EverydayPhrase,
  count: Int,
  onSpeak: () -> Unit,
  onAddToCanvas: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("phrase_row_${phrase.id}")
      .clickable { onAddToCanvas() }
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Número de índice (1 .. 50) flotando sin caja con mayor tamaño
      Text(
        text = "$index",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B),
        fontSize = 13.sp,
        modifier = Modifier.width(34.dp)
      )

      Spacer(modifier = Modifier.width(6.dp))

      // 2. Textos libres: categoría, contador crudo, español e inglés flotando sin caja
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Categoría pill / texto en amarillo/dorado
          Text(
            text = phrase.category.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color(0xFFFACC15),
              letterSpacing = 0.5.sp
            )
          )

          // Contador crudo de uso de la frase
          Text(
            text = "$count",
            fontSize = 13.5.sp,
            fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Medium,
            color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
            modifier = Modifier.testTag("phrase_count_${phrase.id}")
          )
        }

        // Frase en español (texto libre y destacado con mayor legibilidad)
        Text(
          text = phrase.spanishText,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.5.sp,
            color = Color(0xFFF8FAFC)
          )
        )

        // Frase en inglés con reproductor sonoro
        if (phrase.englishHint.isNotBlank()) {
          Text(
            text = phrase.englishHint,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 13.sp,
              color = Color(0xFF38BDF8).copy(alpha = 0.95f),
              fontWeight = FontWeight.Normal
            )
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // 4. Reproductor de sonido de la frase en inglés (Audio TTS) agrandado
      IconButton(
        onClick = onSpeak,
        modifier = Modifier
          .size(34.dp)
          .testTag("btn_listen_phrase_${phrase.id}")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = "Reproducir sonido de la frase en inglés",
          tint = Color(0xFF38BDF8),
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // 6. Fina línea divisoria horizontal entre frases (idéntico a abecedario y números)
    HorizontalDivider(
      color = Color(0xFF334155).copy(alpha = 0.4f),
      thickness = 0.5.dp,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}

/**
 * Botón Flotante Inferior de Flecha de Navegación Rápida.
 * Cuando YouTube está en pantalla, muestra una flecha mirando hacia abajo (Icons.Default.KeyboardArrowDown)
 * que al tocarla baja y coloca de inmediato la vista del vocabulario y sus funciones (como en la imagen 2).
 * Cuando se está en la vista del vocabulario, la flecha mira hacia arriba para volver a mostrar YouTube.
 */
@Composable
fun ScrollArrowFloatingActionButton(
  isAtTop: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("fab_scroll_navigation"),
    shape = CircleShape,
    color = Color(0xFF101923),
    border = null,
    shadowElevation = 8.dp
  ) {
    Box(
      modifier = Modifier.size(48.dp),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isAtTop) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
        contentDescription = if (isAtTop) "Bajar al vocabulario" else "Subir a YouTube",
        tint = Color(0xFF38BDF8),
        modifier = Modifier.size(24.dp)
      )
    }
  }
}


@Composable
private fun CompactWordInputField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  testTag: String,
  modifier: Modifier = Modifier
) {
  BasicTextField(
    value = value,
    onValueChange = onValueChange,
    singleLine = true,
    textStyle = MaterialTheme.typography.bodyMedium.copy(
      color = Color(0xFFF8FAFC),
      fontSize = 13.sp
    ),
    cursorBrush = SolidColor(Color(0xFF38BDF8)),
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    decorationBox = { innerTextField ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .background(Color(0xFF1E293B), shape = RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        if (value.isEmpty()) {
          Text(
            text = placeholder,
            color = Color(0xFF64748B),
            fontSize = 13.sp
          )
        }
        innerTextField()
      }
    }
  )
}

/**
 * Diálogo de confirmación para eliminar una palabra personalizada (#2810+) creada por el usuario.
 */
@Composable
fun DeleteWordConfirmationDialog(
  word: Word,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = Color(0xFF0F172A),
    icon = {
      Box(
        modifier = Modifier
          .size(46.dp)
          .background(Color(0x26EF4444), shape = CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.DeleteOutline,
          contentDescription = null,
          tint = Color(0xFFEF4444),
          modifier = Modifier.size(24.dp)
        )
      }
    },
    title = {
      Text(
        text = "¿Eliminar palabra?",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFF8FAFC),
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("dialog_delete_word_title")
      )
    },
    text = {
      Text(
        text = "¿Estás seguro de que deseas eliminar la palabra \"${word.lemma}\" (#${word.rank}) de tu vocabulario? Esta acción no se puede deshacer.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF94A3B8),
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("dialog_delete_word_message")
      )
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFFEF4444),
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("btn_confirm_delete_word")
      ) {
        Text("Eliminar", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("btn_cancel_delete_word")
      ) {
        Text("Cancelar", color = Color(0xFF94A3B8))
      }
    },
    modifier = Modifier.testTag("dialog_delete_word_confirmation")
  )
}

/**
 * Diálogo interactivo con caja tipo HUD para añadir o actualizar una palabra personalizada (#2810+).
 * - En modo añadir: El rank se asigna automáticamente (maxOf(2809, maxRank) + 1).
 * - En modo actualizar: Se auto-rellena con los datos existentes de ese bloque y conserva su rank original.
 * Guarda en custom_words.json en el almacenamiento local del teléfono sin alterar
 * el vocabulario nativo de 2,809 palabras.
 */
@Composable
fun AddCustomWordDialog(
  nextRank: Int,
  editingWord: Word? = null,
  onDismiss: () -> Unit,
  onSaveWord: (word: String, category: String, spanish: String, example: String) -> Unit,
  onPreviewSpeak: (String) -> Unit
) {
  val isEditing = editingWord != null
  val displayRank = editingWord?.rank ?: nextRank

  var wordInput by remember(editingWord) { mutableStateOf(editingWord?.lemma ?: "") }
  var spanishInput by remember(editingWord) { mutableStateOf(editingWord?.spanishTranslation ?: "") }
  var exampleInput by remember(editingWord) { mutableStateOf(editingWord?.microExample ?: "") }
  var selectedCategory by remember(editingWord) {
    mutableStateOf(
      editingWord?.rawPartOfSpeech?.takeIf { it.isNotBlank() } ?: "sustantivo"
    )
  }

  val previewPartOfSpeech = remember(selectedCategory) {
    com.example.domain.util.WordGrammarCatalog.getPrimaryCategory(selectedCategory)
  }
  val syntaxColor = getWordSyntaxColor(previewPartOfSpeech)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFF0F172A),
      border = null,
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 16.dp)
        .testTag("dialog_add_custom_word")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Cabecera del diálogo
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Add,
              contentDescription = null,
              tint = syntaxColor,
              modifier = Modifier.size(24.dp)
            )
            Column {
              Text(
                text = if (isEditing) "ACTUALIZAR PALABRA (#$displayRank)" else "AÑADIR PALABRA (#$displayRank)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFF8FAFC),
                fontSize = 16.sp
              )
              Text(
                text = "Se guardará en custom_words.json en tu dispositivo",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .testTag("btn_close_add_word_x")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Cerrar",
              tint = Color(0xFF94A3B8),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)

        // 1. VISTA PREVIA DE LA CAJA HUD
        Text(
          text = "VISTA PREVIA DE TU TARJETA HUD:",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF94A3B8),
          fontSize = 10.sp
        )

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("preview_custom_word_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(1.dp, syntaxColor),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Badge de Rank
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF0F172A),
              border = null,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "#$displayRank",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF38BDF8)
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = if (wordInput.isNotBlank()) wordInput.trim() else "(palabra vacía)",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                  ),
                  color = if (wordInput.isNotBlank()) syntaxColor else Color(0xFF64748B)
                )
                Text(
                  text = "${editingWord?.useCount ?: 0}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Normal,
                  color = Color(0xFF64748B)
                )
              }
              Text(
                text = selectedCategory,
                style = MaterialTheme.typography.labelSmall,
                color = syntaxColor.copy(alpha = 0.85f),
                fontSize = 11.sp
              )
              if (spanishInput.isNotBlank()) {
                Text(
                  text = spanishInput.trim(),
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFFCBD5E1),
                  fontSize = 11.sp
                )
              }
            }

            // Icono de volumen preview
            IconButton(
              onClick = {
                if (wordInput.isNotBlank()) {
                  onPreviewSpeak(wordInput.trim())
                }
              },
              modifier = Modifier.size(34.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Escuchar pronunciación",
                tint = if (wordInput.isNotBlank()) syntaxColor else Color(0xFF64748B),
                modifier = Modifier.size(19.dp)
              )
            }
          }
        }

        // 2. FORMULARIO DE ENTRADA
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Palabra en inglés:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE2E8F0),
            fontSize = 12.sp
          )
          CompactWordInputField(
            value = wordInput,
            onValueChange = { wordInput = it },
            placeholder = "Ej: breakthrough, resilient, feedback...",
            testTag = "input_new_word_lemma"
          )
        }

        // Selector de Categoría Sintáctica
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Categoría sintáctica (Color pasivo):",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE2E8F0),
            fontSize = 12.sp
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            val categories = listOf(
              Triple("🟡 Conector", "conector (conjunción)", Color(0xFFFACC15)),
              Triple("🟣 Sujeto", "pronombre", Color(0xFFA855F7)),
              Triple("🔵 Verbo", "verbo", Color(0xFF3B82F6)),
              Triple("🟢 Sustantivo", "sustantivo", Color(0xFF34D399)),
              Triple("⚪ Modificador", "modificador (adjetivo)", Color(0xFFF8FAFC))
            )
            categories.forEach { (label, value, catColor) ->
              val isSelected = selectedCategory == value
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) catColor.copy(alpha = 0.35f) else catColor.copy(alpha = 0.12f),
                border = null,
                modifier = Modifier
                  .clickable { selectedCategory = value }
                  .testTag("chip_select_category_${label.filter { it.isLetter() }}")
              ) {
                Text(
                  text = label,
                  color = if (isSelected) Color.White else catColor,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        // Campo Traducción al español
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Traducción al español:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE2E8F0),
            fontSize = 12.sp
          )
          CompactWordInputField(
            value = spanishInput,
            onValueChange = { spanishInput = it },
            placeholder = "Ej: avance importante, innovación...",
            testTag = "input_new_word_spanish"
          )
        }

        // Campo Micro-ejemplo (opcional)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Micro-ejemplo en inglés (opcional):",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE2E8F0),
            fontSize = 12.sp
          )
          CompactWordInputField(
            value = exampleInput,
            onValueChange = { exampleInput = it },
            placeholder = "Ej: a major breakthrough",
            testTag = "input_new_word_example"
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Botón Guardar
        Button(
          onClick = {
            if (wordInput.isNotBlank()) {
              onSaveWord(
                wordInput.trim(),
                selectedCategory,
                spanishInput.trim(),
                exampleInput.trim()
              )
            }
          },
          enabled = wordInput.isNotBlank(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_save_custom_word"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF38BDF8),
            disabledContainerColor = Color(0xFF334155)
          )
        ) {
          Text(
            text = if (isEditing) "ACTUALIZAR PALABRA #$displayRank" else "GUARDAR PALABRA #$displayRank",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (wordInput.isNotBlank()) Color(0xFF0F172A) else Color(0xFF94A3B8)
          )
        }
      }
    }
  }
}

/**
 * Icono de velocímetro analógico interactivo con aguja animada.
 * La aguja se mueve en tiempo real reflejando el nivel de velocidad:
 * - 0.5x (más bajo): aguja orientada a la izquierda (baja velocidad).
 * - 0.8x (medio): aguja en el centro vertical.
 * - 1.0x (alta velocidad): aguja orientada a la derecha (máxima velocidad).
 * Permite reconocer el nivel visualmente sin necesidad de textos ni números.
 */
@Composable
fun AnimatedSpeedometerIcon(
  currentSpeed: Float,
  modifier: Modifier = Modifier
) {
  val targetAngle = when {
    currentSpeed <= 0.35f -> -50f // Lo más bajo (0.3x): punta a la izquierda
    currentSpeed <= 0.65f -> 0f   // En medio (0.5x): aguja centrada verticalmente
    else -> 50f                   // Alta velocidad (0.8x): punta hacia la derecha
  }

  Canvas(modifier = modifier.size(30.dp)) {
    val w = size.width
    val h = size.height
    // Centro del dial
    val centerDial = Offset(w / 2f, h * 0.62f)
    val radius = w * 0.44f

    // 1. Arco base del velocímetro
    drawArc(
      color = Color(0xFF00E5FF).copy(alpha = 0.5f),
      startAngle = 145f,
      sweepAngle = 250f,
      useCenter = false,
      topLeft = Offset(centerDial.x - radius, centerDial.y - radius),
      size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
      style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
    )

    // 2. Marcas o puntos guía en las 3 posiciones clave
    val markAngles = listOf(-50f, 0f, 50f)
    for (markAngle in markAngles) {
      val isCurrent = (markAngle == targetAngle)
      val rad = Math.toRadians((markAngle - 90f).toDouble())
      val tickStart = Offset(
        centerDial.x + (radius - 1.dp.toPx()) * cos(rad).toFloat(),
        centerDial.y + (radius - 1.dp.toPx()) * sin(rad).toFloat()
      )
      val tickEnd = Offset(
        centerDial.x + (radius - 4.dp.toPx()) * cos(rad).toFloat(),
        centerDial.y + (radius - 4.dp.toPx()) * sin(rad).toFloat()
      )
      drawLine(
        color = if (isCurrent) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.35f),
        start = tickStart,
        end = tickEnd,
        strokeWidth = if (isCurrent) 2.2.dp.toPx() else 1.3.dp.toPx(),
        cap = StrokeCap.Round
      )
    }

    // 3. Aguja indicadora fija según targetAngle (estática sin animaciones)
    val needleRad = Math.toRadians((targetAngle - 90f).toDouble())
    val needleLength = radius * 0.88f
    val needleTip = Offset(
      centerDial.x + needleLength * cos(needleRad).toFloat(),
      centerDial.y + needleLength * sin(needleRad).toFloat()
    )

    // Línea de la aguja limpia y nítida
    drawLine(
      color = Color(0xFF00E5FF),
      start = centerDial,
      end = needleTip,
      strokeWidth = 2.6.dp.toPx(),
      cap = StrokeCap.Round
    )

    // Línea interior sutil blanca
    drawLine(
      color = Color.White.copy(alpha = 0.75f),
      start = centerDial,
      end = needleTip,
      strokeWidth = 1.0.dp.toPx(),
      cap = StrokeCap.Round
    )

    // Núcleo blanco en la punta
    drawCircle(
      color = Color.White,
      radius = 2.0.dp.toPx(),
      center = needleTip
    )

    // 4. Pivote central
    drawCircle(
      color = Color(0xFF00E5FF),
      radius = 3.4.dp.toPx(),
      center = centerDial
    )
    drawCircle(
      color = Color(0xFF101923),
      radius = 1.5.dp.toPx(),
      center = centerDial
    )
  }
}

/**
 * Botón Flotante Inferior de Velocidad TTS Global.
 * Muestra el velocímetro con aguja interactiva en tiempo real sin números en pantalla.
 * Al tocarlo, cicla inmediatamente: 0.3x -> 0.5x -> 0.8x -> 0.3x.
 */
@Composable
fun SpeedFloatingActionButton(
  currentSpeed: Float,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val speedDesc = when {
    currentSpeed <= 0.35f -> "Velocidad baja: 0.3x"
    currentSpeed <= 0.65f -> "Velocidad media: 0.5x"
    else -> "Velocidad: 0.8x"
  }

  Surface(
    onClick = onClick,
    modifier = modifier.testTag("fab_speed_control"),
    shape = CircleShape,
    color = Color.Transparent,
    border = null,
    shadowElevation = 0.dp
  ) {
    Box(
      modifier = Modifier.size(44.dp),
      contentAlignment = Alignment.Center
    ) {
      AnimatedSpeedometerIcon(
        currentSpeed = currentSpeed
      )
    }
  }
}



/**
 * 1. EL GUIADOR SUPERIOR (La Brújula de Orden y Color - Interactivo)
 * [🟡 Conector] ➔ [🟣 Sujeto] ➔ [🔵 Verbo] ➔ [⚪ Objeto]  [✕]
 *
 * Al tocar cualquier categoría, filtra y enlista de inmediato solo esas palabras.
 * El botón 'X' limpia la selección para volver a mostrar las 2,809 palabras.
 */
@Composable
private fun SyntaxCompassHeader(
  selectedFilter: SyntaxFilter?,
  onSelectFilter: (SyntaxFilter) -> Unit,
  onExplainFilter: (SyntaxFilter) -> Unit = {},
  onClearFilter: () -> Unit
) {
  val isConnectorSelected = selectedFilter == SyntaxFilter.CONNECTOR
  val isPronounSelected = selectedFilter == SyntaxFilter.SUBJECT
  val isVerbSelected = selectedFilter == SyntaxFilter.VERB
  val isNounSelected = selectedFilter == SyntaxFilter.NOUN
  val isModifierSelected = selectedFilter == SyntaxFilter.MODIFIER

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
    ) {
      CompassStepChip(
        color = Color(0xFFFACC15),
        label = "Conector",
        isSelected = isConnectorSelected,
        onClick = { onSelectFilter(SyntaxFilter.CONNECTOR) },
        onLongClick = { onExplainFilter(SyntaxFilter.CONNECTOR) },
        testTag = "compass_chip_Conector"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
      CompassStepChip(
        color = Color(0xFFA855F7),
        label = "Sujeto",
        isSelected = isPronounSelected,
        onClick = { onSelectFilter(SyntaxFilter.SUBJECT) },
        onLongClick = { onExplainFilter(SyntaxFilter.SUBJECT) },
        testTag = "compass_chip_Sujeto"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
      CompassStepChip(
        color = Color(0xFF38BDF8),
        label = "Verbo",
        isSelected = isVerbSelected,
        onClick = { onSelectFilter(SyntaxFilter.VERB) },
        onLongClick = { onExplainFilter(SyntaxFilter.VERB) },
        testTag = "compass_chip_Verbo"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
      CompassStepChip(
        color = Color(0xFF34D399),
        label = "Sustantivo",
        isSelected = isNounSelected,
        onClick = { onSelectFilter(SyntaxFilter.NOUN) },
        onLongClick = { onExplainFilter(SyntaxFilter.NOUN) },
        testTag = "compass_chip_Sustantivo"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
      CompassStepChip(
        color = Color(0xFFE2E8F0),
        label = "Modificador",
        isSelected = isModifierSelected,
        onClick = { onSelectFilter(SyntaxFilter.MODIFIER) },
        onLongClick = { onExplainFilter(SyntaxFilter.MODIFIER) },
        testTag = "compass_chip_Modificador"
      )
    }
  }
}

@Composable
private fun CompassStepChip(
  color: Color,
  label: String,
  isSelected: Boolean = false,
  onClick: () -> Unit = {},
  onLongClick: () -> Unit = {},
  testTag: String = "compass_chip_${label.filter { it.isLetter() }}"
) {
  val haptic = LocalHapticFeedback.current
  val currentOnClick by rememberUpdatedState(onClick)
  val currentOnLongClick by rememberUpdatedState(onLongClick)

  Box(
    modifier = Modifier
      .testTag(testTag)
      .pointerInput(Unit) {
        coroutineScope {
          awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val downPosition = down.position
            val touchSlop = viewConfiguration.touchSlop
            var longPressTriggered = false

            val timerJob = launch {
              delay(2000L) // 2 segundos exactos requeridos por el usuario
              longPressTriggered = true
              try {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              } catch (_: Exception) {}
              currentOnLongClick()
            }

            while (true) {
              val event = awaitPointerEvent()
              val pointer = event.changes.firstOrNull { it.id == down.id }
              if (pointer == null || !pointer.pressed) {
                // El usuario levantó el dedo
                timerJob.cancel()
                if (!longPressTriggered && pointer != null) {
                  currentOnClick()
                }
                break
              }
              // Si el usuario arrastra más allá de touchSlop (ej. haciendo scroll horizontal en la fila)
              val dx = pointer.position.x - downPosition.x
              val dy = pointer.position.y - downPosition.y
              val distance = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
              if (distance > touchSlop) {
                timerJob.cancel()
                break
              }
            }
          }
        }
      }
      .padding(horizontal = 4.dp, vertical = 2.dp)
  ) {
    Text(
      text = label,
      color = if (isSelected) color else color.copy(alpha = 0.95f),
      fontSize = 13.5.sp,
      fontWeight = if (isSelected) FontWeight.Black else FontWeight.ExtraBold,
      textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None,
      maxLines = 1,
      softWrap = false
    )
  }
}

/**
 * Segunda bandeja horizontal deslizable ("El tipo del tipo").
 * Muestra los subtipos correspondientes a la categoría principal activa.
 * Empieza siempre con el botón 'Más Usados' (ordenado de mayor a menor conteo, excluyendo 0 usos).
 * Utiliza exactamente el color de acento de la categoría padre seleccionada.
 */
@Composable
private fun SubCategoryHorizontalTray(
  filter: SyntaxFilter,
  selectedSubCategory: String,
  onSelectSubCategory: (String) -> Unit
) {
  val subCategories = remember(filter) { filter.getSubCategories() }
  val accentColor = remember(filter) {
    when (filter) {
      SyntaxFilter.CONNECTOR -> Color(0xFFFACC15) // Amarillo
      SyntaxFilter.SUBJECT -> Color(0xFFA855F7)   // Morado
      SyntaxFilter.VERB -> Color(0xFF38BDF8)      // Azul
      SyntaxFilter.NOUN -> Color(0xFF34D399)      // Verde
      SyntaxFilter.MODIFIER -> Color(0xFFE2E8F0)  // Blanco
    }
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      subCategories.forEach { subCat ->
        val isSelected = (subCat == selectedSubCategory) ||
          (subCat == SyntaxFilter.SUBCAT_MOST_USED && (selectedSubCategory.isBlank() || selectedSubCategory == "Todos" || selectedSubCategory == SyntaxFilter.SUBCAT_MOST_USED))

        Surface(
          onClick = { onSelectSubCategory(subCat) },
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) accentColor.copy(alpha = 0.22f) else Color(0x1F1E293B),
          border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) accentColor else Color(0x3394A3B8)
          ),
          modifier = Modifier.testTag("subcat_chip_${subCat.filter { it.isLetter() }}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            if (isSelected) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .background(accentColor, CircleShape)
              )
            }
            Text(
              text = subCat,
              color = if (isSelected) accentColor else Color(0xFFCBD5E1),
              fontSize = 11.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }
    }
  }
}

private data class SyntaxTypeInfo(
  val title: String,
  val badgeEmoji: String,
  val role: String,
  val positionInPhrase: String,
  val examples: String,
  val pedagogicalTip: String
)

private fun getSyntaxTypeInfo(filter: SyntaxFilter): SyntaxTypeInfo {
  return when (filter) {
    SyntaxFilter.CONNECTOR -> SyntaxTypeInfo(
      title = "CONECTOR",
      badgeEmoji = "🟡",
      role = "Une ideas, oraciones y cláusulas para dar fluidez, causa, efecto o contraste lógico al pensamiento.",
      positionInPhrase = "Se ubica al inicio de la frase o como puente entre dos ideas completas.",
      examples = "because (porque), and (y), however (sin embargo), although (aunque), so (así que), but (pero), if (si).",
      pedagogicalTip = "💡 Te permite pensar en bloques continuos sin traducir palabra por palabra."
    )
    SyntaxFilter.SUBJECT -> SyntaxTypeInfo(
      title = "SUJETO / PRONOMBRE",
      badgeEmoji = "🟣",
      role = "Representa quién o qué realiza la acción o sobre quién se afirma algo en la frase.",
      positionInPhrase = "Va al principio de oraciones afirmativas, posicionado antes del verbo.",
      examples = "I (yo), you (tú/ustedes), he (él), she (ella), it (eso/ello), we (nosotros), they (ellos).",
      pedagogicalTip = "💡 En inglés el sujeto casi NUNCA se omite, a diferencia del español."
    )
    SyntaxFilter.VERB -> SyntaxTypeInfo(
      title = "VERBO",
      badgeEmoji = "🔵",
      role = "Expresa la acción, estado, proceso o movimiento. Es el motor indispensable de toda oración.",
      positionInPhrase = "Sigue directamente al sujeto y determina el tiempo verbal (presente, pasado, futuro).",
      examples = "be (ser/estar), have (tener/haber), do (hacer), go (ir), want (querer), can (poder), make (hacer).",
      pedagogicalTip = "💡 Sin verbo no hay frase; es el motor que da vida a tus ideas."
    )
    SyntaxFilter.NOUN -> SyntaxTypeInfo(
      title = "SUSTANTIVO / OBJETO",
      badgeEmoji = "🟢",
      role = "Nombra personas, cosas, lugares, objetos o conceptos sobre los que recae la acción.",
      positionInPhrase = "Suele ubicarse después del verbo como objetivo o complemento de la frase.",
      examples = "time (tiempo), year (año), people (personas), world (mundo), water (agua), house (casa), life (vida).",
      pedagogicalTip = "💡 Es el destino o meta concreta hacia donde apunta la acción del verbo."
    )
    SyntaxFilter.MODIFIER -> SyntaxTypeInfo(
      title = "MODIFICADOR",
      badgeEmoji = "⚪",
      role = "Describe, califica, delimita o matiza el significado de sustantivos y verbos (artículos, adjetivos, adverbios).",
      positionInPhrase = "En inglés los adjetivos van SIEMPRE antes del sustantivo que modifican.",
      examples = "the (el/la), a (un/una), good (bueno), new (nuevo), big (grande), first (primero), very (muy).",
      pedagogicalTip = "💡 Agrega detalle, cantidad y color específico a tu frase (ej: 'a new world')."
    )
  }
}

/**
 * Bandeja flotante minimalista que explica de qué se encarga el tipo de palabra al mantenerlo presionado durante 2 segundos.
 * Flota en la parte inferior, ocupa muy poca pantalla, tiene manija para deslizar hacia abajo y su contenido es deslizable.
 */
@Composable
fun SyntaxExplanationTray(
  filter: SyntaxFilter,
  onDismiss: () -> Unit
) {
  val info = remember(filter) { getSyntaxTypeInfo(filter) }
  val syntaxColor = when (filter) {
    SyntaxFilter.CONNECTOR -> Color(0xFFFACC15)
    SyntaxFilter.SUBJECT -> Color(0xFFA855F7)
    SyntaxFilter.VERB -> Color(0xFF3B82F6)
    SyntaxFilter.NOUN -> Color(0xFF34D399)
    SyntaxFilter.MODIFIER -> Color(0xFFF8FAFC)
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnClickOutside = true,
      dismissOnBackPress = true
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.45f))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onDismiss
        ),
      contentAlignment = Alignment.BottomCenter
    ) {
      Surface(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
        color = Color(0xF20B132B), // Acrílico oscuro HUD elegante y semi-translúcido
        border = BorderStroke(1.2.dp, syntaxColor.copy(alpha = 0.70f)),
        shadowElevation = 16.dp,
        modifier = Modifier
          .fillMaxWidth(0.95f)
          .padding(bottom = 12.dp, start = 8.dp, end = 8.dp)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {} // Evitar cerrar al hacer clic dentro de la bandeja
          )
          .pointerInput(Unit) {
            detectVerticalDragGestures { change, dragAmount ->
              // Deslizable hacia abajo para cerrar la bandeja flotante
              if (dragAmount > 20f) {
                change.consume()
                onDismiss()
              }
            }
          }
          .testTag("syntax_explanation_tray")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
          // Manija superior deslizable hacia abajo (drag handle)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(width = 38.dp, height = 4.dp)
                .clip(CircleShape)
                .background(Color(0xFF94A3B8).copy(alpha = 0.6f))
            )
          }

          // Cabecera compacta con el tipo y botón cerrar '✕'
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = syntaxColor.copy(alpha = 0.20f),
              border = BorderStroke(1.dp, syntaxColor)
            ) {
              Text(
                text = "${info.badgeEmoji} ${info.title}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = syntaxColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(26.dp)
                .testTag("btn_close_syntax_tray_x")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar explicación",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
          Spacer(modifier = Modifier.height(6.dp))

          // Contenido deslizable hacia abajo si es que su contenido es largo
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 220.dp)
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // 1. ¿De qué se encarga?
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = "¿DE QUÉ SE ENCARGA?",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
              )
              Text(
                text = info.role,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFF8FAFC),
                fontSize = 12.sp,
                lineHeight = 16.sp
              )
            }

            // 2. Posición en la frase
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = "POSICIÓN EN LA FRASE:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
              )
              Text(
                text = info.positionInPhrase,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                lineHeight = 16.sp
              )
            }

            // 3. Ejemplos clave
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = "EJEMPLOS CLAVE:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = syntaxColor,
                fontSize = 10.sp
              )
              Text(
                text = info.examples,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE2E8F0),
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }

            // 4. Tip pedagógico
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1E293B),
              border = BorderStroke(1.dp, Color(0xFF334155)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = info.pedagogicalTip,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF38BDF8),
                fontSize = 11.sp,
                modifier = Modifier.padding(7.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Botón minimalista de cierre / entendido
          Button(
            onClick = onDismiss,
            modifier = Modifier
              .fillMaxWidth()
              .height(34.dp)
              .testTag("btn_close_syntax_tray"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = syntaxColor.copy(alpha = 0.22f)),
            border = BorderStroke(1.dp, syntaxColor.copy(alpha = 0.8f))
          ) {
            Text(
              text = "Entendido (Desliza hacia abajo para cerrar)",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

/**
 * Abre Google Translate con la frase en inglés:
 * 1. Copia la frase automáticamente al Portapapeles (ClipboardManager).
 * 2. Lanza un Intent a la aplicación de Google Translate (com.google.android.apps.translate).
 * 3. Si la app no está instalada, abre el enlace oficial en el navegador web:
 *    https://translate.google.com/?sl=en&tl=es&text=...
 */
fun openGoogleTranslate(context: Context, phrase: String) {
  val cleanPhrase = phrase.trim()
  if (cleanPhrase.isBlank()) return

  // 1. Copiar al portapapeles
  try {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Frase en inglés", cleanPhrase)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Frase copiada al portapapeles", Toast.LENGTH_SHORT).show()
  } catch (_: Exception) {}

  // 2. Intent hacia la app de Google Translate o fallback al navegador
  val encodedText = java.net.URLEncoder.encode(cleanPhrase, "UTF-8")
  val webUrl = "https://translate.google.com/?sl=en&tl=es&text=$encodedText"

  val appIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, cleanPhrase)
    setPackage("com.google.android.apps.translate")
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }

  try {
    context.startActivity(appIntent)
  } catch (_: Exception) {
    try {
      val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(browserIntent)
    } catch (_: Exception) {}
  }
}

/**
 * 2. LA BARRA DE BORRADOR ("Scratchpad" de Frases)
 * Contenedor interactivo fijo con bloques de color, controles de audio y enlace directo
 * a Google Translate.
 *
 * Flujo:
 * - Sin frase: Pantalla limpia, se indica tocar palabras para armar una frase.
 * - Con frase: Aparece el botón de traducir (Translate) para copiar y abrir directamente en Google Translate.
 * - Play (▶️): Reproduce la frase completa corrida con TTS.
 * - Papelera (🗑️): Limpia la frase de inmediato con cero fricción.
 */
@Composable
private fun DraftScratchpadHeader(
  draftWords: List<Word>,
  canUndoDraft: Boolean = false,
  canRedoDraft: Boolean = false,
  onUndo: () -> Unit = {},
  onRedo: () -> Unit = {},
  isPhraseUpdated: Boolean,
  isTranslationTrayExpanded: Boolean,
  translationState: TranslationState,
  onToggleTranslationTray: () -> Unit,
  onTranslate: () -> Unit,
  onRemoveWord: (Int) -> Unit,
  onMoveLeft: (Int) -> Unit,
  onMoveRight: (Int) -> Unit,
  onPlayPhrase: () -> Unit,
  onClearDraft: () -> Unit,
  onSavePhrase: () -> Unit = {},
  onSpeakWord: ((Word) -> Unit)? = null
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 90.dp)
      .padding(horizontal = 16.dp, vertical = 2.dp)
      .testTag("draft_scratchpad"),
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF1E293B).copy(alpha = 0.5f),
    border = null,
    shadowElevation = 0.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 86.dp)
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Cabecera del Scratchpad
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "LIENZO DE PRUEBAS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF38BDF8),
            fontSize = 11.5.sp,
            letterSpacing = 0.5.sp
          )
        }

        // Acciones rápidas (Retroceder, Adelantar, Traducir a Google Translate, Guardar, Play y Limpiar)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Botón Flecha Retroceder cambio (Undo)
          IconButton(
            onClick = onUndo,
            enabled = canUndoDraft,
            modifier = Modifier
              .size(30.dp)
              .testTag("btn_undo_draft")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Undo,
              contentDescription = "Retroceder cambio en lienzo",
              tint = if (canUndoDraft) Color(0xFF38BDF8) else Color(0xFF475569),
              modifier = Modifier.size(19.dp)
            )
          }

          // Botón Flecha Adelantar cambio (Redo)
          IconButton(
            onClick = onRedo,
            enabled = canRedoDraft,
            modifier = Modifier
              .size(30.dp)
              .testTag("btn_redo_draft")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Redo,
              contentDescription = "Adelantar cambio en lienzo",
              tint = if (canRedoDraft) Color(0xFF38BDF8) else Color(0xFF475569),
              modifier = Modifier.size(19.dp)
            )
          }

          // El botón de traducir SOLO existe si hay frase armada en el Lienzo de Pruebas
          if (draftWords.isNotEmpty()) {
            IconButton(
              onClick = onTranslate,
              modifier = Modifier
                .size(30.dp)
                .testTag("btn_translate_practice")
            ) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Traducir y practicar frase en Google Translate",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(19.dp)
              )
            }
          }

          // Botón Guardar frase armada en bandeja lateral (solo icono, sin texto)
          IconButton(
            onClick = onSavePhrase,
            enabled = draftWords.isNotEmpty(),
            modifier = Modifier
              .size(30.dp)
              .testTag("btn_save_draft_phrase")
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = "Guardar frase en bandeja lateral",
              tint = if (draftWords.isNotEmpty()) Color(0xFFFBBF24) else Color(0xFF475569),
              modifier = Modifier.size(19.dp)
            )
          }

          IconButton(
            onClick = onPlayPhrase,
            enabled = draftWords.isNotEmpty(),
            modifier = Modifier
              .size(30.dp)
              .testTag("btn_play_draft")
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Escuchar frase completa",
              tint = if (draftWords.isNotEmpty()) Color(0xFF4ADE80) else Color(0xFF475569),
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = onClearDraft,
            enabled = draftWords.isNotEmpty(),
            modifier = Modifier
              .size(30.dp)
              .testTag("btn_clear_draft")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Limpiar borrador",
              tint = if (draftWords.isNotEmpty()) Color(0xFFF87171) else Color(0xFF475569),
              modifier = Modifier.size(19.dp)
            )
          }
        }
      }

      // Fila desplazable de bloques (Chips de frase armada)
      LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("draft_chips_row"),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
          contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
        ) {
          itemsIndexed(
            items = draftWords,
            key = { index, word -> "${index}_${word.id}_${word.lemma}" }
          ) { index, word ->
            DraftChip(
              word = word,
              index = index,
              totalCount = draftWords.size,
              onMoveLeft = { onMoveLeft(index) },
              onMoveRight = { onMoveRight(index) },
              onClickRemove = { onRemoveWord(index) },
              onSpeakWord = { onSpeakWord?.invoke(word) }
            )
          }
        }

        // Bandeja desplegable justo debajo de los bloques de palabras con la traducción al español usando Google ML Kit
        AnimatedVisibility(
          visible = isTranslationTrayExpanded,
          enter = expandVertically(animationSpec = tween(350, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(300)),
          exit = shrinkVertically(animationSpec = tween(300, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(250))
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.45f)),
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp)
              .testTag("translation_tray")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF0284C7).copy(alpha = 0.25f),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
              ) {
                Text(
                  text = "ES",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF38BDF8),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 10.sp
                )
              }

              when (translationState) {
                is TranslationState.DownloadingModel -> {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(14.dp),
                      strokeWidth = 2.dp,
                      color = Color(0xFF38BDF8)
                    )
                    Text(
                      text = "Descargando modelo...",
                      style = MaterialTheme.typography.bodySmall,
                      color = Color(0xFF94A3B8),
                      fontSize = 12.sp,
                      modifier = Modifier.testTag("text_translation_downloading")
                    )
                  }
                }
                is TranslationState.Translating -> {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(14.dp),
                      strokeWidth = 2.dp,
                      color = Color(0xFF38BDF8)
                    )
                    Text(
                      text = "Traduciendo frase...",
                      style = MaterialTheme.typography.bodySmall,
                      color = Color(0xFF94A3B8),
                      fontSize = 12.sp,
                      modifier = Modifier.testTag("text_translation_loading")
                    )
                  }
                }
                is TranslationState.Success -> {
                  Text(
                    text = translationState.translation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFFF1F5F9),
                      fontSize = 14.sp
                    ),
                    modifier = Modifier
                      .weight(1f)
                      .testTag("text_translated_phrase")
                  )
                }
                is TranslationState.Error -> {
                  Text(
                    text = translationState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF87171),
                    fontSize = 12.sp,
                    modifier = Modifier
                      .weight(1f)
                      .testTag("text_translation_error")
                  )
                }
                TranslationState.Idle -> {
                  Text(
                    text = "Arma una frase para ver la traducción instantánea",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                  )
                }
              }
            }
          }
        }
    }
  }
}


/**
 * Chip interactivo del borrador con soporte para desplazamiento por pulsación larga y arrastre.
 */
@Composable
private fun DraftChip(
  word: Word,
  index: Int,
  totalCount: Int,
  onMoveLeft: () -> Unit,
  onMoveRight: () -> Unit,
  onClickRemove: () -> Unit,
  onSpeakWord: (() -> Unit)? = null
) {
  val chipColor = getWordSyntaxColor(word)
  var accumulatedDrag by remember { mutableStateOf(0f) }
  var isDragging by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isDragging) chipColor.copy(alpha = 0.22f) else Color.Transparent,
    border = if (isDragging) BorderStroke(1.dp, chipColor) else null,
    modifier = Modifier
      .testTag("chip_word_${word.lemma}_$index")
      .graphicsLayer {
        translationX = accumulatedDrag
        if (isDragging) {
          scaleX = 1.08f
          scaleY = 1.08f
          shadowElevation = 4f
        }
      }
      .pointerInput(index, totalCount) {
        detectDragGesturesAfterLongPress(
          onDragStart = {
            isDragging = true
            accumulatedDrag = 0f
          },
          onDragEnd = {
            isDragging = false
            accumulatedDrag = 0f
          },
          onDragCancel = {
            isDragging = false
            accumulatedDrag = 0f
          },
          onDrag = { change, dragAmount ->
            change.consume()
            accumulatedDrag += dragAmount.x
            val currentDrag = accumulatedDrag.toDouble()
            if (currentDrag > 50.0 && index < totalCount - 1) {
              onMoveRight()
              accumulatedDrag = 0f
            } else if (currentDrag < -50.0 && index > 0) {
              onMoveLeft()
              accumulatedDrag = 0f
            }
          }
        )
      }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = word.lemma,
        color = chipColor,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        modifier = Modifier
          .clickable { onSpeakWord?.invoke() }
      )

      // Eliminar palabra del borrador (la x a su costado)
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .clickable(onClick = onClickRemove)
          .testTag("btn_remove_chip_$index"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Eliminar ${word.lemma}",
          tint = chipColor.copy(alpha = 0.85f),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

/**
 * Predictor de Conexión en Vivo con soporte de los 3 grupos de conectores.
 */
@Composable
private fun LiveConnectionPredictor(lastWord: Word?) {
  val text: String
  val highlightColor: Color

  if (lastWord == null) {
    text = "💡 Inicia tu frase con: [🟡 Conector], [🟣 Sujeto], [⚪ Modificador] o [🟢 Sustantivo]"
    highlightColor = Color(0xFF94A3B8)
  } else {
    when {
      lastWord.partOfSpeech.isConnector -> {
        text = "🔗 Conecta con: [🟣 Sujeto], [⚪ Modificador] o [🟢 Sustantivo]"
        highlightColor = Color(0xFFFACC15) // Amarillo
      }
      lastWord.partOfSpeech == PartOfSpeech.PRONOUN -> {
        text = "🔗 Conecta con: [🔵 Verbo] o [⚪ Modificador]"
        highlightColor = Color(0xFFA855F7) // Morado
      }
      lastWord.partOfSpeech == PartOfSpeech.VERB -> {
        text = "🔗 Conecta con: [🟢 Sustantivo], [⚪ Modificador] o [🟡 Conector]"
        highlightColor = Color(0xFF3B82F6) // Azul
      }
      lastWord.partOfSpeech == PartOfSpeech.MODIFIER -> {
        text = "🔗 Conecta con: [🟢 Sustantivo] o [🔵 Verbo]"
        highlightColor = Color(0xFFF8FAFC) // Blanco
      }
      lastWord.partOfSpeech == PartOfSpeech.NOUN -> {
        text = "🔗 Conecta con: [🔵 Verbo] o [🟡 Conector]"
        highlightColor = Color(0xFF34D399) // Verde Esmeralda
      }
      else -> {
        text = "🔗 Conecta con: [🟢 Sustantivo] o [🔵 Verbo]"
        highlightColor = Color(0xFFF8FAFC)
      }
    }
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = highlightColor,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )
  }
}

/**
 * 3. TARJETA HUD EN LA LISTA DE LAS 2,809 PALABRAS
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordHudCard(
  word: Word,
  onTap: () -> Unit,
  onSpeak: () -> Unit = {},
  onSpeakExample: ((String) -> Unit)? = null,
  onOpenDetails: (() -> Unit)? = null,
  onEdit: (() -> Unit)? = null,
  onDelete: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val wordColor = getWordSyntaxColor(word)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("word_item_${word.rank}")
      .combinedClickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onTap,
        onLongClick = onSpeak
      )
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Número de rank flotando sin caja con mayor tamaño y legibilidad
      Text(
        text = "${word.rank}",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B),
        fontSize = 13.5.sp,
        modifier = Modifier.width(38.dp)
      )

      Spacer(modifier = Modifier.width(6.dp))

      // Palabra, contador crudo, traducción permanente y categoría
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = word.lemma,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            ),
            color = wordColor
          )
          // Contador crudo de uso: sin fondo, sin bordes, con mayor visibilidad
          Text(
            text = "${word.useCount}",
            fontSize = 14.sp,
            fontWeight = if (word.useCount > 0) FontWeight.Bold else FontWeight.Medium,
            color = if (word.useCount > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
            modifier = Modifier.testTag("word_use_count_${word.rank}")
          )
        }

        // Traducción al español PERMANENTEMENTE VISIBLE (sin bandejita ni flecha)
        Text(
          text = word.displaySpanishTranslation,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          ),
          color = Color(0xFFF1F5F9),
          maxLines = 1,
          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
          modifier = Modifier
            .padding(top = 1.dp)
            .testTag("word_spanish_${word.rank}")
        )

        val categorySubcategoryLabel = remember(word) {
          val sub = word.subcategoria
          if (!sub.isNullOrBlank()) {
            "${word.displayCategoryLabel} • $sub"
          } else {
            word.displayCategoryLabel
          }
        }
        Text(
          text = categorySubcategoryLabel,
          style = MaterialTheme.typography.labelSmall,
          color = wordColor.copy(alpha = 0.85f),
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
          modifier = Modifier.padding(top = 1.dp)
        )
      }

      // Icono de audio flotando con botón e icono agrandados
      IconButton(
        onClick = onSpeak,
        modifier = Modifier
          .size(36.dp)
          .testTag("btn_speak_word_${word.rank}")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = "Pronunciar ${word.lemma}",
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(20.dp)
        )
      }

      // Icono de edición (SOLO para palabras personalizadas con rank >= 2812)
      if (word.rank >= 2812 && onEdit != null) {
        IconButton(
          onClick = onEdit,
          modifier = Modifier
            .size(36.dp)
            .testTag("btn_edit_word_${word.rank}")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar palabra ${word.lemma}",
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(19.dp)
          )
        }
      }

      // Icono de eliminar / tacho de basura (SOLO para palabras personalizadas con rank >= 2812)
      if (word.rank >= 2812 && onDelete != null) {
        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(36.dp)
            .testTag("btn_delete_word_${word.rank}")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Eliminar palabra ${word.lemma}",
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(19.dp)
          )
        }
      }
    }

    // Simple y fina línea divisoria entre palabras
    HorizontalDivider(
      color = Color(0xFF334155).copy(alpha = 0.4f),
      thickness = 0.5.dp,
      modifier = Modifier.padding(top = 5.dp)
    )
  }
}


