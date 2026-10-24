package com.example.presentation.phrases

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedDraftPhrase
import com.example.data.local.SavedPhrasesStorage

/**
 * Bandeja flotante para ver y administrar las frases armadas guardadas desde el lienzo de pruebas.
 * Permite reproducirlas con TTS, agregarlas de vuelta al lienzo (+ Lienzo) o eliminarlas.
 */
@Composable
fun FloatingSavedPhrasesTray(
  isOpen: Boolean,
  onClose: () -> Unit,
  onSpeakPhrase: (String) -> Unit,
  onAddToCanvas: (SavedDraftPhrase) -> Unit,
  modifier: Modifier = Modifier
) {
  if (!isOpen) return

  BackHandler { onClose() }

  val context = LocalContext.current
  val storage = remember(context) { SavedPhrasesStorage(context.applicationContext) }
  var savedPhrases by remember { mutableStateOf<List<SavedDraftPhrase>>(emptyList()) }

  fun reloadPhrases() {
    savedPhrases = storage.loadSavedPhrases()
  }

  LaunchedEffect(isOpen) {
    if (isOpen) {
      reloadPhrases()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("floating_saved_phrases_tray_overlay"),
    contentAlignment = Alignment.Center
  ) {
    // Fondo oscurecido para enfocar la bandeja flotante y cerrar al tocar fuera
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.65f))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) { onClose() }
    )

    // Contenedor flotante centrado con estética cyber-oscura
    AnimatedVisibility(
      visible = isOpen,
      enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .fillMaxHeight(0.80f)
          .shadow(16.dp, shape = RoundedCornerShape(20.dp))
          .testTag("floating_saved_phrases_dialog"),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(
          1.2.dp,
          Brush.verticalGradient(
            listOf(
              Color(0xFF38BDF8).copy(alpha = 0.8f),
              Color(0xFF818CF8).copy(alpha = 0.4f)
            )
          )
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
        ) {
          // Barra superior con Icono, Título y Botón Cerrar X
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(Color(0xFF38BDF8).copy(alpha = 0.15f), shape = CircleShape)
                  .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Bookmarks,
                  contentDescription = "Icono Frases Guardadas",
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(20.dp)
                )
              }

              Column {
                Text(
                  text = "Frases Guardadas",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFF8FAFC)
                  ),
                  modifier = Modifier.testTag("floating_saved_phrases_title")
                )
                Text(
                  text = "Frases armadas desde el lienzo de pruebas",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                  )
                )
              }
            }

            IconButton(
              onClick = onClose,
              modifier = Modifier
                .size(36.dp)
                .testTag("btn_close_saved_phrases_x")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Contador de frases guardadas
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Colección de frases (${savedPhrases.size})",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Contenido de la lista o estado vacío
          if (savedPhrases.isEmpty()) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.Bookmarks,
                  contentDescription = null,
                  tint = Color(0xFF64748B),
                  modifier = Modifier.size(46.dp)
                )
                Text(
                  text = "No tienes frases guardadas todavía",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9),
                    fontSize = 15.sp
                  )
                )
                Text(
                  text = "Arma cualquier frase en el lienzo de pruebas y presiona el icono 💾 para guardarla aquí y reutilizarla en cualquier momento.",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                  ),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("list_saved_phrases"),
              verticalArrangement = Arrangement.spacedBy(10.dp),
              contentPadding = PaddingValues(vertical = 4.dp)
            ) {
              items(savedPhrases, key = { it.id }) { phrase ->
                SavedPhraseItemCard(
                  phrase = phrase,
                  onSpeak = { onSpeakPhrase(phrase.phraseText) },
                  onAddToCanvas = {
                    onAddToCanvas(phrase)
                    Toast.makeText(context, "Frase añadida al lienzo", Toast.LENGTH_SHORT).show()
                  },
                  onDelete = {
                    storage.deletePhrase(phrase.id)
                    reloadPhrases()
                    Toast.makeText(context, "Frase eliminada", Toast.LENGTH_SHORT).show()
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Botón inferior para cerrar
          Button(
            onClick = onClose,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("btn_close_saved_phrases"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF38BDF8),
              contentColor = Color.Black
            )
          ) {
            Text(
              text = "Listo",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }
  }
}

/**
 * Tarjeta individual de una frase guardada.
 */
@Composable
private fun SavedPhraseItemCard(
  phrase: SavedDraftPhrase,
  onSpeak: () -> Unit,
  onAddToCanvas: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("saved_phrase_item_${phrase.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.85f)),
    border = BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(end = 8.dp)
        ) {
          Text(
            text = phrase.phraseText,
            style = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color(0xFFF8FAFC)
            ),
            modifier = Modifier.testTag("text_saved_phrase_${phrase.id}")
          )

          if (phrase.spanishTranslation.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = phrase.spanishTranslation,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF38BDF8),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.testTag("text_saved_phrase_trans_${phrase.id}")
              )
            }
          }
        }

        // Fila de acciones compacta en la misma tarjeta: Pronunciar (🔊), + Lienzo (botón para reutilizar), Borrar (🗑️)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          IconButton(
            onClick = onSpeak,
            modifier = Modifier
              .size(34.dp)
              .background(Color(0xFF0F172A), CircleShape)
              .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), CircleShape)
              .testTag("btn_speak_saved_phrase_${phrase.id}")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Escuchar pronunciación",
              tint = Color(0xFF38BDF8),
              modifier = Modifier.size(16.dp)
            )
          }

          Button(
            onClick = onAddToCanvas,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF38BDF8).copy(alpha = 0.2f),
              contentColor = Color(0xFF38BDF8)
            ),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier
              .height(34.dp)
              .testTag("btn_add_saved_to_canvas_${phrase.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "+ Lienzo",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(34.dp)
              .background(Color(0xFF0F172A), CircleShape)
              .testTag("btn_delete_saved_phrase_${phrase.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Eliminar frase guardada",
              tint = Color(0xFFEF4444),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
