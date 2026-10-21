package com.example.presentation.vocabulary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import com.example.presentation.youtube.RawYouTubeTray
import com.example.presentation.youtube.FloatingYouTubeTray
import com.example.presentation.gemini.FloatingGeminiWebTray
import com.example.presentation.translate.FloatingGoogleTranslateWebTray
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import com.example.domain.translation.TranslationState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VocabularyScreen(
  viewModel: VocabularyViewModel,
  onBack: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val words by viewModel.words.collectAsStateWithLifecycle()
  val filteredWords by viewModel.filteredWords.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val draftWords by viewModel.draftWords.collectAsStateWithLifecycle()
  val isPhraseUpdated by viewModel.isPhraseUpdated.collectAsStateWithLifecycle()
  val isTranslationTrayExpanded by viewModel.isTranslationTrayExpanded.collectAsStateWithLifecycle()
  val translationState by viewModel.translationState.collectAsStateWithLifecycle()
  val selectedSyntaxFilter by viewModel.selectedSyntaxFilter.collectAsStateWithLifecycle()
  val currentSpeechSpeed by viewModel.currentSpeechSpeed.collectAsStateWithLifecycle()
  val spanishInputText by viewModel.spanishInputText.collectAsStateWithLifecycle()
  val spanishTranslationState by viewModel.spanishTranslationState.collectAsStateWithLifecycle()
  val showAddWordDialog by viewModel.showAddWordDialog.collectAsStateWithLifecycle()
  val nextRank = remember(words) { maxOf(2809, words.maxOfOrNull { it.rank } ?: 2809) + 1 }

  val context = LocalContext.current
  var showRoadmapDialog by rememberSaveable { mutableStateOf(false) }
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
    floatingActionButtonPosition = FabPosition.Center,
    floatingActionButton = {
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // 1. Costado izquierdo: Ruta / Brújula
        RoadmapFloatingActionButton(
          isOpen = showRoadmapDialog,
          onClick = { showRoadmapDialog = !showRoadmapDialog }
        )
        // 2. Costado izquierdo: Gemini Web
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
        // 3. EN MEDIO: Añadir palabra (+)
        AddWordFloatingActionButton(
          onClick = { viewModel.openAddWordDialog() }
        )
        // 4. EN MEDIO: Graduador de velocidad de sonido
        SpeedFloatingActionButton(
          currentSpeed = currentSpeechSpeed,
          onClick = { viewModel.onToggleSpeed() }
        )
        // 5. El otro lado: YouTube Web
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
        // 6. El otro lado: Google Translate Web
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
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        // BANDEJA FIJA: Brújula sintáctica, traductor, lienzo de pruebas y buscadores (siempre visibles)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 10.dp, bottom = 2.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // BANDEJA ESCRITOR TRADUCTOR ESPAÑOL ➔ INGLÉS - JAMÁS SE OCULTA
          SpanishToEnglishWriterCard(
            spanishText = spanishInputText,
            onSpanishTextChanged = { viewModel.updateSpanishInputText(it) },
            onTranslateNow = { viewModel.translateSpanishNow() },
            onClear = { viewModel.clearSpanishTranslator() },
            translationState = spanishTranslationState,
            onSpeakEnglish = { viewModel.speakWord(it) },
            onAddToCanvas = { viewModel.addSpanishTranslationToDraft(it) },
            onWordClick = { word ->
              if (jumpRankInput.isNotEmpty()) {
                jumpRankInput = ""
              }
              viewModel.clearSyntaxFilter()
              viewModel.updateSearchQuery(word)
              coroutineScope.launch {
                listState.scrollToItem(0)
              }
            }
          )

          // LA BARRA DE BORRADOR ("Scratchpad" de Frases) - LIENZO DE PRUEBAS
          // Ubicado debajo del traductor español y antes del buscador de vocabulario
          DraftScratchpadHeader(
            draftWords = draftWords,
            isPhraseUpdated = isPhraseUpdated,
            isTranslationTrayExpanded = isTranslationTrayExpanded,
            translationState = translationState,
            onToggleTranslationTray = { viewModel.toggleTranslationTray() },
            onTranslate = {
              if (!isTranslationTrayExpanded) {
                viewModel.toggleTranslationTray()
              } else {
                viewModel.translateCurrentDraft()
              }
            },
            onRemoveWord = { index -> viewModel.removeWordFromDraft(index) },
            onMoveLeft = { index -> viewModel.moveWordLeft(index) },
            onMoveRight = { index -> viewModel.moveWordRight(index) },
            onPlayPhrase = { viewModel.playFullDraftPhrase() },
            onClearDraft = { viewModel.clearDraft() }
          )

          // FILA DE BUSCADORES DE PALABRAS Y SALTO NUMÉRICO (# 1-2809) - ULTRA-FINAS (LA MITAD DEL ALTO)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Buscador de texto tradicional (Filtro) - Delgada (30dp de alto, mitad del tamaño)
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
                    .height(30.dp)
                    .background(Color(0xFF0F172A), shape = RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF334155), shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                  ) {
                    innerTextField()
                  }
                  if (searchQuery.isNotEmpty()) {
                    IconButton(
                      onClick = { viewModel.updateSearchQuery("") },
                      modifier = Modifier.size(22.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                      )
                    }
                  }
                }
              }
            )

            // Mini-Buscador de Salto Numérico (#) (Teletransporte sin filtrar) - Delgada (30dp de alto, mitad del tamaño)
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
                .width(98.dp)
                .testTag("jump_rank_input"),
              decorationBox = { innerTextField ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(Color(0xFF0F172A), shape = RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF334155), shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  if (jumpRankInput.isEmpty()) {
                    Text(
                      text = "# 1-${words.maxOfOrNull { it.rank } ?: 2809}",
                      style = MaterialTheme.typography.bodySmall,
                      color = Color(0xFF64748B),
                      fontSize = 11.sp,
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
        }

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
        when {
          isLoading -> {
            item(key = "loading_state") {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  CircularProgressIndicator()
                  Text(
                    text = "Cargando palabras en orden de frecuencia...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
          filteredWords.isEmpty() -> {
            item(key = "empty_state") {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (searchQuery.isNotEmpty()) "No se encontraron coincidencias para \"$searchQuery\"" else "Cargando base de datos...",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
          else -> {
            itemsIndexed(
              items = filteredWords,
              key = { _, item -> item.id }
            ) { _, word ->
              Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                WordHudCard(
                  word = word,
                  onTap = { viewModel.addWordToDraft(word) },
                  onSpeak = { viewModel.speakWord(word.lemma) },
                  onSpeakExample = { example -> viewModel.speakWord(example) }
                )
              }
            }
          }
        }
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
    }
  }

  if (showRoadmapDialog) {
    TacticalRoadmapDialog(onDismiss = { showRoadmapDialog = false })
  }

  if (showAddWordDialog) {
    AddCustomWordDialog(
      nextRank = nextRank,
      onDismiss = { viewModel.closeAddWordDialog() },
      onSaveWord = { word, category, spanish, example ->
        viewModel.addNewCustomWord(word, category, spanish, example)
        coroutineScope.launch {
          val newIndex = words.size.coerceAtLeast(0)
          listState.animateScrollToItem(newIndex)
        }
      },
      onPreviewSpeak = { text -> viewModel.speakWord(text) }
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
    color = Color(0xFF101923),
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFFFF0000)) else null,
    shadowElevation = 8.dp
  ) {
    Box(
      modifier = Modifier.size(48.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = Color(0xFFFF0000),
        modifier = Modifier.size(28.dp)
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
 * Botón Flotante Inferior de Ruta Táctica (Brújula).
 * Ubicado inmediatamente al lado del botón de YouTube, con solo el icono de brújula (0 texto).
 * Al tocarlo abre la bandeja de Ruta Táctica normalmente.
 */
@Composable
fun RoadmapFloatingActionButton(
  isOpen: Boolean = false,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("btn_roadmap_shortcut"),
    shape = CircleShape,
    color = Color(0xFF101923),
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF38BDF8)) else null,
    shadowElevation = 8.dp
  ) {
    Box(
      modifier = Modifier.size(48.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = Color(0xFF0284C7),
        modifier = Modifier.size(28.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = "Abrir Ruta Táctica",
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
 * Botón Flotante Inferior de Géminis Web.
 * Ubicado inmediatamente al lado del botón de YouTube. Abre de manera automática
 * Géminis Web en el navegador para cualquier consulta o duda de inglés en tiempo real.
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
    color = Color(0xFF101923),
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF60A5FA)) else null,
    shadowElevation = 8.dp
  ) {
    Box(
      modifier = Modifier.size(48.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF2563EB) else Color(0xFF1A73E8),
        modifier = Modifier.size(28.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = if (isOpen) "Cerrar Géminis Web" else "Abrir Géminis Web Flotante",
            tint = Color.White,
            modifier = Modifier.size(17.dp)
          )
        }
      }
    }
  }
}

/**
 * Botón Flotante Inferior de Google Traductor Web.
 * Ubicado al lado de los 5 botones existentes. Abre de manera automática la bandeja
 * flotante in-app del 75% con Google Translate web.
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
    color = Color(0xFF101923),
    border = if (isOpen) BorderStroke(1.5.dp, Color(0xFF4285F4)) else null,
    shadowElevation = 8.dp
  ) {
    Box(
      modifier = Modifier.size(48.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOpen) Color(0xFF1967D2) else Color(0xFF1A73E8),
        modifier = Modifier.size(28.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = if (isOpen) "Cerrar Traductor Web" else "Abrir Google Traductor Web",
            tint = Color.White,
            modifier = Modifier.size(17.dp)
          )
        }
      }
    }
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

/**
 * Botón Flotante Inferior de Añadir Palabra Personalizada (+).
 * Ubicado en la esquina baja justo al lado del reductor de velocidad.
 * Estética HUD oscura (fondo #101923 sin cinta y cruz '+').
 */
@Composable
fun AddWordFloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    modifier = modifier.testTag("fab_add_custom_word"),
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
        imageVector = Icons.Default.Add,
        contentDescription = "Añadir palabra personalizada",
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
 * Diálogo interactivo con caja tipo HUD para añadir una palabra personalizada.
 * El rank siempre se asigna automáticamente (maxOf(2809, maxRank) + 1).
 * Guarda en custom_words.json en el almacenamiento local del teléfono sin alterar
 * el vocabulario nativo de 2,809 palabras.
 */
@Composable
fun AddCustomWordDialog(
  nextRank: Int,
  onDismiss: () -> Unit,
  onSaveWord: (word: String, category: String, spanish: String, example: String) -> Unit,
  onPreviewSpeak: (String) -> Unit
) {
  var wordInput by rememberSaveable { mutableStateOf("") }
  var spanishInput by rememberSaveable { mutableStateOf("") }
  var exampleInput by rememberSaveable { mutableStateOf("") }
  var selectedCategory by rememberSaveable { mutableStateOf("sustantivo") }

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
              imageVector = Icons.Default.Add,
              contentDescription = null,
              tint = syntaxColor,
              modifier = Modifier.size(24.dp)
            )
            Column {
              Text(
                text = "AÑADIR PALABRA (#$nextRank)",
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
            // Badge de Rank automático
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF0F172A),
              border = null,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "#$nextRank",
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
                  text = "0",
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
            text = "GUARDAR PALABRA #$nextRank",
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
    currentSpeed <= 0.55f -> -50f // Lo más bajo: punta a la izquierda
    currentSpeed <= 0.85f -> 0f   // En medio: aguja centrada verticalmente
    else -> 50f                   // Alta velocidad: punta hacia la derecha
  }

  val animatedAngle by animateFloatAsState(
    targetValue = targetAngle,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "speedometer_needle_angle"
  )

  Canvas(modifier = modifier.size(26.dp)) {
    val w = size.width
    val h = size.height
    // Centro del dial (ligeramente hacia abajo para balancear el arco superior)
    val centerDial = Offset(w / 2f, h * 0.62f)
    val radius = w * 0.44f

    // 1. Arco base del velocímetro (desde abajo a la izquierda hasta abajo a la derecha)
    drawArc(
      color = Color(0xFF00E5FF).copy(alpha = 0.45f),
      startAngle = 145f,
      sweepAngle = 250f,
      useCenter = false,
      topLeft = Offset(centerDial.x - radius, centerDial.y - radius),
      size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
      style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )

    // 2. Marcas o puntos guía en las 3 posiciones clave (Bajo: -50°, Medio: 0°, Alto: +50°)
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
        strokeWidth = if (isCurrent) 2.dp.toPx() else 1.2.dp.toPx(),
        cap = StrokeCap.Round
      )
    }

    // 3. Aguja indicadora ("punta indicadora / aguja")
    val needleRad = Math.toRadians((animatedAngle - 90f).toDouble())
    val needleLength = radius * 0.88f
    val needleTip = Offset(
      centerDial.x + needleLength * cos(needleRad).toFloat(),
      centerDial.y + needleLength * sin(needleRad).toFloat()
    )

    // Línea de la aguja
    drawLine(
      color = Color(0xFF00E5FF),
      start = centerDial,
      end = needleTip,
      strokeWidth = 2.5.dp.toPx(),
      cap = StrokeCap.Round
    )

    // Punta brillante en el extremo de la aguja
    drawCircle(
      color = Color.White,
      radius = 1.6.dp.toPx(),
      center = needleTip
    )

    // 4. Pivote central
    drawCircle(
      color = Color(0xFF00E5FF),
      radius = 3.2.dp.toPx(),
      center = centerDial
    )
    drawCircle(
      color = Color(0xFF101923),
      radius = 1.4.dp.toPx(),
      center = centerDial
    )
  }
}

