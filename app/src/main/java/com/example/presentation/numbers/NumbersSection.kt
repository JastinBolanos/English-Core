package com.example.presentation.numbers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
 * Sección completa de Números del 1 al 100 en inglés con su escritura, fonética, contador y botón para subir al lienzo.
 * Diseño minimalista idéntico al del vocabulario y abecedario: sin cajas pesadas, con línea fina divisoria y contador crudo.
 */
@Composable
fun NumbersSection(
    numberCounts: Map<Int, Int> = emptyMap(),
    onSpeakNumber: (NumberEntry) -> Unit,
    onAddNumberToCanvas: (NumberEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(searchQuery) {
        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) {
            NumbersCatalog.allNumbers
        } else {
            NumbersCatalog.allNumbers.filter { entry ->
                entry.number.toString().contains(query) ||
                    entry.word.lowercase().contains(query) ||
                    entry.phoneticSp.lowercase().contains(query)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("numbers_1_to_100_list"),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Buscador rápido sin texto fantasma
        item(key = "numbers_controls") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("input_search_numbers"),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(21.dp)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Buscar",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.5.sp,
                                color = Color(0xFF64748B)
                            )
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
                        focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155).copy(alpha = 0.7f),
                        focusedTextColor = Color(0xFFF8FAFC),
                        unfocusedTextColor = Color(0xFFF8FAFC)
                    )
                )
            }
        }

        // Listado de los números sin caja, separados por línea fina (igual que vocabulario y abecedario)
        items(filteredList, key = { it.number }) { item ->
            val count = numberCounts[item.number] ?: 0
            NumberRowItem(
                entry = item,
                count = count,
                onPlay = { onSpeakNumber(item) },
                onAddToCanvas = { onAddNumberToCanvas(item) }
            )
        }
    }
}

/**
 * Fila individual para un número (1 al 100):
 * - Pronunciación oculta bajo una flechita desplegable (igual para los 100 números).
 * - Al tocar la flechita, se expande la bandejita mostrando la pronunciación (sin caja cuando baje).
 * - Sin caja exterior ni bordes pesados (diseño minimalista).
 * - Separado por una línea fina horizontal al final.
 * - Muestra el número índice (#1 .. #100), la palabra en inglés, contador crudo y categoría.
 * - Al tocar la fila se sube al lienzo de pruebas; incluye botón de sonido ("🔊") y flechita ("⌄").
 */
@Composable
private fun NumberRowItem(
    entry: NumberEntry,
    count: Int,
    onPlay: () -> Unit,
    onAddToCanvas: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable(entry.number) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("number_card_${entry.number}")
            .clickable { onAddToCanvas() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Número de índice (1 .. 100) flotando sin caja con mayor tamaño
            Text(
                text = "${entry.number}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                modifier = Modifier.width(36.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Palabra, contador crudo y categoría flotando sin caja
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Palabra del número en inglés destacada
                    Text(
                        text = entry.word,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = if (entry.isDecade) Color(0xFFFBBF24) else Color(0xFF38BDF8)
                    )

                    // Contador crudo de uso del número (como en vocabulario y abecedario)
                    Text(
                        text = "$count",
                        fontSize = 13.5.sp,
                        fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
                        modifier = Modifier.testTag("number_count_${entry.number}")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (entry.isDecade) "Decena" else "Número",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (entry.isDecade) Color(0xFFF59E0B) else Color(0xFF64748B),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // 4. Icono de audio flotando sin caja agrandado
            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_speak_number_${entry.number}")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Pronunciar ${entry.word}",
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
                    .testTag("btn_toggle_pronunciation_${entry.number}")
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Ocultar pronunciación de ${entry.word}" else "Mostrar pronunciación de ${entry.word}",
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 38.dp, top = 4.dp, bottom = 6.dp),
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
                    text = "(${entry.phoneticSp})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = Color(0xFFF8FAFC)
                    )
                )

                // Transcripción Fonética IPA
                Text(
                    text = entry.ipa,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = Color(0xFF38BDF8)
                    )
                )
            }
        }

        // 6. Simple y fina línea divisoria entre números (exactamente como en vocabulario y abecedario)
        HorizontalDivider(
            color = Color(0xFF334155).copy(alpha = 0.4f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}
