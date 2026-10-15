package com.example.presentation.vocabulary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
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

@OptIn(ExperimentalMaterial3Api::class)
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

  val context = LocalContext.current
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
          // Píldora táctica discreta 'Ruta' alineada limpiamente a la derecha
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.55f)),
            modifier = Modifier
              .padding(end = 12.dp)
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
      // 2. LA BARRA DE BORRADOR ("Scratchpad" de Frases) con enlace directo a Google Translate
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
              emoji = "🌐",
              title = "Práctica Externa (Google Translate)",
              description = "Envío instantáneo de frases armadas a Google Translate con voz nativa, traducción y práctica avanzada."
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
      .padding(horizontal = 12.dp, vertical = 4.dp),
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF0F172A),
    border = BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Fila deslizable horizontalmente para los 5 botones con espaciado compacto
      Row(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        CompassStepChip(
          color = Color(0xFFFACC15),
          label = "🟡 Conector",
          isSelected = isConnectorSelected,
          onClick = { onSelectFilter(SyntaxFilter.CONNECTOR) },
          testTag = "compass_chip_Conector"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
        CompassStepChip(
          color = Color(0xFFA855F7),
          label = "🟣 Sujeto",
          isSelected = isPronounSelected,
          onClick = { onSelectFilter(SyntaxFilter.SUBJECT) },
          testTag = "compass_chip_Sujeto"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
        CompassStepChip(
          color = Color(0xFF3B82F6),
          label = "🔵 Verbo",
          isSelected = isVerbSelected,
          onClick = { onSelectFilter(SyntaxFilter.VERB) },
          testTag = "compass_chip_Verbo"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
        CompassStepChip(
          color = Color(0xFF34D399),
          label = "🟢 Sustantivo",
          isSelected = isNounSelected,
          onClick = { onSelectFilter(SyntaxFilter.NOUN) },
          testTag = "compass_chip_Sustantivo"
        )
        Text("➔", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
        CompassStepChip(
          color = Color(0xFFF8FAFC),
          label = "⚪ Modificador",
          isSelected = isModifierSelected,
          onClick = { onSelectFilter(SyntaxFilter.MODIFIER) },
          testTag = "compass_chip_Modificador"
        )
        Spacer(modifier = Modifier.width(8.dp))
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Separador vertical sutil entre los chips y la X fija
      VerticalDivider(
        modifier = Modifier.height(20.dp),
        thickness = 1.dp,
        color = Color(0xFF334155)
      )

      Spacer(modifier = Modifier.width(4.dp))

      // Botón X compacto y fijo al costado para deseleccionar todo y dejar los 2809 libres
      Surface(
        shape = CircleShape,
        color = if (selectedFilter != null) Color(0xFFEF4444).copy(alpha = 0.25f) else Color(0xFF1E293B),
        border = BorderStroke(
          width = if (selectedFilter != null) 1.2.dp else 1.dp,
          color = if (selectedFilter != null) Color(0xFFEF4444) else Color(0xFF334155)
        ),
        modifier = Modifier
          .size(26.dp)
          .clickable(onClick = onClearFilter)
          .testTag("btn_clear_compass_selection")
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Deseleccionar todo y ver las 2809 palabras",
            tint = if (selectedFilter != null) Color(0xFFFCA5A5) else Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
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
    shape = RoundedCornerShape(7.dp),
    color = if (isSelected) color.copy(alpha = 0.35f) else color.copy(alpha = 0.12f),
    border = BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) color else color.copy(alpha = 0.45f)
    ),
    modifier = Modifier
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Text(
      text = label,
      color = if (isSelected) Color.White else color,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
      maxLines = 1,
      softWrap = false,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
    )
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
          horizontalArrangement = Arrangement.spacedBy(6.dp)
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

            if (isPhraseUpdated) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.7f))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(5.dp)
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
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // El botón de traducir SOLO existe si hay frase armada en el Lienzo de Pruebas
          if (draftWords.isNotEmpty()) {
            IconButton(
              onClick = onTranslate,
              modifier = Modifier
                .size(32.dp)
                .testTag("btn_translate_practice")
            ) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Traducir y practicar frase en Google Translate",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(20.dp)
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
            border = if (shouldPulse) BorderStroke(1.5.dp, Color(0xFF34D399).copy(alpha = glowAlpha)) else null,
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
                .size(32.dp)
                .testTag("btn_play_draft")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = if (isPhraseUpdated) "Escuchar frase actualizada en nueva posición" else "Escuchar frase completa",
                  tint = if (draftWords.isNotEmpty()) {
                    if (isPhraseUpdated) Color(0xFF22C55E) else Color(0xFF4ADE80)
                  } else Color(0xFF475569),
                  modifier = Modifier.size(22.dp)
                )
                if (shouldPulse) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.TopEnd)
                      .padding(1.dp)
                      .size(6.dp)
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

      // Fila desplazable de bloques (Chips de frase armada) con botón de flecha hacia abajo a la izquierda
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
  onSpeakExample: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var isRevealed by rememberSaveable(word.id) { mutableStateOf(false) }
  val wordColor = getWordSyntaxColor(word)
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
