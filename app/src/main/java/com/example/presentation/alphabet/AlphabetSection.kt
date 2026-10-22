package com.example.presentation.alphabet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modelo de una letra del abecedario inglés con su fonética IPA y guía en español.
 */
data class AlphabetLetter(
    val letter: String,
    val isVowel: Boolean,
    val ipa: String,
    val phoneticSp: String,
    val note: String = ""
)

/**
 * Catálogo completo de las 26 letras del abecedario inglés según EF English Live.
 */
val englishAlphabetCatalog = listOf(
    AlphabetLetter(letter = "A", isVowel = true, ipa = "/ei/", phoneticSp = "éi"),
    AlphabetLetter(letter = "B", isVowel = false, ipa = "/biː/", phoneticSp = "bí"),
    AlphabetLetter(letter = "C", isVowel = false, ipa = "/siː/", phoneticSp = "sí"),
    AlphabetLetter(letter = "D", isVowel = false, ipa = "/diː/", phoneticSp = "dí"),
    AlphabetLetter(letter = "E", isVowel = true, ipa = "/iː/", phoneticSp = "í"),
    AlphabetLetter(letter = "F", isVowel = false, ipa = "/ef/", phoneticSp = "ef"),
    AlphabetLetter(letter = "G", isVowel = false, ipa = "/dʒiː/", phoneticSp = "yí"),
    AlphabetLetter(letter = "H", isVowel = false, ipa = "/eɪtʃ/", phoneticSp = "éich"),
    AlphabetLetter(letter = "I", isVowel = true, ipa = "/aɪ/", phoneticSp = "ái"),
    AlphabetLetter(letter = "J", isVowel = false, ipa = "/dʒeɪ/", phoneticSp = "yéi"),
    AlphabetLetter(letter = "K", isVowel = false, ipa = "/keɪ/", phoneticSp = "kéi"),
    AlphabetLetter(letter = "L", isVowel = false, ipa = "/el/", phoneticSp = "el"),
    AlphabetLetter(letter = "M", isVowel = false, ipa = "/em/", phoneticSp = "em"),
    AlphabetLetter(letter = "N", isVowel = false, ipa = "/en/", phoneticSp = "en"),
    AlphabetLetter(letter = "O", isVowel = true, ipa = "/əʊ/", phoneticSp = "óu"),
    AlphabetLetter(letter = "P", isVowel = false, ipa = "/piː/", phoneticSp = "pí"),
    AlphabetLetter(letter = "Q", isVowel = false, ipa = "/kjuː/", phoneticSp = "kiú"),
    AlphabetLetter(letter = "R", isVowel = false, ipa = "/ɑːr/", phoneticSp = "ar"),
    AlphabetLetter(letter = "S", isVowel = false, ipa = "/es/", phoneticSp = "es"),
    AlphabetLetter(letter = "T", isVowel = false, ipa = "/tiː/", phoneticSp = "tí"),
    AlphabetLetter(letter = "U", isVowel = true, ipa = "/juː/", phoneticSp = "iú"),
    AlphabetLetter(letter = "V", isVowel = false, ipa = "/viː/", phoneticSp = "ví"),
    AlphabetLetter(letter = "W", isVowel = false, ipa = "/ˈdʌb.əl.juː/", phoneticSp = "dábal-iú"),
    AlphabetLetter(letter = "X", isVowel = false, ipa = "/eks/", phoneticSp = "eks"),
    AlphabetLetter(letter = "Y", isVowel = false, ipa = "/waɪ/", phoneticSp = "wái"),
    AlphabetLetter(letter = "Z", isVowel = false, ipa = "/ziː/ · /zed/", phoneticSp = "zí / zed", note = "zí en EE. UU. / zed en Reino Unido")
)

enum class AlphabetFilter {
    ALL,
    VOWELS,
    CONSONANTS
}

/**
 * Pantalla/Sección del Abecedario en Inglés con pronunciación, contador de práctica y opción de subir al lienzo.
 * Diseño minimalista idéntico al del vocabulario: sin cajas pesadas, con línea fina divisoria y contador crudo.
 */
