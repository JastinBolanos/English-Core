package com.example.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Bandeja flotante de Ajustes y Copia de Seguridad JSON.
 * Permite exportar y restaurar todo el historial, progreso, puntaje de trofeos y notas locales
 * de manera robusta y sin perder datos.
 */
@Composable
fun FloatingSettingsBackupTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    vocabularyActiveWords: Int,
    vocabularyTotalUses: Int,
    alphabetActiveCount: Int,
    alphabetTotalUses: Int,
    numbersActiveCount: Int,
    numbersTotalUses: Int,
    phrasesActiveCount: Int,
    phrasesTotalUses: Int,
    localNotesCount: Int,
    statusMessage: String?,
    onClearStatusMessage: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onDirectRestore: (() -> Unit)? = null,
    hasDirectBackup: Boolean = false,
    onOpenProficiencyLevels: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    BackHandler { onClose() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("floating_settings_backup_tray_overlay"),
        contentAlignment = Alignment.TopEnd
    ) {
        // Fondo semi-transparente que oscurece el fondo y permite cerrar al tocar fuera
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.50f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClose() }
        )

        // Contenedor lateral anclado al borde derecho de la pantalla
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            val trayShape = RoundedCornerShape(
                bottomStart = 24.dp,
                bottomEnd = 0.dp,
                topStart = 0.dp,
                topEnd = 0.dp
            )
            Surface(
                modifier = Modifier
                    .fillMaxHeight(0.75f)
                    .fillMaxWidth(0.85f)
                    .widthIn(max = 420.dp)
                    .align(Alignment.TopEnd)
                    .shadow(16.dp, shape = trayShape)
                    .clip(trayShape)
                    .testTag("floating_settings_backup_dialog"),
                shape = trayShape,
                color = Color(0xFF0F172A),
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Barra superior con Icono de Ajustes, Título y botón cerrar
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
                                    .size(36.dp)
                                    .background(
                                        Color(0xFF38BDF8).copy(alpha = 0.15f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Ajustes y Datos",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(0xFFF8FAFC)
                                )
                            )
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_close_settings_tray")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar ajustes",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // FILA 1: Copas Ganadas (🏆) y Usos de Vocabulario (📚)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Módulo 1: Copas (Palabras usadas mínimo 1 vez)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = onOpenProficiencyLevels != null) {
                                    onOpenProficiencyLevels?.invoke()
                                }
                                .testTag("card_progress_trophies"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF131D33)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                Color(0xFFFACC15).copy(alpha = 0.35f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🏆 $vocabularyActiveWords",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFFACC15)
                                )
                                Text(
                                    text = "Copas (mín. 1 uso)",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFDE047),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "$vocabularyActiveWords palabras con copa",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Módulo 2: Usos de Vocabulario (Frecuencia total de uso)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_progress_vocabulary"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF131D33)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                Color(0xFF38BDF8).copy(alpha = 0.35f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📚 $vocabularyTotalUses",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "Usos Vocabulario",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF7DD3FC),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "$vocabularyTotalUses usos acumulados",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // FILA 2: Notas Locales (📝) y Abecedario (🔤)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Módulo 3: Notas locales
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_progress_notes"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF131D33)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                Color(0xFF60A5FA).copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📝 $localNotesCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF60A5FA)
                                )
                                Text(
                                    text = "Notas locales",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF93C5FD),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "$localNotesCount notas guardadas",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Módulo 4: Abecedario
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_progress_alphabet"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF131D33)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                Color(0xFF2DD4BF).copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🔤 $alphabetTotalUses",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF2DD4BF)
                                )
                                Text(
                                    text = "Abecedario ($alphabetActiveCount/26)",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF5EEAD4),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "$alphabetTotalUses prácticas de letras",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // FILA 3: Números (🔢) y Frases Cotidianas (💬)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Módulo 5: Números
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_progress_numbers"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF131D33)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                Color(0xFF34D399).copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🔢 $numbersTotalUses",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF34D399)
                                )
                                Text(
                                    text = "Números ($numbersActiveCount/100)",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6EE7B7),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "$numbersTotalUses prácticas de números",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Módulo 6: Frases cotidianas
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_progress_phrases"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF131D33)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                Color(0xFFA855F7).copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "💬 $phrasesTotalUses",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFA855F7)
                                )
                                Text(
                                    text = "Frases ($phrasesActiveCount/50)",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFC084FC),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "$phrasesTotalUses prácticas de frases",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tarjeta destacada para consultar Niveles de Dominio y Copas (CEFR)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = onOpenProficiencyLevels != null) {
                                onOpenProficiencyLevels?.invoke()
                            }
                            .testTag("banner_proficiency_trophies_settings"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF131D33)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFFACC15).copy(alpha = 0.45f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "🏆", fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = "Niveles de Dominio (Copas)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = Color(0xFFFACC15)
                                        )
                                    )
                                    Text(
                                        text = "Toca para abrir tu desglose CEFR (A1 - C2)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Abrir ➔",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFDE047)
                            )
                        }
                    }

                    // Mensaje de estado (éxito o aviso)
                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF064E3B).copy(alpha = 0.85f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = statusMessage,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFECFDF5)
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = onClearStatusMessage,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Descartar",
                                        tint = Color(0xFFA7F3D0),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ACCIONES PRINCIPALES:
                    // 1. Exportar copia JSON
                    Button(
                        onClick = onExportBackup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_export_json_backup"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exportar copia (.json)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Buscar y restaurar archivo (.json)
                    OutlinedButton(
                        onClick = onImportBackup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_import_json_backup"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFF38BDF8)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF38BDF8)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buscar y restaurar archivo (.json)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF38BDF8)
                            )
                        )
                    }


                }
            }
        }
    }
}
