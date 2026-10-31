package com.example.presentation.alphabet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.saveable.rememberSaveable
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
    searchQuery: String = "",
    onSpeakLetter: (AlphabetLetter) -> Unit,
    onAddLetterToCanvas: (AlphabetLetter) -> Unit,
    modifier: Modifier = Modifier
) {
    val allLetters = remember { englishAlphabetCatalog }
    val displayedLetters = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allLetters
        } else {
            val q = searchQuery.trim().lowercase()
            allLetters.filter { item ->
                item.letter.lowercase().contains(q) ||
                item.phoneticSp.lowercase().contains(q) ||
                item.ipa.lowercase().contains(q) ||
                (if (item.isVowel) "vocal" else "consonante").contains(q)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("alphabet_list_view"),
        contentPadding = PaddingValues(top = 4.dp, bottom = 260.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
 * - Pronunciación oculta bajo una flechita desplegable (igual para las 26 letras).
 * - Al tocar la flechita, se expande la bandejita mostrando la pronunciación (sin caja cuando baje).
 * - Sin caja envolvente ni bordes pesados (diseño minimalista).
 * - Separada por una línea fina horizontal al final.
 * - Muestra el número de rank, letra, contador de uso crudo y categoría.
 * - Al tocar la fila se sube al lienzo de pruebas; incluye botón de sonido ("🔊") y flechita ("⌄").
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
    var isExpanded by rememberSaveable(letterItem.letter) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alphabet_item_${letterItem.letter}")
            .clickable { onAddToCanvas() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Número de índice (1 .. 26) flotando sin caja con mayor tamaño
            Text(
                text = "$index",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                modifier = Modifier.width(34.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Letra, contador crudo y categoría flotando sin caja
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Letra grande y destacada sin caja
                    Text(
                        text = letterItem.letter,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = if (letterItem.isVowel) Color(0xFFFBBF24) else Color(0xFF38BDF8)
                    )

                    // Contador crudo de uso del abecedario: sin fondo, sin bordes, sin cajas
                    Text(
                        text = "$count",
                        fontSize = 13.5.sp,
                        fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
                        modifier = Modifier.testTag("alphabet_count_${letterItem.letter}")
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
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // 4. Icono de audio flotando sin caja agrandado
            IconButton(
                onClick = onListen,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_listen_letter_${letterItem.letter}")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Pronunciar letra ${letterItem.letter}",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // 5. Flechita para desplegar pronunciación agrandada
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_toggle_pronunciation_${letterItem.letter}")
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Ocultar pronunciación de ${letterItem.letter}" else "Mostrar pronunciación de ${letterItem.letter}",
                    tint = if (isExpanded) Color(0xFF38BDF8) else Color(0xFF64748B),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Bandejita expandible de pronunciación (sin caja, animación fluida)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, top = 4.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PRONUNCIACIÓN:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )

                    // Pronunciación fonética en español destacada
                    Text(
                        text = "(${letterItem.phoneticSp})",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = Color(0xFFF8FAFC)
                        )
                    )

                    // Transcripción Fonética IPA
                    Text(
                        text = letterItem.ipa,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = Color(0xFF38BDF8)
                        )
                    )
                }

                if (letterItem.note.isNotBlank()) {
                    Text(
                        text = "• ${letterItem.note}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 6. Simple y fina línea divisoria entre letras (exactamente como en el vocabulario)
        HorizontalDivider(
            color = Color(0xFF334155).copy(alpha = 0.4f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}