/**
 * Botón Flotante Inferior de Velocidad TTS Global.
 * Muestra el velocímetro con aguja interactiva en tiempo real sin números en pantalla.
 * Al tocarlo, cicla inmediatamente: 1.0 -> 0.8 -> 0.5 -> 1.0.
 */
@Composable
fun SpeedFloatingActionButton(
  currentSpeed: Float,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val speedDesc = when {
    currentSpeed <= 0.55f -> "Velocidad baja: 0.5x"
    currentSpeed <= 0.85f -> "Velocidad media: 0.8x"
    else -> "Velocidad normal: 1.0x"
  }

  Surface(
    onClick = onClick,
    modifier = modifier.testTag("fab_speed_control"),
    shape = CircleShape,
    color = Color(0xFF101923),
    border = null,
    shadowElevation = 8.dp
  ) {
    Box(
      modifier = Modifier.size(48.dp),
      contentAlignment = Alignment.Center
    ) {
      AnimatedSpeedometerIcon(
        currentSpeed = currentSpeed
      )
    }
  }
}

/**

 * Diálogo Táctico: Mapa de Ruta de Dominio del Idioma.
 * Divide con claridad quirúrgica:
 * - Sección A [ 85% ]: Lo que cubre el Laboratorio (Fuerza Interna).
 * - Sección B [ 15% ]: Lo que debes cazar afuera en la calle (Inmersión Real).
 */