@Composable
fun AlphabetListSection(
    letterCounts: Map<String, Int> = emptyMap(),
    onSpeakLetter: (AlphabetLetter) -> Unit,
    onAddLetterToCanvas: (AlphabetLetter) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(AlphabetFilter.ALL) }

    val displayedLetters = remember(selectedFilter) {
        when (selectedFilter) {
            AlphabetFilter.ALL -> englishAlphabetCatalog
            AlphabetFilter.VOWELS -> englishAlphabetCatalog.filter { it.isVowel }
            AlphabetFilter.CONSONANTS -> englishAlphabetCatalog.filter { !it.isVowel }
        }
    }

    val totalPractices = remember(letterCounts) {
        letterCounts.values.sum()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("alphabet_list_view"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Encabezado con chips de filtro y contador acumulado del abecedario
        item(key = "alphabet_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Fila de Filtros (Todas 26, Vocales 5, Consonantes 21) y Contador Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selectedFilter == AlphabetFilter.ALL,
                            onClick = { selectedFilter = AlphabetFilter.ALL },
                            label = { Text("Todas (26)", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = null
                        )

                        FilterChip(
                            selected = selectedFilter == AlphabetFilter.VOWELS,
                            onClick = { selectedFilter = AlphabetFilter.VOWELS },
                            label = { Text("Vocales (5)", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF59E0B),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = null
                        )

                        FilterChip(
                            selected = selectedFilter == AlphabetFilter.CONSONANTS,
                            onClick = { selectedFilter = AlphabetFilter.CONSONANTS },
                            label = { Text("Consonantes (21)", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF38BDF8),
                                selectedLabelColor = Color(0xFF0F172A),
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = null
                        )
                    }

                    // Contador global del abecedario
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.testTag("alphabet_total_practices_badge")
                    ) {
                        Text(
                            text = "Usos:",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "$totalPractices",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (totalPractices > 0) Color(0xFF38BDF8) else Color(0xFF64748B)
                            )
                        )
                    }
                }
            }
        }

        // Lista de las letras sin caja, separadas por línea fina (igual que en el vocabulario)
        itemsIndexed(
            items = displayedLetters,
            key = { _, item -> item.letter }
        ) { index, item ->
            val count = letterCounts[item.letter] ?: 0
            AlphabetLetterRowItem(
                index = index + 1,
                letterItem = item,
                count = count,
                onListen = { onSpeakLetter(item) },
                onAddToCanvas = { onAddLetterToCanvas(item) }
            )
        }
    }
}

/**
 * Fila individual de una letra del abecedario:
 * - Sin caja envolvente ni bordes pesados (exactamente igual que WordHudCard en el vocabulario).
 * - Separada por una línea fina horizontal al final.
 * - Muestra el número de rank, letra, contador de uso crudo, fonética IPA y español.
 * - Botón para subir al lienzo de pruebas ("+ Lienzo") y botón de sonido ("🔊").
 * - Al tocar la fila también se sube al lienzo e incrementa su contador.
 */
@Composable
private fun AlphabetLetterRowItem(
    index: Int,
    letterItem: AlphabetLetter,
    count: Int,
    onListen: () -> Unit,
    onAddToCanvas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alphabet_item_${letterItem.letter}")
            .clickable { onAddToCanvas() }
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Número de índice (#1 .. #26) flotando sin caja
            Text(
                text = "#$index",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                fontSize = 9.5.sp,
                modifier = Modifier.width(26.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Letra, contador crudo, pronunciación y categoría flotando sin caja
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Letra grande y destacada sin caja
                    Text(
                        text = letterItem.letter,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = if (letterItem.isVowel) Color(0xFFFBBF24) else Color(0xFF38BDF8)
                    )

                    // Contador crudo de uso del abecedario: sin fondo, sin bordes, sin cajas
                    Text(
                        text = "$count",
                        fontSize = 10.5.sp,
                        fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
                        modifier = Modifier.testTag("alphabet_count_${letterItem.letter}")
                    )

                    // Pronunciación en español destacada
                    Text(
                        text = "(${letterItem.phoneticSp})",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = Color(0xFFF8FAFC)
                        )
                    )

                    // Transcripción Fonética IPA
                    Text(
                        text = letterItem.ipa,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (letterItem.isVowel) "Vocal" else "Consonante",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (letterItem.isVowel) Color(0xFFF59E0B) else Color(0xFF64748B),
                        fontSize = 8.5.sp
                    )

                    if (letterItem.note.isNotBlank()) {
                        Text(
                            text = "• ${letterItem.note}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF38BDF8).copy(alpha = 0.85f),
                            fontSize = 8.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 3. Botón para subir al lienzo de pruebas ("+ Lienzo")
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF0284C7).copy(alpha = 0.15f),
                border = BorderStroke(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                modifier = Modifier
                    .clickable { onAddToCanvas() }
                    .testTag("btn_add_letter_to_canvas_${letterItem.letter}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Subir letra ${letterItem.letter} al lienzo",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Lienzo",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // 4. Icono de audio flotando sin caja
            IconButton(
                onClick = onListen,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("btn_listen_letter_${letterItem.letter}")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Pronunciar letra ${letterItem.letter}",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        // 5. Simple y fina línea divisoria entre letras (exactamente como en el vocabulario)
        HorizontalDivider(
            color = Color(0xFF334155).copy(alpha = 0.4f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
