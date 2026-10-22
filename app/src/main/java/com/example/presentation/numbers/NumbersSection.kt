package com.example.presentation.numbers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Modelo de un número en inglés (1 al 100), su escritura, fonética IPA y guía en español.
 */
data class NumberEntry(
    val number: Int,
    val word: String,
    val ipa: String,
    val phoneticSp: String,
    val isDecade: Boolean = false
)

/**
 * Generador y catálogo completo de números del 1 al 100 en inglés con fonética IPA y pronunciación en español.
 */
object NumbersCatalog {
    private val onesWords = listOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    private val teensWords = listOf("ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
    private val tensWords = listOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

    private val onesIpa = listOf("", "/wʌn/", "/tuː/", "/θriː/", "/fɔːr/", "/faɪv/", "/sɪks/", "/ˈsev.ən/", "/eɪt/", "/naɪn/")
    private val teensIpa = listOf("/ten/", "/ɪˈlev.ən/", "/twelv/", "/ˌθɜːˈtiːn/", "/ˌfɔːˈtiːn/", "/ˌfɪfˈtiːn/", "/ˌsɪkˈstiːn/", "/ˌsev.ənˈtiːn/", "/ˌeɪˈtiːn/", "/ˌnaɪnˈtiːn/")
    private val tensIpa = listOf("", "", "/ˈtwen.ti/", "/ˈθɜː.ti/", "/ˈfɔː.ti/", "/ˈfɪf.ti/", "/ˈsɪk.sti/", "/ˈsev.ən.ti/", "/ˈeɪ.ti/", "/ˈnaɪn.ti/")

    private val onesPhoneticSp = listOf("", "uán", "tu", "zri", "for", "fáiv", "six", "séven", "éit", "náin")
    private val teensPhoneticSp = listOf("ten", "iléven", "tuélv", "zer-tíin", "for-tíin", "fif-tíin", "six-tíin", "seven-tíin", "ei-tíin", "nain-tíin")
    private val tensPhoneticSp = listOf("", "", "tuénti", "zér-ti", "fór-ti", "fíf-ti", "síx-ti", "séven-ti", "éi-ti", "náin-ti")

    val allNumbers: List<NumberEntry> by lazy {
        val list = mutableListOf<NumberEntry>()
        for (n in 1..100) {
            when {
                n in 1..9 -> {
                    list.add(
                        NumberEntry(
                            number = n,
                            word = onesWords[n].replaceFirstChar { it.uppercase() },
                            ipa = onesIpa[n],
                            phoneticSp = onesPhoneticSp[n]
                        )
                    )
                }
                n in 10..19 -> {
                    list.add(
                        NumberEntry(
                            number = n,
                            word = teensWords[n - 10].replaceFirstChar { it.uppercase() },
                            ipa = teensIpa[n - 10],
                            phoneticSp = teensPhoneticSp[n - 10],
                            isDecade = (n == 10)
                        )
                    )
                }
                n in 20..99 -> {
                    val tens = n / 10
                    val rem = n % 10
                    if (rem == 0) {
                        list.add(
                            NumberEntry(
                                number = n,
                                word = tensWords[tens].replaceFirstChar { it.uppercase() },
                                ipa = tensIpa[tens],
                                phoneticSp = tensPhoneticSp[tens],
                                isDecade = true
                            )
                        )
                    } else {
                        val word = "${tensWords[tens].replaceFirstChar { it.uppercase() }}-${onesWords[rem]}"
                        val ipa = "${tensIpa[tens]} ${onesIpa[rem]}"
                        val phonetic = "${tensPhoneticSp[tens]}-${onesPhoneticSp[rem]}"
                        list.add(
                            NumberEntry(
                                number = n,
                                word = word,
                                ipa = ipa,
                                phoneticSp = phonetic
                            )
                        )
                    }
                }
                n == 100 -> {
                    list.add(
                        NumberEntry(
                            number = 100,
                            word = "One hundred",
                            ipa = "/wʌn ˈhʌndrəd/",
                            phoneticSp = "uán jándred",
                            isDecade = true
                        )
                    )
                }
            }
        }
        list
    }
}

enum class NumbersFilterCategory(val label: String) {
    ALL("Todos (1-100)"),
    ONE_TO_TWENTY("1 al 20"),
    TWENTY_ONE_TO_FIFTY("21 al 50"),
    FIFTY_ONE_TO_HUNDRED("51 al 100")
}

/**
 * Sección completa de Números del 1 al 100 en inglés con su escritura, fonética y botón de audio.
 */
@Composable
fun NumbersSection(
    onSpeakNumber: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(NumbersFilterCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var activeSpeakingNumber by remember { mutableIntStateOf(-1) }

    // Limpiar feedback auditivo tras 1.2 segundos
    LaunchedEffect(activeSpeakingNumber) {
        if (activeSpeakingNumber >= 0) {
            delay(1200)
            activeSpeakingNumber = -1
        }
    }

    val filteredList = remember(selectedFilter, searchQuery) {
        val query = searchQuery.trim().lowercase()
        NumbersCatalog.allNumbers.filter { entry ->
            val matchesFilter = when (selectedFilter) {
                NumbersFilterCategory.ALL -> true
                NumbersFilterCategory.ONE_TO_TWENTY -> entry.number in 1..20
                NumbersFilterCategory.TWENTY_ONE_TO_FIFTY -> entry.number in 21..50
                NumbersFilterCategory.FIFTY_ONE_TO_HUNDRED -> entry.number in 51..100
            }

            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                entry.number.toString().contains(query) ||
                    entry.word.lowercase().contains(query) ||
                    entry.phoneticSp.lowercase().contains(query)
            }

            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("numbers_1_to_100_list"),
        contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Buscador y Filtros
        item(key = "numbers_controls") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Buscador rápido de número o palabra
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("input_search_numbers"),
                    placeholder = {
                        Text(
                            text = "Buscar número (ej. 7, 21, fifty)...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(17.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.85f),
                        unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.65f),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color(0xFFF8FAFC),
                        unfocusedTextColor = Color(0xFFF8FAFC)
                    )
                )

                // Chips de rango
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NumbersFilterCategory.values().forEach { category ->
                        FilterChip(
                            selected = selectedFilter == category,
                            onClick = { selectedFilter = category },
                            label = {
                                Text(
                                    text = category.label,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (selectedFilter == category) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF0F172A),
                                labelColor = Color(0xFF94A3B8),
                                selectedContainerColor = Color(0xFF38BDF8).copy(alpha = 0.20f),
                                selectedLabelColor = Color(0xFF38BDF8)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == category,
                                borderColor = Color(0xFF334155),
                                selectedBorderColor = Color(0xFF38BDF8)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("chip_filter_${category.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Listado de los números
        items(filteredList, key = { it.number }) { item ->
            NumberRowCard(
                entry = item,
                isPlaying = (activeSpeakingNumber == item.number),
                onPlay = {
                    activeSpeakingNumber = item.number
                    onSpeakNumber(item.word)
                }
            )
        }
    }
}

/**
 * Fila minimalista y atractiva para un número individual (1 al 100)
 */
@Composable
private fun NumberRowCard(
    entry: NumberEntry,
    isPlaying: Boolean,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onPlay() }
            .testTag("number_card_${entry.number}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isPlaying) 1.2.dp else 0.8.dp,
            brush = if (isPlaying) {
                Brush.horizontalGradient(
                    listOf(Color(0xFF38BDF8), Color(0xFF818CF8))
                )
            } else {
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF334155).copy(alpha = 0.80f),
                        Color(0xFF1E293B).copy(alpha = 0.40f)
                    )
                )
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con el número dígito (ej. 1, 45, 100)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = if (isPlaying) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color(0xFF1E293B),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (isPlaying) Color(0xFF38BDF8) else Color(0xFF38BDF8).copy(alpha = 0.40f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${entry.number}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (entry.number >= 100) 11.5.sp else 14.sp,
                        color = Color(0xFF38BDF8)
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Textos: Palabra en inglés, IPA y pronunciación en español
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = entry.word,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFF8FAFC)
                        )
                    )

                    if (entry.isDecade) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF59E0B).copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Decena",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFBBF24)
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.ipa,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF94A3B8)
                        )
                    )

                    Text(
                        text = "•",
                        fontSize = 10.sp,
                        color = Color(0xFF475569)
                    )

                    Text(
                        text = "(${entry.phoneticSp})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF38BDF8)
                        )
                    )
                }
            }

            // Botón de audio para reproducir pronunciación
            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (isPlaying) Color(0xFF38BDF8) else Color(0xFF1E293B).copy(alpha = 0.8f),
                        shape = CircleShape
                    )
                    .testTag("btn_speak_number_${entry.number}")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Escuchar pronunciación de ${entry.word}",
                    tint = if (isPlaying) Color(0xFF0F172A) else Color(0xFF38BDF8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