@Composable
fun TacticalRoadmapDialog(
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFF0F172A),
      border = BorderStroke(1.2.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 16.dp)
        .testTag("tactical_roadmap_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Cabecera del Mapa Táctico
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
              imageVector = Icons.Default.Explore,
              contentDescription = null,
              tint = Color(0xFF38BDF8),
              modifier = Modifier.size(24.dp)
            )
            Column {
              Text(
                text = "MAPA TÁCTICO: RUTA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFF8FAFC),
                fontSize = 17.sp
              )
              Text(
                text = "Estrategia de dominio y fluidez en inglés real",
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
              .testTag("btn_close_roadmap_x")
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

        // SECCIÓN A: LO QUE CUBRE ESTE LABORATORIO (FUERZA INTERNA + YOUTUBE INTEGRADO)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF1E293B).copy(alpha = 0.75f),
          border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.45f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF34D399))
              ) {
                Text(
                  text = "95%",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFF34D399),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 10.sp
                )
              }
              Text(
                text = "LABORATORIO (FUERZA INTERNA)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF34D399),
                fontSize = 11.sp
              )
            }

            RoadmapLabPillarItem(
              emoji = "🟢",
              title = "Vocabulario Nuclear (92% cobertura)",
              description = "2,807 palabras clave (NGSL) ordenadas por frecuencia e impacto real."
            )
            RoadmapLabPillarItem(
              emoji = "🟡",
              title = "Sintaxis Posicional y Conectores",
              description = "Estructuras directas sin traducir mentalmente palabra por palabra."
            )
            RoadmapLabPillarItem(
              emoji = "🔵",
              title = "Fonética Limpia (TTS)",
              description = "Pronunciación nativa de cada palabra y frases armadas."
            )
            RoadmapLabPillarItem(
              emoji = "🎬",
              title = "Habla Pegada (Connected Speech)",
              description = "Atajos de YouTube para entrenar reducciones y enlaces nativos reales."
            )
            RoadmapLabPillarItem(
              emoji = "🔥",
              title = "Jerga Callejera (Slang & Phrasal Verbs)",
              description = "Búsqueda directa de expresiones cotidianas y phrasal verbs en contexto."
            )
            RoadmapLabPillarItem(
              emoji = "🌐",
              title = "Práctica Externa (Google Translate)",
              description = "Práctica y contraste instantáneo de tus frases armadas."
            )
          }
        }

        // SECCIÓN B: LO QUE DEBES CAZAR AFUERA (REFLEJO EN VIVO)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF1E293B).copy(alpha = 0.75f),
          border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF78350F),
                border = BorderStroke(1.dp, Color(0xFFFBBF24))
              ) {
                Text(
                  text = "5%",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFFFBBF24),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 10.sp
                )
              }
              Text(
                text = "REFLEJO EN VIVO (MUNDO REAL)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFBBF24),
                fontSize = 11.sp
              )
            }

            Text(
              text = "Lo que entrenas en la vida real:",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )

            // Misión Externa Única
            RoadmapExternalMissionCard(
              icon = "⚡",
              title = "Velocidad sin Borrador",
              whatIs = "Responder en menos de 2 segundos sin preparar borradores.",
              action = "Narra tu día en voz alta usando los conectores del laboratorio."
            )
          }
        }

        // Botón Entendido
        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_close_roadmap"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF38BDF8)
          )
        ) {
          Text(
            text = "Entendido",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
        }
      }
    }
  }
}

