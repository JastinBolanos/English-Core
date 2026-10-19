package com.example.presentation.youtube

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class YouTubeLessonItem(
    val title: String,
    val description: String,
    val videoId: String,
    val durationBadge: String
)

val sampleYouTubeLessons = listOf(
    YouTubeLessonItem(
        title = "Top 100 Palabras Más Comunes en Inglés",
        description = "Aprende vocabulario elemental con pronunciación nativa clara.",
        videoId = "d93m4s_Gf5A",
        durationBadge = "Vocabulario"
    ),
    YouTubeLessonItem(
        title = "Conectores y Estructura de Oraciones",
        description = "Cómo unir sujetos, verbos y complementos fluidamente.",
        videoId = "fJ9rUzIMcZQ",
        durationBadge = "Gramática"
    ),
    YouTubeLessonItem(
        title = "Pronunciación en Inglés Paso a Paso",
        description = "Sonidos clave y diferencias entre fonemas similares.",
        videoId = "7u0_yC0aYF8",
        durationBadge = "Fonética"
    ),
    YouTubeLessonItem(
        title = "Frases Cotidianas de Alta Frecuencia",
        description = "Expresiones prácticas para situaciones reales.",
        videoId = "84kLpP7j9iQ",
        durationBadge = "Conversación"
    )
)

/**
 * Extrae el videoId de una URL estándar de YouTube (youtube.com/watch?v=... o youtu.be/...) o devuelve el ID tal cual.
 */
fun extractYouTubeVideoId(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return ""
    return when {
        trimmed.contains("v=") -> {
            trimmed.substringAfter("v=").substringBefore("&").substringBefore("?")
        }
        trimmed.contains("youtu.be/") -> {
            trimmed.substringAfter("youtu.be/").substringBefore("?")
        }
        trimmed.contains("embed/") -> {
            trimmed.substringAfter("embed/").substringBefore("?")
        }
        else -> trimmed
    }
}

@Composable
fun YouTubeVideoSelectorDialog(
    onDismissRequest: () -> Unit,
    onSelectVideo: (videoId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var customInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("dialog_youtube_selector"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Encabezado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFF0000).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFFF0000).copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = Color(0xFFFF0000),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Sticky YouTube Player",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF8FAFC)
                            )
                            Text(
                                text = "Aparece entre el Header y las listas sin romper el scroll",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("btn_close_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para ingresar cualquier ID o enlace
                Text(
                    text = "Reproducir cualquier video de YouTube:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customInput,
                        onValueChange = { customInput = it },
                        placeholder = { Text("Video ID (ej. d93m4s_Gf5A) o enlace", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_youtube_video_id"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val id = extractYouTubeVideoId(customInput)
                            if (id.isNotBlank()) {
                                onSelectVideo(id)
                                onDismissRequest()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_play_custom_video")
                    ) {
                        Text("Ver", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Lecciones y tutoriales recomendados:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de lecciones
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sampleYouTubeLessons) { lesson ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectVideo(lesson.videoId)
                                    onDismissRequest()
                                }
                                .testTag("lesson_${lesson.videoId}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = lesson.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF1F5F9)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF0284C7).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = lesson.durationBadge,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                color = Color(0xFF7DD3FC),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = lesson.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botón para probar la Activity pura XML con ViewBinding
                OutlinedButton(
                    onClick = {
                        val intent = Intent(context, YouTubeStickyPlayerActivity::class.java)
                        context.startActivity(intent)
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_open_xml_viewbinding_activity"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF64748B))
                ) {
                    Icon(
                        imageVector = Icons.Default.OndemandVideo,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Abrir pantalla XML pura (ViewBinding)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}
