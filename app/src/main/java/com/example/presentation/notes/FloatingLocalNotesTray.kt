package com.example.presentation.notes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Bandeja flotante de Notas Locales minimalista y simple.
 * Muestra bloques de texto en fila con un botón '+' al fondo para añadir más bloques.
 */
@Composable
fun FloatingLocalNotesTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    BackHandler { onClose() }

    val context = LocalContext.current
    val storage = remember { LocalNotesStorage(context) }
    val notesList = remember { mutableStateListOf<NoteBlock>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Cargar notas al abrir
    LaunchedEffect(isOpen) {
        notesList.clear()
        val saved = storage.loadNotes()
        if (saved.isNotEmpty()) {
            notesList.addAll(saved)
        } else {
            // Si está vacío, iniciar con un bloque vacío listo para escribir
            val initialBlock = NoteBlock(text = "")
            notesList.add(initialBlock)
            storage.saveNotes(notesList.toList())
        }
    }

    // Guardado auxiliar reactivo
    fun persistChanges() {
        storage.saveNotes(notesList.toList())
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.68f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.70f),
                                Color(0xFF818CF8).copy(alpha = 0.50f),
                                Color(0xFF38BDF8).copy(alpha = 0.35f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp)),
                color = Color(0xFF0B132B).copy(alpha = 0.96f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Header minimalista
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        Color(0xFF38BDF8).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 0.8.dp,
                                        color = Color(0xFF38BDF8).copy(alpha = 0.45f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Notas Locales",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFFF8FAFC)
                                    )
                                )
                                Text(
                                    text = "${notesList.size} ${if (notesList.size == 1) "bloque" else "bloques"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (notesList.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        notesList.clear()
                                        val emptyInitial = NoteBlock(text = "")
                                        notesList.add(emptyInitial)
                                        persistChanges()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Limpiar notas",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar notas",
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista de bloques de texto en fila con botón '+' al fondo
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        itemsIndexed(
                            items = notesList,
                            key = { _, item -> item.id }
                        ) { index, block ->
                            NoteBlockRowItem(
                                index = index + 1,
                                block = block,
                                onTextChanged = { newText ->
                                    val idx = notesList.indexOfFirst { it.id == block.id }
                                    if (idx != -1) {
                                        notesList[idx] = block.copy(text = newText)
                                        persistChanges()
                                    }
                                },
                                onDelete = {
                                    notesList.removeAll { it.id == block.id }
                                    persistChanges()
                                }
                            )
                        }

                        // Botón '+' al fondo para añadir más bloques de texto en fila
                        item(key = "add_more_button_bottom") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color(0xFF1E293B).copy(alpha = 0.50f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF38BDF8).copy(alpha = 0.35f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            val newBlock = NoteBlock(text = "")
                                            notesList.add(newBlock)
                                            persistChanges()
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(notesList.size)
                                            }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .testTag("btn_add_note_block_bottom"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(
                                                Color(0xFF38BDF8).copy(alpha = 0.20f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Añadir bloque",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Añadir bloque",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.5.sp,
                                            color = Color(0xFFBAE6FD)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renglón/bloque individual de texto en fila.
 */
@Composable
private fun NoteBlockRowItem(
    index: Int,
    block: NoteBlock,
    onTextChanged: (String) -> Unit,
    onDelete: () -> Unit
) {
    var textValue by remember(block.id) { mutableStateOf(block.text) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("note_block_item_$index"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF131D33).copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 0.8.dp,
            color = Color(0xFF334155).copy(alpha = 0.70f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador numérico sutil de la fila
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        Color(0xFF1E293B),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF7DD3FC)
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Campo de texto minimalista
            BasicTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    onTextChanged(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
                    .testTag("input_note_block_$index"),
                textStyle = TextStyle(
                    fontSize = 13.5.sp,
                    color = Color(0xFFF1F5F9),
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp
                ),
                cursorBrush = SolidColor(Color(0xFF38BDF8)),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (textValue.isEmpty()) {
                            Text(
                                text = "Escribe aquí...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Botón sutil de eliminar fila
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(26.dp)
                    .testTag("btn_delete_note_$index")
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Eliminar nota",
                    tint = Color(0xFF94A3B8).copy(alpha = 0.65f),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