@Composable
private fun RoadmapLabPillarItem(
  emoji: String,
  title: String,
  description: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = emoji,
      fontSize = 15.sp,
      modifier = Modifier.padding(top = 1.dp)
    )
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFF1F5F9),
        fontSize = 13.sp
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8),
        fontSize = 11.sp,
        lineHeight = 15.sp
      )
    }
  }
}

@Composable
private fun RoadmapExternalMissionCard(
  icon: String,
  title: String,
  whatIs: String,
  action: String
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFF0F172A).copy(alpha = 0.8f),
    border = BorderStroke(1.dp, Color(0xFF334155)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(text = icon, fontSize = 13.sp)
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF8FAFC),
          fontSize = 12.sp
        )
      }
      Text(
        text = "• Clave: $whatIs",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8),
        fontSize = 11.sp,
        lineHeight = 14.sp
      )
      Text(
        text = "🎯 Acción: $action",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF38BDF8),
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp
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
      horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
      CompassStepChip(
        color = Color(0xFFFACC15),
        label = "Conector",
        isSelected = isConnectorSelected,
        onClick = { onSelectFilter(SyntaxFilter.CONNECTOR) },
        onLongClick = { onExplainFilter(SyntaxFilter.CONNECTOR) },
        testTag = "compass_chip_Conector"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
      CompassStepChip(
        color = Color(0xFFA855F7),
        label = "Sujeto",
        isSelected = isPronounSelected,
        onClick = { onSelectFilter(SyntaxFilter.SUBJECT) },
        onLongClick = { onExplainFilter(SyntaxFilter.SUBJECT) },
        testTag = "compass_chip_Sujeto"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
      CompassStepChip(
        color = Color(0xFF38BDF8),
        label = "Verbo",
        isSelected = isVerbSelected,
        onClick = { onSelectFilter(SyntaxFilter.VERB) },
        onLongClick = { onExplainFilter(SyntaxFilter.VERB) },
        testTag = "compass_chip_Verbo"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
      CompassStepChip(
        color = Color(0xFF34D399),
        label = "Sustantivo",
        isSelected = isNounSelected,
        onClick = { onSelectFilter(SyntaxFilter.NOUN) },
        onLongClick = { onExplainFilter(SyntaxFilter.NOUN) },
        testTag = "compass_chip_Sustantivo"
      )
      Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
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
      color = if (isSelected) color else color.copy(alpha = 0.9f),
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
      textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None,
      maxLines = 1,
      softWrap = false
    )
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
  isPhraseUpdated: Boolean,
  isTranslationTrayExpanded: Boolean,
  translationState: TranslationState,
  onToggleTranslationTray: () -> Unit,
  onTranslate: () -> Unit,
  onRemoveWord: (Int) -> Unit,
  onMoveLeft: (Int) -> Unit,
  onMoveRight: (Int) -> Unit,
  onPlayPhrase: () -> Unit,
  onClearDraft: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 2.dp)
      .testTag("draft_scratchpad"),
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFF1E293B).copy(alpha = 0.5f),
    border = null,
    shadowElevation = 0.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 4.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
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
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF38BDF8),
            fontSize = 10.sp
          )
          if (draftWords.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF0F172A),
              border = BorderStroke(1.dp, Color(0xFF475569))
            ) {
              Text(
                text = "${draftWords.size} piezas",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                fontSize = 9.sp
              )
            }

            if (isPhraseUpdated) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.7f))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(4.dp)
                      .background(Color(0xFF34D399), CircleShape)
                  )
                  Text(
                    text = "Actualizada",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6EE7B7),
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                }
              }
            }
          }
        }

        // Acciones rápidas (Traducir a Google Translate, Play y Limpiar)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // El botón de traducir SOLO existe si hay frase armada en el Lienzo de Pruebas
          if (draftWords.isNotEmpty()) {
            IconButton(
              onClick = onTranslate,
              modifier = Modifier
                .size(26.dp)
                .testTag("btn_translate_practice")
            ) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Traducir y practicar frase en Google Translate",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(18.dp)
              )
            }
          }

          // Animación pulsante para el botón Play cuando la frase fue actualizada/reordenada
          val infiniteTransition = rememberInfiniteTransition(label = "PlayGlowTransition")
          val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
              animation = tween(800, easing = FastOutSlowInEasing),
              repeatMode = RepeatMode.Reverse
            ),
            label = "GlowAlpha"
          )
          val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
              animation = tween(800, easing = FastOutSlowInEasing),
              repeatMode = RepeatMode.Reverse
            ),
            label = "PulseScale"
          )

          val shouldPulse = draftWords.isNotEmpty() && isPhraseUpdated

          Surface(
            shape = CircleShape,
            color = if (shouldPulse) Color(0xFF10B981).copy(alpha = glowAlpha * 0.28f) else Color.Transparent,
            border = if (shouldPulse) BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = glowAlpha)) else null,
            modifier = Modifier.graphicsLayer {
              if (shouldPulse) {
                scaleX = pulseScale
                scaleY = pulseScale
              }
            }
          ) {
            IconButton(
              onClick = onPlayPhrase,
              enabled = draftWords.isNotEmpty(),
              modifier = Modifier
                .size(26.dp)
                .testTag("btn_play_draft")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = if (isPhraseUpdated) "Escuchar frase actualizada en nueva posición" else "Escuchar frase completa",
                  tint = if (draftWords.isNotEmpty()) {
                    if (isPhraseUpdated) Color(0xFF22C55E) else Color(0xFF4ADE80)
                  } else Color(0xFF475569),
                  modifier = Modifier.size(19.dp)
                )
                if (shouldPulse) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.TopEnd)
                      .padding(1.dp)
                      .size(5.dp)
                      .background(Color(0xFF34D399), CircleShape)
                  )
                }
              }
            }
          }

          IconButton(
            onClick = onClearDraft,
            enabled = draftWords.isNotEmpty(),
            modifier = Modifier
              .size(26.dp)
              .testTag("btn_clear_draft")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Limpiar borrador",
              tint = if (draftWords.isNotEmpty()) Color(0xFFF87171) else Color(0xFF475569),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Fila desplazable de bloques (Chips de frase armada) con botón de flecha hacia abajo a la izquierda
      if (draftWords.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            text = "Toca palabras abajo: sonarán y subirán aquí para armar tu frase.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8),
            fontStyle = FontStyle.Italic,
            fontSize = 11.sp
          )
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Botón con la flecha mirando hacia abajo al principio de la fila de bloques, a la izquierda de la primera palabra
          val arrowRotation by animateFloatAsState(
            targetValue = if (isTranslationTrayExpanded) 180f else 0f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
            label = "ArrowRotation"
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isTranslationTrayExpanded) Color(0xFF38BDF8).copy(alpha = 0.22f) else Color(0xFF0F172A),
            border = BorderStroke(
              1.dp,
              if (isTranslationTrayExpanded) Color(0xFF38BDF8) else Color(0xFF475569)
            ),
            modifier = Modifier
              .size(34.dp)
              .clickable(onClick = onToggleTranslationTray)
              .testTag("btn_toggle_translation_tray")
          ) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isTranslationTrayExpanded) "Ocultar traducción" else "Traducir frase con ML Kit",
                tint = if (isTranslationTrayExpanded) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                modifier = Modifier
                  .size(22.dp)
                  .graphicsLayer { rotationZ = arrowRotation }
              )
            }
          }

          LazyRow(
            modifier = Modifier
              .weight(1f)
              .testTag("draft_chips_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(vertical = 4.dp)
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
                onClickRemove = { onRemoveWord(index) }
              )
            }
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

        if (draftWords.size > 1) {
          Text(
            text = "💡 Toca ◀ ▶ o mantén presionado para desplazar bloques en el lienzo",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B),
            fontSize = 10.sp
          )
        }
      }
    }
  }
}


