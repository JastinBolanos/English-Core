package com.example.data.youtube

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class YouTubeVideoResult(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val channel: String = ""
)

object YouTubeSearchService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    // Base de respaldo instantánea con miniaturas oficiales de YouTube
    private val fallbackCatalog = listOf(
        YouTubeVideoResult("84kLpP7j9iQ", "Conversación Casual en Inglés - Diálogos Reales", "https://img.youtube.com/vi/84kLpP7j9iQ/mqdefault.jpg", "Inglés Práctico"),
        YouTubeVideoResult("4uRCE3r_0oY", "Slang Americano y Frases Callejeras Más Comunes", "https://img.youtube.com/vi/4uRCE3r_0oY/mqdefault.jpg", "American Slang"),
        YouTubeVideoResult("d93m4s_Gf5A", "Top 100 Palabras Más Usadas en Inglés con Pronunciación", "https://img.youtube.com/vi/d93m4s_Gf5A/mqdefault.jpg", "Vocabulario HUD"),
        YouTubeVideoResult("fJ9rUzIMcZQ", "Conectores Gramaticales para Unir Ideas Fluidamente", "https://img.youtube.com/vi/fJ9rUzIMcZQ/mqdefault.jpg", "Gramática Fácil"),
        YouTubeVideoResult("7u0_yC0aYF8", "Connected Speech: Habla Rápida y Reducciones (gonna, wanna)", "https://img.youtube.com/vi/7u0_yC0aYF8/mqdefault.jpg", "Pronunciación Pro"),
        YouTubeVideoResult("5p3q2Ff64aY", "Los 20 Verbos de Acción Más Frecuentes en Inglés", "https://img.youtube.com/vi/5p3q2Ff64aY/mqdefault.jpg", "Motor del Idioma"),
        YouTubeVideoResult("4N0jA_hJ3dE", "Preguntas Cotidianas para Iniciar Charlas Sociales", "https://img.youtube.com/vi/4N0jA_hJ3dE/mqdefault.jpg", "Conversación"),
        YouTubeVideoResult("k2zdfV3xZuo", "Phrasal Verbs que Sí Usan los Nativos en la Vida Real", "https://img.youtube.com/vi/k2zdfV3xZuo/mqdefault.jpg", "Phrasal Verbs"),
        YouTubeVideoResult("9dSNK2n9jcY", "English Pronunciation Training - Master English Sounds", "https://img.youtube.com/vi/9dSNK2n9jcY/mqdefault.jpg", "Accent Master"),
        YouTubeVideoResult("tpN9CPwZ-oE", "How to Pronounce ALL English Sounds (American Accent)", "https://img.youtube.com/vi/tpN9CPwZ-oE/mqdefault.jpg", "Speak Clear"),
        YouTubeVideoResult("QxQUapA-2w4", "Complete Guide to English Pronunciation & Phonetics", "https://img.youtube.com/vi/QxQUapA-2w4/mqdefault.jpg", "Oxford English"),
        YouTubeVideoResult("q7SAt9h4sd0", "American English Pronunciation Secrets", "https://img.youtube.com/vi/q7SAt9h4sd0/mqdefault.jpg", "Native Flow"),
        YouTubeVideoResult("2rF1TEnkNfU", "English for Beginners: Simple Sentences & Everyday Talk", "https://img.youtube.com/vi/2rF1TEnkNfU/mqdefault.jpg", "Easy English"),
        YouTubeVideoResult("n4NVPg2kHv4", "Improve Your Accent & Speak Clearly in Everyday English", "https://img.youtube.com/vi/n4NVPg2kHv4/mqdefault.jpg", "Fluency Hub")
    )

    suspend fun searchVideos(query: String): List<YouTubeVideoResult> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext emptyList()

        // 1. Si el usuario ingresó un enlace o un ID directo de 11 caracteres
        val extractedId = extractVideoId(trimmed)
        if (extractedId != null) {
            return@withContext listOf(
                YouTubeVideoResult(
                    videoId = extractedId,
                    title = "Video seleccionado: $extractedId",
                    thumbnailUrl = "https://img.youtube.com/vi/$extractedId/mqdefault.jpg",
                    channel = "YouTube Directo"
                )
            )
        }

        // 2. Búsqueda en vivo en YouTube vía scraping seguro de resultados HTML
        try {
            val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://www.youtube.com/results?search_query=$encodedQuery"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept-Language", "es,en;q=0.9")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val html = response.body?.string().orEmpty()
                val liveResults = parseYouTubeHtml(html)
                if (liveResults.isNotEmpty()) {
                    return@withContext liveResults.take(15)
                }
            }
        } catch (_: Exception) {
            // Silencioso; continúa con el catálogo local y coincidencias dinámicas
        }

        // 3. Fallback inteligente: buscar en catálogo local que contenga las palabras clave
        val qLower = trimmed.lowercase()
        val localMatches = fallbackCatalog.filter {
            it.title.lowercase().contains(qLower) ||
            it.channel.lowercase().contains(qLower) ||
            it.videoId.lowercase().contains(qLower)
        }

        if (localMatches.isNotEmpty()) {
            return@withContext localMatches
        }

        // 4. Si no hubo coincidencia exacta, proveer el catálogo de lecciones útiles
        return@withContext fallbackCatalog.take(6)
    }

    private fun parseYouTubeHtml(html: String): List<YouTubeVideoResult> {
        val results = mutableListOf<YouTubeVideoResult>()
        val seenIds = mutableSetOf<String>()

        // Patrón para extraer videoId y title dentro de los bloques de videoRenderer
        val pattern = Pattern.compile("\"videoRenderer\":\\{.*?\"videoId\":\"([a-zA-Z0-9_-]{11})\".*?\"title\":\\{.*?\"text\":\"(.*?)\"\\}")
        val matcher = pattern.matcher(html)

        while (matcher.find()) {
            val vid = matcher.group(1) ?: continue
            val rawTitle = matcher.group(2) ?: "Video de YouTube"
            if (seenIds.add(vid)) {
                val cleanTitle = cleanTitle(rawTitle)
                results.add(
                    YouTubeVideoResult(
                        videoId = vid,
                        title = cleanTitle,
                        thumbnailUrl = "https://img.youtube.com/vi/$vid/mqdefault.jpg",
                        channel = "YouTube"
                    )
                )
            }
        }

        return results
    }

    private fun cleanTitle(title: String): String {
        return title
            .replace("\\u0026", "&")
            .replace("\\\"", "\"")
            .replace("\\'", "'")
            .replace("&#39;", "'")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
    }

    fun extractVideoId(input: String): String? {
        val clean = input.trim()
        if (clean.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            return clean
        }
        if (clean.contains("v=")) {
            val after = clean.substringAfter("v=")
            val id = after.substringBefore("&").substringBefore("?")
            if (id.length == 11) return id
        }
        if (clean.contains("youtu.be/")) {
            val after = clean.substringAfter("youtu.be/")
            val id = after.substringBefore("?").substringBefore("/")
            if (id.length == 11) return id
        }
        if (clean.contains("embed/")) {
            val after = clean.substringAfter("embed/")
            val id = after.substringBefore("?").substringBefore("/")
            if (id.length == 11) return id
        }
        return null
    }
}
