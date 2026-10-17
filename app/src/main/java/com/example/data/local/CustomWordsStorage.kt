package com.example.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

data class CustomWordRecord(
  val rank: Int,
  val word: String,
  val partOfSpeech: String,
  val spanishTranslation: String = "",
  val microExample: String = "",
  val useCount: Int = 0
)

class CustomWordsStorage(private val context: Context) {

  private val jsonFile: File
    get() = File(context.filesDir, "custom_words.json")

  @Synchronized
  fun loadCustomWords(): List<CustomWordRecord> {
    val file = jsonFile
    if (!file.exists()) return emptyList()

    return try {
      val content = file.readText(StandardCharsets.UTF_8)
      if (content.isBlank()) return emptyList()
      val jsonArray = JSONArray(content)
      val list = mutableListOf<CustomWordRecord>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          CustomWordRecord(
            rank = obj.optInt("rank", 2810),
            word = obj.optString("word", "").trim(),
            partOfSpeech = obj.optString("partOfSpeech", "sustantivo"),
            spanishTranslation = obj.optString("spanishTranslation", ""),
            microExample = obj.optString("microExample", ""),
            useCount = obj.optInt("useCount", 0)
          )
        )
      }
      list.filter { it.word.isNotBlank() }
    } catch (_: Exception) {
      emptyList()
    }
  }

  @Synchronized
  fun saveCustomWord(record: CustomWordRecord) {
    try {
      val currentList = loadCustomWords().toMutableList()
      // Remove any duplicate by lemma or rank to prevent conflicts
      currentList.removeAll {
        it.rank == record.rank || it.word.equals(record.word, ignoreCase = true)
      }
      currentList.add(record)
      currentList.sortBy { it.rank }

      val jsonArray = JSONArray()
      for (item in currentList) {
        val obj = JSONObject().apply {
          put("rank", item.rank)
          put("word", item.word)
          put("partOfSpeech", item.partOfSpeech)
          put("spanishTranslation", item.spanishTranslation)
          put("microExample", item.microExample)
          put("useCount", item.useCount)
        }
        jsonArray.put(obj)
      }

      jsonFile.writeText(jsonArray.toString(2), StandardCharsets.UTF_8)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @Synchronized
  fun deleteCustomWord(rank: Int) {
    try {
      val currentList = loadCustomWords().filter { it.rank != rank }
      val jsonArray = JSONArray()
      for (item in currentList) {
        val obj = JSONObject().apply {
          put("rank", item.rank)
          put("word", item.word)
          put("partOfSpeech", item.partOfSpeech)
          put("spanishTranslation", item.spanishTranslation)
          put("microExample", item.microExample)
          put("useCount", item.useCount)
        }
        jsonArray.put(obj)
      }
      jsonFile.writeText(jsonArray.toString(2), StandardCharsets.UTF_8)
    } catch (_: Exception) {}
  }
}
