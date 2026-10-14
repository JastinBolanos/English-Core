package com.example.presentation.vocabulary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.stt.SpeechRecognitionState
import com.example.domain.model.PartOfSpeech
import com.example.domain.model.Word
import com.example.domain.util.PronunciationEvaluation
import com.example.domain.util.SpokenWordMatch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
  viewModel: VocabularyViewModel,
  onBack: () -> Unit = {},
  onNavigateToAlarms: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val words by viewModel.words.collectAsStateWithLifecycle()
  val filteredWords by viewModel.filteredWords.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val draftWords by viewModel.draftWords.collectAsStateWithLifecycle()
  val selectedSyntaxFilter by viewModel.selectedSyntaxFilter.collectAsStateWithLifecycle()
  val isListening by viewModel.isListening.collectAsStateWithLifecycle()
  val speechState by viewModel.speechState.collectAsStateWithLifecycle()
  val partialSpokenText by viewModel.partialSpokenText.collectAsStateWithLifecycle()
  val pronunciationEvaluation by viewModel.pronunciationEvaluation.collectAsStateWithLifecycle()
  val isMirrorTrayVisible by viewModel.isMirrorTrayVisible.collectAsStateWithLifecycle()
  val currentSpeechSpeed by viewModel.currentSpeechSpeed.collectAsStateWithLifecycle()

  var showRoadmapDialog by rememberSaveable { mutableStateOf(false) }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  var jumpRankInput by rememberSaveable { mutableStateOf("") }

  fun performJumpToRank(input: String) {
    val targetRank = input.trim().toIntOrNull() ?: return
    if (words.isEmpty()) return

    // Si había búsqueda de texto o filtro de categoría activo, limpiarlo para tener la lista de 2,809 palabras completa visible
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
      listState.scrollToItem(targetIndex.coerceIn(0, maxOf(0, words.size - 1)))
    }
  }

  val context = LocalContext.current
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.startSpeechRecognition()
    }
  }

  val onToggleMic: () -> Unit = {
    val permissionStatus = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    )
    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
      viewModel.toggleSpeechRecognition()
    } else {
      permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("screen_vocabulario"),
    floatingActionButton = {
      SpeedFloatingActionButton(
        currentSpeed = currentSpeechSpeed,
        onClick = { viewModel.onToggleSpeed() }
      )
    },
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "HUD Laboratorio",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            if (words.isNotEmpty()) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Text(
                  text = "${filteredWords.size} palabras",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        },
        actions = {
          // Píldora táctica discreta '🗺️ Ruta' entre el contador y la alarma
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.55f)),
            modifier = Modifier
              .clickable { showRoadmapDialog = true }
              .testTag("btn_roadmap_shortcut")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Ver Mapa de Ruta Táctica",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Ruta",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFE0F2FE),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }

          if (onNavigateToAlarms != null) {
            IconButton(
              onClick = onNavigateToAlarms,
              modifier = Modifier.testTag("btn_alarm_shortcut")
            ) {
              Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = stringResource(id = R.string.btn_alarmas),
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. EL GUIADOR SUPERIOR (La Brújula de Orden y Color - Interactivo con selección y botón X)
      SyntaxCompassHeader(
        selectedFilter = selectedSyntaxFilter,
        onSelectFilter = { filter -> viewModel.selectSyntaxFilter(filter) },
        onClearFilter = { viewModel.clearSyntaxFilter() }
      )

      // 2. LA BARRA DE BORRADOR ("Scratchpad" de Frases) con Bandeja Espejo de Voz y Predictor
      DraftScratchpadHeader(
        draftWords = draftWords,
        isListening = isListening,
        speechState = speechState,
        partialSpokenText = partialSpokenText,
        pronunciationEvaluation = pronunciationEvaluation,
        isMirrorTrayVisible = isMirrorTrayVisible,
        onToggleMic = onToggleMic,
        onCloseMirrorTray = { viewModel.closeMirrorTray() },
        onRemoveWord = { index -> viewModel.removeWordFromDraft(index) },
        onPlayPhrase = { viewModel.playFullDraftPhrase() },
        onClearDraft = { viewModel.clearDraft() }
      )

      // Fila de Buscadores: Búsqueda por texto (izquierda) + Mini-Buscador de Salto por número (# 1-2809) (derecha)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Buscador de texto tradicional (Filtro)
        OutlinedTextField(
          value = searchQuery,
          onValueChange = {
            if (it.isNotEmpty() && jumpRankInput.isNotEmpty()) {
              jumpRankInput = ""
            }
            viewModel.updateSearchQuery(it)
          },
          placeholder = null,
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = Color(0xFF94A3B8)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Limpiar búsqueda",
                  tint = Color(0xFF94A3B8)
                )
              }
            }
          },
          shape = RoundedCornerShape(14.dp),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("search_word_input")
        )

        // Mini-Buscador de Salto Numérico (#) (Teletransporte sin filtrar)
        OutlinedTextField(
          value = jumpRankInput,
          onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }.take(4)
            jumpRankInput = digits
            if (digits.isNotEmpty()) {
              performJumpToRank(digits)
            }
          },
          placeholder = {
            Text(
              text = "# 1-2809",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF64748B),
              fontSize = 11.sp
            )
          },
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Go
          ),
          keyboardActions = KeyboardActions(
            onGo = { performJumpToRank(jumpRankInput) }
          ),
          shape = RoundedCornerShape(14.dp),
          singleLine = true,
          textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Color(0xFF38BDF8),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          ),
          modifier = Modifier
            .width(98.dp)
            .testTag("jump_rank_input")
        )
      }

      when {
        isLoading -> {
          Box(
            modifier = Modifier.fillMaxSize(),
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
        filteredWords.isEmpty() -> {
          Box(
            modifier = Modifier
              .fillMaxSize()
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
        else -> {
          // 3. LISTA DE LAS 2,809 PALABRAS (Orden de combate + Imán de conexión con salto instantáneo)
          LazyColumn(
            state = listState,
            modifier = Modifier
              .fillMaxSize()
              .testTag("vocabulary_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            itemsIndexed(
              items = filteredWords,
              key = { _, item -> item.id }
            ) { _, word ->
              WordHudCard(
                word = word,
                onTap = { viewModel.addWordToDraft(word) },
                onSpeakExample = { example -> viewModel.speakWord(example) }
              )
            }
          }
        }
      }
    }
  }

  if (showRoadmapDialog) {
    TacticalRoadmapDialog(onDismiss = { showRoadmapDialog = false })
  }
}

/**
 * Botón Flotante Inferior de Velocidad TTS Global.
 * Muestra el ícono de velocímetro (Speed) y el nivel actual ('1.0x', '0.8x' o '0.5x').
 * Estética HUD oscura (fondo #121824 con borde cian / menta brillante).
 * Al tocarlo, cicla inmediatamente: 1.0x -> 0.8x -> 0.5x -> 1.0x.
 */
@Composable
fun SpeedFloatingActionButton(
  currentSpeed: Float,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val speedLabel = when {
    currentSpeed <= 0.55f -> "0.5x"
    currentSpeed <= 0.85f -> "0.8x"
    else -> "1.0x"
  }

  Surface(
    onClick = onClick,
    modifier = modifier
      .testTag("fab_speed_control"),
    shape = RoundedCornerShape(20.dp),
    color = Color(0xFF101923),
    border = BorderStroke(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.8f)),
    shadowElevation = 8.dp
  ) {
    Row(
      modifier = Modifier
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Speed,
        contentDescription = "Velocidad de reproducción",
        tint = Color(0xFF00E5FF),
        modifier = Modifier.size(20.dp)
      )
      Text(
        text = speedLabel,
        color = Color(0xFFE2E8F0),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
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

        // SECCIÓN A: LO QUE CUBRE ESTE LABORATORIO (85% DEL IDIOMA)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF1E293B).copy(alpha = 0.75f),
          border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.45f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                  text = "85%",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFF34D399),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 10.sp
                )
              }
              Text(
                text = "LO QUE CUBRE ESTE LABORATORIO (FUERZA INTERNA)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF34D399),
                fontSize = 11.sp
              )
            }

            // 4 Nodos de progreso conectados
            RoadmapLabPillarItem(
              emoji = "🟢",
              title = "Vocabulario Nuclear (92% cobertura)",
              description = "Las 2,807 palabras más usadas del inglés (NGSL) ordenadas estrictamente por frecuencia e impacto real."
            )
            RoadmapLabPillarItem(
              emoji = "🟡",
              title = "Sintaxis Posicional y Conectores",
              description = "Pensar en bloques directos (Conector ➔ Sujeto ➔ Verbo ➔ Objeto) sin traducir mentalmente palabra por palabra."
            )
            RoadmapLabPillarItem(
              emoji = "🔵",
              title = "Fonética Limpia (TTS)",
              description = "Memoria auditiva de alta fidelidad: pronunciación nativa palabra por palabra y lectura corrida de frases armadas."
            )
            RoadmapLabPillarItem(
              emoji = "🎙️",
              title = "Juez de Pronunciación (Micrófono)",
              description = "Entrenamiento físico muscular de lengua y mandíbula con la Bandeja Espejo en el Lienzo de Pruebas."
            )
          }
        }

        // SECCIÓN B: LO QUE DEBES CAZAR AFUERA (15% CALLE Y FLUIDEZ REAL)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF1E293B).copy(alpha = 0.75f),
          border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                  text = "15%",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFFFBBF24),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 10.sp
                )
              }
              Text(
                text = "LO QUE DEBES CAZAR AFUERA (CALLE Y FLUIDEZ REAL)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFBBF24),
                fontSize = 11.sp
              )
            }

            Text(
              text = "Las 3 únicas cosas que ninguna app puede darte y que debes entrenar en el mundo real:",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )

            // 3 Misiones Externas
            RoadmapExternalMissionCard(
              icon = "🎬",
              title = "Habla Pegada (Connected Speech)",
              whatIs = "Los nativos ligan y reducen palabras al hablar rápido (gonna, wanna, gotta, didja, whaddaya).",
              action = "Ver películas, series o YouTube con audio y subtítulos en inglés (nunca en español) para sincronizar ojo y oído."
            )
            RoadmapExternalMissionCard(
              icon = "🔥",
              title = "Jerga Callejera (Slang & Phrasal Verbs)",
              whatIs = "Combinaciones cotidianas que cambian de significado según el contexto (figure out, hang out, screw up, my bad).",
              action = "Escuchar podcasts conversacionales o streams informales donde hablen sin guion predeterminado."
            )
            RoadmapExternalMissionCard(
              icon = "⚡",
              title = "Velocidad sin Borrador (Reflejo en Vivo)",
              whatIs = "Responder en menos de 2 segundos sin botón de borrador cuando alguien te cambia de tema en una llamada o charla.",
              action = "Narrar lo que haces en tu día en voz alta y a solas, uniendo ideas con tus conectores del laboratorio."
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
        text = "• Qué es: $whatIs",
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
  onClearFilter: () -> Unit
) {
  val isConnectorSelected = selectedFilter == SyntaxFilter.CONNECTOR
  val isPronounSelected = selectedFilter == SyntaxFilter.SUBJECT
  val isVerbSelected = selectedFilter == SyntaxFilter.VERB
  val isNounSelected = selectedFilter == SyntaxFilter.NOUN
  val isModifierSelected = selectedFilter == SyntaxFilter.MODIFIER

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF0F172A),
    border = BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 10.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Fila deslizable horizontalmente para los 5 botones sin romper la pantalla
      Row(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CompassStepChip(
          color = Color(0xFFFACC15),
          label = "🟡 Conector",
          isSelected = isConnectorSelected,
          onClick = { onSelectFilter(SyntaxFilter.CONNECTOR) },
          testTag = "compass_chip_Conector"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
        CompassStepChip(
          color = Color(0xFFA855F7),
          label = "🟣 Sujeto",
          isSelected = isPronounSelected,
          onClick = { onSelectFilter(SyntaxFilter.SUBJECT) },
          testTag = "compass_chip_Sujeto"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
        CompassStepChip(
          color = Color(0xFF3B82F6),
          label = "🔵 Verbo",
          isSelected = isVerbSelected,
          onClick = { onSelectFilter(SyntaxFilter.VERB) },
          testTag = "compass_chip_Verbo"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
        CompassStepChip(
          color = Color(0xFF34D399),
          label = "🟢 Sustantivo",
          isSelected = isNounSelected,
          onClick = { onSelectFilter(SyntaxFilter.NOUN) },
          testTag = "compass_chip_Sustantivo"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
        CompassStepChip(
          color = Color(0xFFF8FAFC),
          label = "⚪ Modificador",
          isSelected = isModifierSelected,
          onClick = { onSelectFilter(SyntaxFilter.MODIFIER) },
          testTag = "compass_chip_Modificador"
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Separador vertical sutil entre los chips y la X fija
      VerticalDivider(
        modifier = Modifier.height(22.dp),
        thickness = 1.dp,
        color = Color(0xFF334155)
      )

      Spacer(modifier = Modifier.width(6.dp))

      // Botón X fijo al costado para deseleccionar todo y dejar los 2809 libres
      Surface(
        shape = CircleShape,
        color = if (selectedFilter != null) Color(0xFFEF4444).copy(alpha = 0.25f) else Color(0xFF1E293B),
        border = BorderStroke(
          width = if (selectedFilter != null) 1.2.dp else 1.dp,
          color = if (selectedFilter != null) Color(0xFFEF4444) else Color(0xFF334155)
        ),
        modifier = Modifier
          .size(28.dp)
          .clickable(onClick = onClearFilter)
          .testTag("btn_clear_compass_selection")
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Deseleccionar todo y ver las 2809 palabras",
            tint = if (selectedFilter != null) Color(0xFFFCA5A5) else Color(0xFF94A3B8),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun CompassStepChip(
  color: Color,
  label: String,
  isSelected: Boolean = false,
  onClick: () -> Unit = {},
  testTag: String = "compass_chip_${label.filter { it.isLetter() }}"
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) color.copy(alpha = 0.35f) else color.copy(alpha = 0.12f),
    border = BorderStroke(
      width = if (isSelected) 1.8.dp else 1.dp,
      color = if (isSelected) color else color.copy(alpha = 0.45f)
    ),
    modifier = Modifier
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Text(
      text = label,
      color = if (isSelected) Color.White else color,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
      maxLines = 1,
      softWrap = false,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
    )
  }
}

/**
 * 2. LA BARRA DE BORRADOR ("Scratchpad" de Frases)
 * Contenedor interactivo fijo con bloques de color, controles de audio, evaluador de pronunciación
 * ("Bandeja Espejo") y predictor en vivo.
 *
 * Flujo Invisible:
 * - Sin frase: Pantalla limpia sin micrófono ni bandeja espejo.
 * - Con frase: Aparece el micrófono (🎤).
 * - Tocar 🎤: Se despliega la Bandeja Espejo directamente acoplada y evalúa palabra por palabra
 *   (verde menta = acierto, rojo coral = diferencia).
 * - Papelera (🗑️): Borra la frase y cierra la bandeja de inmediato con cero fricción.
 */
@Composable
private fun DraftScratchpadHeader(
  draftWords: List<Word>,
  isListening: Boolean,
  speechState: SpeechRecognitionState,
  partialSpokenText: String,
  pronunciationEvaluation: PronunciationEvaluation?,
  isMirrorTrayVisible: Boolean,
  onToggleMic: () -> Unit,
  onCloseMirrorTray: () -> Unit,
  onRemoveWord: (Int) -> Unit,
  onPlayPhrase: () -> Unit,
  onClearDraft: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .testTag("draft_scratchpad"),
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF1E293B),
    border = BorderStroke(1.dp, if (draftWords.isNotEmpty()) Color(0xFF38BDF8) else Color(0xFF334155)),
    shadowElevation = 3.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
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
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "LIENZO DE PRUEBAS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF38BDF8),
            fontSize = 11.sp
          )
          if (draftWords.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF0F172A),
              border = BorderStroke(1.dp, Color(0xFF475569))
            ) {
              Text(
                text = "${draftWords.size} piezas",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp
              )
            }
          }
        }

        // Acciones rápidas (Micrófono opcional, Play y Limpiar)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // El micrófono SOLO existe si hay frase armada en el Lienzo de Pruebas
          if (draftWords.isNotEmpty()) {
            IconButton(
              onClick = onToggleMic,
              modifier = Modifier
                .size(32.dp)
                .testTag("btn_mic_pronunciation")
            ) {
              Icon(
                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isListening) "Detener evaluador de pronunciación" else "Evaluar pronunciación con voz",
                tint = if (isListening) Color(0xFFEF4444) else Color(0xFF38BDF8),
                modifier = Modifier.size(20.dp)
              )
            }
          }

          IconButton(
            onClick = onPlayPhrase,
            enabled = draftWords.isNotEmpty(),
            modifier = Modifier
              .size(32.dp)
              .testTag("btn_play_draft")
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Escuchar frase completa",
              tint = if (draftWords.isNotEmpty()) Color(0xFF4ADE80) else Color(0xFF475569),
              modifier = Modifier.size(22.dp)
            )
          }

          IconButton(
            onClick = onClearDraft,
            enabled = draftWords.isNotEmpty(),
            modifier = Modifier
              .size(32.dp)
              .testTag("btn_clear_draft")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Limpiar borrador",
              tint = if (draftWords.isNotEmpty()) Color(0xFFF87171) else Color(0xFF475569),
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      // Fila desplazable de bloques (Chips de frase armada)
      if (draftWords.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            text = "Toca palabras abajo: sonarán y subirán aquí para armar tu frase.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8),
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp
          )
        }
      } else {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("draft_chips_row"),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          itemsIndexed(
            items = draftWords,
            key = { index, word -> "${index}_${word.id}_${word.lemma}" }
          ) { index, word ->
            DraftChip(
              word = word,
              onClickRemove = { onRemoveWord(index) }
            )
          }
        }
      }

      // BANDEJA ESPEJO DE VOZ (Acoplada directamente al Lienzo de Pruebas como segundo piso)
      AnimatedVisibility(
        visible = draftWords.isNotEmpty() && (isMirrorTrayVisible || isListening || pronunciationEvaluation != null),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        MirrorVoiceTray(
          isListening = isListening,
          speechState = speechState,
          partialSpokenText = partialSpokenText,
          evaluation = pronunciationEvaluation,
          onToggleMic = onToggleMic,
          onClose = onCloseMirrorTray
        )
      }
    }
  }
}