/**
 * Chip interactivo del borrador con soporte para desplazamiento posicional y arrastre.
 */
@Composable
private fun DraftChip(
  word: Word,
  index: Int,
  totalCount: Int,
  onMoveLeft: () -> Unit,
  onMoveRight: () -> Unit,
  onClickRemove: () -> Unit
) {
  val chipColor = getWordSyntaxColor(word)
  var accumulatedDrag by remember { mutableStateOf(0f) }
  var isDragging by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = chipColor.copy(alpha = if (isDragging) 0.38f else 0.2f),
    border = BorderStroke(if (isDragging) 2.dp else 1.dp, chipColor),
    modifier = Modifier
      .testTag("chip_word_${word.lemma}_$index")
      .graphicsLayer {
        translationX = accumulatedDrag
        if (isDragging) {
          scaleX = 1.08f
          scaleY = 1.08f
          shadowElevation = 8f
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
            if (currentDrag > 90.0 && index < totalCount - 1) {
              onMoveRight()
              accumulatedDrag = 0f
            } else if (currentDrag < -90.0 && index > 0) {
              onMoveLeft()
              accumulatedDrag = 0f
            }
          }
        )
      }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      // Desplazar a la izquierda
      if (totalCount > 1 && index > 0) {
        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .clickable(onClick = onMoveLeft)
            .testTag("btn_move_left_$index"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Mover ${word.lemma} a la izquierda",
            tint = chipColor.copy(alpha = 0.9f),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Text(
        text = word.lemma,
        color = chipColor,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 2.dp)
      )

      // Desplazar a la derecha
      if (totalCount > 1 && index < totalCount - 1) {
        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .clickable(onClick = onMoveRight)
            .testTag("btn_move_right_$index"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Mover ${word.lemma} a la derecha",
            tint = chipColor.copy(alpha = 0.9f),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Eliminar palabra del borrador
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
          tint = chipColor.copy(alpha = 0.8f),
          modifier = Modifier.size(13.dp)
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
@Composable
fun WordHudCard(
  word: Word,
  onTap: () -> Unit,
  onSpeak: () -> Unit = {},
  onSpeakExample: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var isRevealed by rememberSaveable(word.id) { mutableStateOf(false) }
  val wordColor = getWordSyntaxColor(word)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("word_item_${word.rank}")
      .pointerInput(word.id, word.lemma) {
        detectTapGestures(
          onTap = { onTap() },
          onLongPress = { isRevealed = !isRevealed }
        )
      }
      .padding(vertical = 3.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Número de rank flotando sin caja
      Text(
        text = "#${word.rank}",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B),
        fontSize = 11.sp,
        modifier = Modifier.width(36.dp)
      )

      Spacer(modifier = Modifier.width(8.dp))

      // Palabra, contador crudo y categoría flotando sin caja
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = word.lemma,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            ),
            color = wordColor
          )
          // Contador crudo de uso: sin fondo, sin bordes, sin cajas
          Text(
            text = "${word.useCount}",
            fontSize = 12.sp,
            fontWeight = if (word.useCount > 0) FontWeight.Bold else FontWeight.Normal,
            color = if (word.useCount > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
            modifier = Modifier.testTag("word_use_count_${word.rank}")
          )
        }
        Text(
          text = word.displayCategoryLabel,
          style = MaterialTheme.typography.labelSmall,
          color = wordColor.copy(alpha = 0.85f),
          fontSize = 10.sp
        )
      }

      // Icono de audio flotando sin caja
      IconButton(
        onClick = onSpeak,
        modifier = Modifier
          .size(28.dp)
          .testTag("btn_speak_word_${word.rank}")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = "Pronunciar ${word.lemma}",
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(17.dp)
        )
      }
    }

    // Panel de revelación Active Recall (Long Press)
    AnimatedVisibility(
      visible = isRevealed,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 4.dp)
      ) {
        // 1. Traducción al español
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "ESPAÑOL:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            fontSize = 9.sp
          )
          Text(
            text = word.displaySpanishTranslation,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF8FAFC),
            fontSize = 12.sp
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 2. Micro-ejemplo interactivo invisible sin saturar la UI (pronuncia al tocar)
        val cleanExample = word.microExample.replace("\"", "").trim()
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .clickable {
              if (cleanExample.isNotBlank()) {
                onSpeakExample?.invoke(cleanExample)
              }
            }
            .padding(vertical = 1.dp)
            .testTag("example_row_${word.rank}")
        ) {
          Text(
            text = "EJEMPLO:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            fontSize = 9.sp
          )
          Text(
            text = "\"$cleanExample\"",
            style = MaterialTheme.typography.bodySmall.copy(
              fontStyle = FontStyle.Italic,
              textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
            ),
            color = Color(0xFF38BDF8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Simple y fina línea divisoria entre palabras
    HorizontalDivider(
      color = Color(0xFF334155).copy(alpha = 0.4f),
      thickness = 0.5.dp,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}


