package com.example.presentation.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.gemini.GeminiChatMessage

@Composable
fun GeminiConsultationTray(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  messages: List<GeminiChatMessage>,
  isLoading: Boolean,
  inputText: String,
  onInputTextChanged: (String) -> Unit,
  onSendMessage: (String) -> Unit,
  onClearChat: () -> Unit,
  apiKey: String,
  onApiKeyChanged: (String) -> Unit,
  draftPhrase: String = "",
  modifier: Modifier = Modifier
) {
  if (!isOpen) return

  val listState = rememberLazyListState()
  var showApiKeyField by remember { mutableStateOf(false) }

  // Auto-scroll al recibir o enviar un mensaje nuevo
  LaunchedEffect(messages.size, isLoading) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnClickOutside = true,
      dismissOnBackPress = true,
      usePlatformDefaultWidth = false
    )
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.94f)
        .widthIn(max = 520.dp)
        .heightIn(min = 380.dp, max = 580.dp)
        .testTag("gemini_consultation_dialog"),
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFF0F172A), // Slate 900
      border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.55f)),
      shadowElevation = 12.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // 1. Cabecera con Icono de Estrella de Gemini, Título y Acciones
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF0284C7).copy(alpha = 0.25f),
              border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .padding(4.dp),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.AutoAwesome,
                  contentDescription = "Estrella Gemini IA",
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Gemini IA",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFF1F5F9)
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFF064E3B)
                ) {
                  Text(
                    text = "3.5 Flash",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6EE7B7),
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Tutor pedagógico de inglés",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            if (apiKey.isBlank()) {
              IconButton(
                onClick = { showApiKeyField = !showApiKeyField },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Key,
                  contentDescription = "Configurar API Key",
                  tint = Color(0xFFFACC15),
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            if (messages.isNotEmpty()) {
              IconButton(
                onClick = onClearChat,
                modifier = Modifier
                  .size(32.dp)
                  .testTag("btn_clear_gemini_chat")
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Limpiar conversación",
                  tint = Color(0xFF94A3B8),
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(32.dp)
                .testTag("btn_close_gemini_tray")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar bandeja de Gemini",
                tint = Color(0xFFE2E8F0),
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        // Campo opcional para ingresar o cambiar la clave de API de Gemini si está vacía
        AnimatedVisibility(visible = showApiKeyField || apiKey.isBlank()) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "🔑 Clave de API de Gemini (Opcional):",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFDE68A),
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Si no está configurada en .env, puedes pegarla aquí para usar Gemini:",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChanged,
                placeholder = { Text("AIzaSy...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_gemini_api_key"),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color(0xFF38BDF8),
                  unfocusedBorderColor = Color(0xFF475569)
                )
              )
            }
          }
        }

        // Sugerencias rápidas en píldoras horizontales
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (draftPhrase.isNotBlank()) {
            SuggestionPill(
              text = "Analizar frase: '$draftPhrase'",
              onClick = {
                onSendMessage("Explícame la frase '$draftPhrase'. ¿Qué significa, en qué contexto se usa y qué regla gramatical sigue?")
              }
            )
          }

          SuggestionPill(
            text = "Diferencia 'make' vs 'do'",
            onClick = {
              onSendMessage("¿Cuál es la diferencia entre los verbos 'make' y 'do' en inglés? Dame ejemplos claros.")
            }
          )

          SuggestionPill(
            text = "3 ejemplos de conectores",
            onClick = {
              onSendMessage("Dame 3 ejemplos prácticos con conectores en inglés (ej: however, although, because) con su traducción.")
            }
          )

          SuggestionPill(
            text = "Tips para pronunciación",
            onClick = {
              onSendMessage("¿Cuáles son los 3 errores de pronunciación más comunes para hispanohablantes al aprender inglés y cómo evitarlos?")
            }
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Área de Conversación (LazyColumn que preserva mensajes)
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          if (messages.isEmpty() && !isLoading) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF38BDF8).copy(alpha = 0.6f),
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "¡Pregúntale a Gemini!",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE2E8F0)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Escribe cualquier consulta sobre una palabra, gramática, pronunciación o las frases del lienzo de pruebas.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          } else {
            LazyColumn(
              state = listState,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("gemini_chat_messages_list"),
              contentPadding = PaddingValues(vertical = 4.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(
                items = messages,
                key = { it.id }
              ) { message ->
                ChatMessageBubble(message = message)
              }

              if (isLoading) {
                item(key = "loading_indicator") {
                  GeminiTypingIndicator()
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Barra de Entrada de Consulta
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = inputText,
            onValueChange = onInputTextChanged,
            placeholder = {
              Text(
                text = "Escribe tu duda sobre inglés...",
                color = Color(0xFF64748B),
                fontSize = 13.sp
              )
            },
            modifier = Modifier
              .weight(1f)
              .testTag("input_gemini_query"),
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFF1F5F9), fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFF38BDF8),
              unfocusedBorderColor = Color(0xFF334155),
              focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.7f),
              unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
              onSend = {
                if (inputText.isNotBlank() && !isLoading) {
                  onSendMessage(inputText)
                }
              }
            )
          )

          Surface(
            shape = CircleShape,
            color = if (inputText.isNotBlank() && !isLoading) Color(0xFF0284C7) else Color(0xFF1E293B),
            border = BorderStroke(
              1.dp,
              if (inputText.isNotBlank() && !isLoading) Color(0xFF38BDF8) else Color(0xFF334155)
            ),
            modifier = Modifier
              .size(44.dp)
              .clickable(
                enabled = inputText.isNotBlank() && !isLoading,
                onClick = { onSendMessage(inputText) }
              )
              .testTag("btn_send_gemini_query")
          ) {
            Box(
              modifier = Modifier.fillMaxWidth(),
              contentAlignment = Alignment.Center
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(18.dp),
                  strokeWidth = 2.dp,
                  color = Color(0xFF38BDF8)
                )
              } else {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "Enviar a Gemini",
                  tint = if (inputText.isNotBlank()) Color.White else Color(0xFF64748B),
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ChatMessageBubble(message: GeminiChatMessage) {
  val isUser = message.isFromUser
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      Box(
        modifier = Modifier
          .padding(top = 4.dp, end = 6.dp)
          .size(22.dp)
          .clip(CircleShape)
          .background(Color(0xFF0284C7).copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Filled.AutoAwesome,
          contentDescription = null,
          tint = Color(0xFF38BDF8),
          modifier = Modifier.size(13.dp)
        )
      }
    }

    Surface(
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (isUser) 14.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 14.dp
      ),
      color = if (isUser) Color(0xFF0369A1) else Color(0xFF1E293B),
      border = BorderStroke(
        1.dp,
        if (isUser) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFF334155).copy(alpha = 0.6f)
      ),
      modifier = Modifier.widthIn(max = 380.dp)
    ) {
      Text(
        text = message.text,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Color(0xFFF1F5F9),
          fontSize = 13.sp,
          lineHeight = 18.sp
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
      )
    }
  }
}

@Composable
private fun GeminiTypingIndicator() {
  val infiniteTransition = rememberInfiniteTransition(label = "TypingTransition")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "TypingAlpha"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.padding(start = 28.dp, top = 4.dp)
  ) {
    CircularProgressIndicator(
      modifier = Modifier.size(12.dp),
      strokeWidth = 2.dp,
      color = Color(0xFF38BDF8)
    )
    Text(
      text = "Gemini está pensando...",
      style = MaterialTheme.typography.labelSmall,
      color = Color(0xFF38BDF8).copy(alpha = alpha),
      fontSize = 11.sp
    )
  }
}

@Composable
private fun SuggestionPill(
  text: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF1E293B).copy(alpha = 0.8f),
    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = Color(0xFFBAE6FD),
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
  }
}