/**
 * Sub-bandeja espejo acoplada dentro del Lienzo de Pruebas para evaluar la voz sin popups externos.
 */
@Composable
private fun MirrorVoiceTray(
  isListening: Boolean,
  speechState: SpeechRecognitionState,
  partialSpokenText: String,
  evaluation: PronunciationEvaluation?,
  onToggleMic: () -> Unit,
  onClose: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFF0F172A),
    border = BorderStroke(
      width = 1.dp,
      color = when {
        isListening -> Color(0xFF38BDF8)
        evaluation?.isExactMatch == true -> Color(0xFF34D399)
        evaluation != null -> Color(0xFFF87171)
        else -> Color(0xFF334155)
      }
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("mirror_voice_tray")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Barra de estado de la Bandeja Espejo
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
            contentDescription = null,
            tint = if (isListening) Color(0xFF38BDF8) else Color(0xFF94A3B8),
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "BANDEJA ESPEJO",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = if (isListening) Color(0xFF38BDF8) else Color(0xFF94A3B8),
            fontSize = 10.sp
          )
        }

        if (evaluation != null) {
          Text(
            text = evaluation.feedbackMessage,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = if (evaluation.isExactMatch) Color(0xFF34D399) else Color(0xFFF87171),
            modifier = Modifier.testTag("evaluation_feedback_text")
          )
        } else if (isListening) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .background(Color(0xFF38BDF8), CircleShape)
            )
            Text(
              text = "Escuchando en inglés...",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF38BDF8),
              fontSize = 10.sp
            )
          }
        } else {
          IconButton(
            onClick = onClose,
            modifier = Modifier.size(20.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Cerrar bandeja espejo",
              tint = Color(0xFF64748B),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Contenido: Escuchando, Resultado o Error
      when {
        isListening -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Text(
              text = if (partialSpokenText.isNotBlank()) "🎙️ \"$partialSpokenText...\"" else "🎙️ Escuchando tu pronunciación...",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFE2E8F0),
              fontStyle = FontStyle.Italic,
              fontSize = 12.sp
            )
          }
        }
        evaluation != null -> {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            LazyRow(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("spoken_words_row"),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              itemsIndexed(
                items = evaluation.wordMatches,
                key = { idx, item -> "${idx}_${item.word}" }
              ) { _, match ->
                MirrorWordChip(match = match)
              }
            }

            if (evaluation.missingWordsCount > 0) {
              Text(
                text = "Faltaron ${evaluation.missingWordsCount} palabra(s) por pronunciar.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
              )
            }
          }
        }
        speechState is SpeechRecognitionState.Error -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "⚠️ ${speechState.message}",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFF87171),
              fontSize = 11.sp,
              modifier = Modifier.weight(1f)
            )
            Text(
              text = "Reintentar 🎤",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF38BDF8),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable(onClick = onToggleMic)
                .padding(4.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Chip para cada palabra transcrita por el micrófono:
 * 🟢 Verde menta: Coincidió exactamente con el bloque original.
 * 🔴 Rojo coral: El teléfono entendió otra cosa.
 */
@Composable
private fun MirrorWordChip(match: SpokenWordMatch) {
  val isMatch = match.isMatch
  val accentColor = if (isMatch) Color(0xFF34D399) else Color(0xFFF87171)
  val bgColor = if (isMatch) Color(0xFF064E3B).copy(alpha = 0.55f) else Color(0xFF7F1D1D).copy(alpha = 0.55f)

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = bgColor,
    border = BorderStroke(1.dp, accentColor),
    modifier = Modifier.testTag("spoken_word_${match.word}")
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = match.word,
        color = accentColor,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      if (!isMatch && match.expectedWord != null) {
        Text(
          text = "esperado: ${match.expectedWord}",
          color = Color(0xFFFCA5A5),
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

/**
 * Chip interactivo del borrador.
 */
@Composable
private fun DraftChip(
  word: Word,
  onClickRemove: () -> Unit
) {
  val chipColor = getWordSyntaxColor(word.partOfSpeech)

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = chipColor.copy(alpha = 0.2f),
    border = BorderStroke(1.dp, chipColor),
    modifier = Modifier.clickable(onClick = onClickRemove)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Text(
        text = word.lemma,
        color = chipColor,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Eliminar ${word.lemma}",
        tint = chipColor.copy(alpha = 0.8f),
        modifier = Modifier.size(13.dp)
      )
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
  onSpeakExample: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var isRevealed by rememberSaveable(word.id) { mutableStateOf(false) }
  val wordColor = getWordSyntaxColor(word.partOfSpeech)
  val borderColor by animateColorAsState(
    targetValue = if (isRevealed) wordColor else Color(0xFF334155),
    label = "cardBorderColor"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("word_item_${word.rank}")
      .pointerInput(word.id, word.lemma) {
        detectTapGestures(
          onTap = { onTap() },
          onLongPress = { isRevealed = !isRevealed }
        )
      },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFF1E293B)
    ),
    border = BorderStroke(1.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Badge de Rank
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF0F172A),
          border = BorderStroke(1.dp, Color(0xFF334155)),
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "#${word.rank}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Palabra, contador crudo y categoría
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = word.lemma,
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              ),
              color = wordColor
            )
            // Contador crudo de uso: sin fondo, sin bordes, sin cajas
            Text(
              text = "${word.useCount}",
              fontSize = 13.sp,
              fontWeight = if (word.useCount > 0) FontWeight.Bold else FontWeight.Normal,
              color = if (word.useCount > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
              modifier = Modifier.testTag("word_use_count_${word.rank}")
            )
          }
          Text(
            text = word.displayCategoryLabel,
            style = MaterialTheme.typography.labelSmall,
            color = wordColor.copy(alpha = 0.85f),
            fontSize = 11.sp
          )
        }

        // Indicador de "Imán" (Acepta después...)
        MagnetIndicatorPill(nextCategories = word.nextExpectedCategories)

        Spacer(modifier = Modifier.width(8.dp))

        // Icono de audio (asistente visual)
        IconButton(
          onClick = onTap,
          modifier = Modifier.size(34.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Agregar y pronunciar ${word.lemma}",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(19.dp)
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
            .padding(top = 10.dp)
        ) {
          HorizontalDivider(
            color = Color(0xFF334155),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 8.dp)
          )

          // 1. Traducción al español
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "ESPAÑOL:",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8),
              fontSize = 10.sp
            )
            Text(
              text = word.displaySpanishTranslation,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFF8FAFC),
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // 2. Micro-ejemplo interactivo invisible sin saturar la UI (pronuncia al tocar)
          val cleanExample = word.microExample.replace("\"", "").trim()
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
              .clickable {
                if (cleanExample.isNotBlank()) {
                  onSpeakExample?.invoke(cleanExample)
                }
              }
              .padding(vertical = 2.dp)
              .testTag("example_row_${word.rank}")
          ) {
            Text(
              text = "EJEMPLO:",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8),
              fontSize = 10.sp
            )
            Text(
              text = "\"$cleanExample\"",
              style = MaterialTheme.typography.bodySmall.copy(
                fontStyle = FontStyle.Italic,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
              ),
              color = Color(0xFF38BDF8),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // 3. Regla de conexión explicada (Interceptada del catálogo)
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "REGLA:",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF38BDF8),
              fontSize = 10.sp,
              modifier = Modifier.padding(top = 1.dp)
            )
            Text(
              text = word.connectionRuleNote,
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MagnetIndicatorPill(nextCategories: List<PartOfSpeech>) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF0F172A),
    border = BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "➔",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B)
      )
      nextCategories.take(3).forEach { category ->
        Box(
          modifier = Modifier
            .size(8.dp)
            .background(getWordSyntaxColor(category), shape = CircleShape)
        )
      }
    }
  }
}
