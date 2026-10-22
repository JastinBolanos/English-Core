package com.example.presentation.alphabet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
 * Pantalla/Sección del Abecedario en Inglés con pronunciación y botón de escuchar.
 */
@Composable
fun AlphabetListSection(
    onSpeakLetter: (String) -> Unit,
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("alphabet_list_view"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Chips de filtro del abecedario
        item(key = "alphabet_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                // Fila de Filtros (Todas 26, Vocales 5, Consonantes 21)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            }
        }

        // Lista de las letras
        itemsIndexed(
            items = displayedLetters,
            key = { _, item -> item.letter }
        ) { index, item ->
            AlphabetLetterRowItem(
                index = index + 1,
                letterItem = item,
                onListen = { onSpeakLetter(item.letter) }
            )
        }
    }
}

/**
 * Fila individual de una letra del abecedario.
 */
@Composable
private fun AlphabetLetterRowItem(
    index: Int,
    letterItem: AlphabetLetter,
    onListen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alphabet_item_${letterItem.letter}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF131D33).copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            if (letterItem.isVowel) Color(0xFFF59E0B).copy(alpha = 0.35f)
            else Color(0xFF334155).copy(alpha = 0.70f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Letra grande y destacada en su recuadro estilizado
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (letterItem.isVowel) Color(0xFFF59E0B).copy(alpha = 0.15f)
                        else Color(0xFF38BDF8).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (letterItem.isVowel) Color(0xFFF59E0B).copy(alpha = 0.4f)
                        else Color(0xFF38BDF8).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letterItem.letter,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (letterItem.isVowel) Color(0xFFFBBF24) else Color(0xFF38BDF8)
                    )
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // 2. Información fonética: IPA y pronunciación en español
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pronunciación en español destacada
                    Text(
                        text = "(${letterItem.phoneticSp})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFF8FAFC)
                        )
                    )

                    // Transcripción Fonética IPA
                    Text(
                        text = letterItem.ipa,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    )

                    // Badge de vocal o consonante
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (letterItem.isVowel) Color(0xFFF59E0B).copy(alpha = 0.2f)
                        else Color(0xFF1E293B),
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Text(
                            text = if (letterItem.isVowel) "Vocal" else "Consonante",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (letterItem.isVowel) Color(0xFFFBBF24) else Color(0xFF64748B)
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (letterItem.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = letterItem.note,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8).copy(alpha = 0.85f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Botón de escuchar con icono de sonido
            Surface(
                onClick = onListen,
                shape = CircleShape,
                color = Color(0xFF0284C7),
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_listen_letter_${letterItem.letter}")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Escuchar letra ${letterItem.letter}",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
