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

/**
 * Bandeja / Escritor Traductor de alta precisión: Español ➔ Inglés.
 * Ubicada justo debajo del Lienzo de Pruebas.
 *
 * Flujo:
 * - El usuario escribe palabras o frases en español en el escritor superior.
 * - Inmediatamente abajo dentro del mismo escritor se despliega la traducción al inglés.
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
    // 1. Una sola caja limpia con el símbolo de traductor a su costado - Altura compacta (34dp)
    BasicTextField(
      value = spanishText,
      onValueChange = onSpanishTextChanged,
      singleLine = true,
      textStyle = MaterialTheme.typography.bodyMedium.copy(
        color = Color(0xFFF8FAFC),
        fontSize = 12.sp,
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
            .height(34.dp)
            .background(Color(0xFF0F172A), shape = RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF334155), shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = "Traductor",
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
          ) {
            if (spanishText.isEmpty()) {
              Text(
                text = "Escribe una palabra o frase",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
                fontSize = 11.sp,
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
                  contentDescription = "Traducir al inglés",
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
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF0F172A),
          border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.55f)),
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
            // Fila superior del resultado: Píldora de idioma y acciones rápidas
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = Color(0xFF064E3B),
                  border = BorderStroke(1.dp, Color(0xFF34D399))
                ) {
                  Text(
                    text = "VERSIÓN EN INGLÉS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF34D399),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              if (englishText.isNotBlank()) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  // Botón TTS: Pronunciar en inglés
                  Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                    modifier = Modifier
                      .clickable { onSpeakEnglish(englishText) }
                      .testTag("btn_speak_english_translation")
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                      Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Escuchar pronunciación",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp)
                      )
                      Text(
                        text = "Voz",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    }
                  }

                  // Botón Copiar al portapapeles
                  Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF475569)),
                    modifier = Modifier
                      .clickable {
                        try {
                          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                          val clip = ClipData.newPlainText("Traducción en inglés", englishText)
                          clipboard?.setPrimaryClip(clip)
                          Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        } catch (_: Exception) {}
                      }
                      .testTag("btn_copy_english_translation")
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(13.dp)
                      )
                      Text(
                        text = "Copiar",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    }
                  }

                  // Botón para subir piezas al lienzo de pruebas
                  Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.8f)),
                    modifier = Modifier
                      .clickable { onAddToCanvas(englishText) }
                      .testTag("btn_add_translation_to_canvas")
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Subir al lienzo",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(13.dp)
                      )
                      Text(
                        text = "Lienzo",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    }
                  }
                }
              }
            }

            // Contenido traducido al inglés (con palabras interactivas para búsqueda táctil)
            if (englishText.isNotBlank()) {
              val words = remember(englishText) {
                englishText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
              }

              FlowRow(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("text_english_translation_result"),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                words.forEach { rawWord ->
                  val cleanWord = rawWord.filter { it.isLetterOrDigit() || it == '\'' }
                  if (cleanWord.isNotBlank()) {
                    val isSelected = selectedWord?.equals(cleanWord, ignoreCase = true) == true

                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) Color(0xFF0369A1).copy(alpha = 0.5f) else Color(0xFF1E293B),
                      border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF38BDF8).copy(alpha = 0.45f)
                      ),
                      modifier = Modifier
                        .clickable {
                          selectedWord = cleanWord
                          onWordClick(cleanWord)
                        }
                        .testTag("translated_word_$cleanWord")
                    ) {
                      Text(
                        text = cleanWord,
                        style = MaterialTheme.typography.bodyLarge.copy(
                          fontSize = 15.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = if (isSelected) Color(0xFF38BDF8) else Color(0xFFF1F5F9),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                      )
                    }
                  }
                }
              }

              // Texto crudo no cliclable tal como se ve realmente con su puntuación, comas o símbolos
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.7f)),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 2.dp)
              ) {
                Text(
                  text = englishText,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal
                  ),
                  color = Color(0xFFCBD5E1),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("raw_english_translation_text")
                )
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 1.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = Color(0xFF38BDF8).copy(alpha = 0.85f),
                  modifier = Modifier.size(12.dp)
                )
                Text(
                  text = "Toca cualquier palabra para buscarla en el vocabulario (#1-2809)",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF94A3B8),
                  fontSize = 10.sp
                )
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
