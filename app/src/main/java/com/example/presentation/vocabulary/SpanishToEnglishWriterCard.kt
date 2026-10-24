package com.example.presentation.vocabulary

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.translation.TranslationState

import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.SwapHoriz

/**
 * Bandeja / Escritor Traductor de alta precisión: Español ⇄ Inglés.
 * Ubicada justo debajo del Lienzo de Pruebas.
 *
 * Flujo:
 * - El usuario puede alternar la dirección entre Español ➔ Inglés y Inglés ➔ Español con el mini icono de inversión.
 * - Si hay contenido escrito y traducido, se invierten ambos lados como en Google Translate.
 * - Incluye reproducción auditiva nativa (TTS), copiado al portapapeles y botón para subir al lienzo.
 * - Cada palabra en inglés es interactiva y al tocarla busca esa palabra en el vocabulario (#1-2809).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpanishToEnglishWriterCard(
  spanishText: String,
  onSpanishTextChanged: (String) -> Unit,
  onTranslateNow: () -> Unit,
  onClear: () -> Unit,
  translationState: TranslationState,
  onSpeakEnglish: (String) -> Unit,
  onAddToCanvas: (String) -> Unit,
  onWordClick: (String) -> Unit = {},
  isSpanishToEnglish: Boolean = true,
  onSwapLanguages: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val focusManager = LocalFocusManager.current

  var selectedWord by remember { mutableStateOf<String?>(null) }

  val englishText = (translationState as? TranslationState.Success)?.translation ?: ""
  val isTranslating = translationState is TranslationState.Translating || translationState is TranslationState.DownloadingModel
  val hasContent = spanishText.isNotBlank() || englishText.isNotBlank()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 1.dp)
      .testTag("spanish_english_writer_card"),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    // 1. Caja tenue integrada sin cinta de borde con altura generosa (48dp) para escribir fácilmente
    BasicTextField(
      value = spanishText,
      onValueChange = onSpanishTextChanged,
      singleLine = true,
      textStyle = MaterialTheme.typography.bodyMedium.copy(
        color = Color(0xFFF8FAFC),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium
      ),
      cursorBrush = SolidColor(Color(0xFF38BDF8)),
      keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done
      ),
      keyboardActions = KeyboardActions(
        onDone = {
          focusManager.clearFocus()
          onTranslateNow()
        }
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_spanish_text"),
      decorationBox = { innerTextField ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFF131D2E).copy(alpha = 0.88f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mini botón selector/inversor de dirección de traducción (Google Translate style)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onSwapLanguages() }
              .padding(horizontal = 4.dp, vertical = 2.dp)
              .testTag("btn_swap_translation_direction")
          ) {
            Text(
              text = if (isSpanishToEnglish) "ES" else "EN",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF38BDF8),
              fontSize = 11.5.sp
            )
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = "Invertir dirección de traducción",
              tint = Color(0xFF38BDF8),
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (isSpanishToEnglish) "EN" else "ES",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8),
              fontSize = 11.5.sp
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
          ) {
            if (spanishText.isEmpty()) {
              Text(
                text = if (isSpanishToEnglish) "Escribe en español..." else "Escribe en inglés...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8),
                fontSize = 14.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            innerTextField()
          }
          if (isTranslating) {
            CircularProgressIndicator(
              modifier = Modifier.size(15.dp),
              strokeWidth = 2.dp,
              color = Color(0xFF38BDF8)
            )
          } else if (spanishText.isNotBlank()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(start = 4.dp)
            ) {
              IconButton(
                onClick = {
                  selectedWord = null
                  onClear()
                },
                modifier = Modifier
                  .size(26.dp)
                  .testTag("btn_clear_spanish_writer")
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Limpiar escritor",
                  tint = Color(0xFF94A3B8),
                  modifier = Modifier.size(16.dp)
                )
              }
              IconButton(
                onClick = {
                  focusManager.clearFocus()
                  onTranslateNow()
                },
                modifier = Modifier
                  .size(26.dp)
                  .testTag("btn_trigger_translation")
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = if (isSpanishToEnglish) "Traducir al inglés" else "Traducir al español",
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    )

      // 2. Despliegue de la versión en inglés justo más abajo en este escritor
      AnimatedVisibility(
        visible = englishText.isNotBlank() || isTranslating,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF131D2E).copy(alpha = 0.88f),
          border = null,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("english_translation_result_box")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Fila unificada: palabra(s) traducida(s) a la izquierda y los 3 botones a la derecha en armonía
            if (englishText.isNotBlank()) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 2.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                // Contenido traducido al inglés interactivo SIN CAJA (solo texto con todas sus funciones de búsqueda táctil)
                val words = remember(englishText) {
                  englishText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                }

                FlowRow(
                  modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .testTag("text_english_translation_result"),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  words.forEach { rawWord ->
                    val cleanWord = rawWord.filter { it.isLetterOrDigit() || it == '\'' }
                    if (cleanWord.isNotBlank()) {
                      val isSelected = selectedWord?.equals(cleanWord, ignoreCase = true) == true

                      Text(
                        text = cleanWord,
                        style = MaterialTheme.typography.bodyLarge.copy(
                          fontSize = 17.sp,
                          fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                        ),
                        color = if (isSelected) Color(0xFF00E5FF) else Color(0xFFF1F5F9),
                        modifier = Modifier
                          .clickable {
                            selectedWord = cleanWord
                            onWordClick(cleanWord)
                          }
                          .padding(horizontal = 2.dp, vertical = 2.dp)
                          .testTag("translated_word_$cleanWord")
                      )
                    }
                  }
                }

                // Los 3 botones posicionados a su lado: volumen, copiar y + Lienzo
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  // Icono de volumen (sin caja, sin texto)
                  IconButton(
                    onClick = { onSpeakEnglish(englishText) },
                    modifier = Modifier
                      .size(30.dp)
                      .testTag("btn_speak_english_translation")
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                      contentDescription = "Escuchar pronunciación",
                      tint = Color(0xFF38BDF8),
                      modifier = Modifier.size(19.dp)
                    )
                  }

                  // Icono de copiar (sin caja, sin texto)
                  IconButton(
                    onClick = {
                      try {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clip = ClipData.newPlainText("Traducción", englishText)
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
                      } catch (_: Exception) {}
                    },
                    modifier = Modifier
                      .size(30.dp)
                      .testTag("btn_copy_english_translation")
                  ) {
                    Icon(
                      imageVector = Icons.Default.ContentCopy,
                      contentDescription = "Copiar",
                      tint = Color(0xFF94A3B8),
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  // Icono + con palabra Lienzo (sin caja)
                  Row(
                    modifier = Modifier
                      .clickable { onAddToCanvas(if (isSpanishToEnglish) englishText else spanishText) }
                      .padding(horizontal = 4.dp, vertical = 4.dp)
                      .testTag("btn_add_translation_to_canvas"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "Subir al lienzo",
                      tint = Color(0xFF38BDF8),
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = "Lienzo",
                      style = MaterialTheme.typography.labelSmall,
                      color = Color(0xFF38BDF8),
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                }
              }
            } else if (isTranslating) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
              ) {
                CircularProgressIndicator(
                  modifier = Modifier.size(14.dp),
                  strokeWidth = 2.dp,
                  color = Color(0xFF38BDF8)
                )
                Text(
                  text = "Traduciendo con precisión...",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF94A3B8),
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }
    }
}
