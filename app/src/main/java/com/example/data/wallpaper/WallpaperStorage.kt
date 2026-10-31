package com.example.data.wallpaper

import android.content.Context
import java.util.Locale

/**
 * Modelo de datos para un fondo de pantalla seleccionable en la aplicación.
 */
data class WallpaperItem(
  val id: String,
  val title: String,
  val fileName: String? = null,
  val assetPath: String? = null,
  val isDefaultNone: Boolean = false
)

/**
 * Almacenamiento y escaneo de fondos de pantalla.
 * Escanea dinámicamente la carpeta especial 'app/src/main/assets/wallpapers/' para reconocer
 * de inmediato cualquier archivo de imagen (.jpg, .jpeg, .png, .webp) colocado allí.
 */
class WallpaperStorage(private val context: Context) {
  private val prefs = context.getSharedPreferences("english_wallpaper_prefs", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_SELECTED_WALLPAPER_ID = "selected_wallpaper_id"
    private const val KEY_WALLPAPER_DIM_ALPHA = "wallpaper_dim_alpha"
    const val ASSET_WALLPAPERS_DIR = "wallpapers"
    const val PROJECT_WALLPAPER_DIR = "wallpaper"

    val DEFAULT_NONE_WALLPAPER = WallpaperItem(
      id = "none",
      title = "Tema Predeterminado (Oscuro)",
      fileName = null,
      assetPath = null,
      isDefaultNone = true
    )
  }

  fun getSelectedWallpaperId(): String {
    return prefs.getString(KEY_SELECTED_WALLPAPER_ID, "none") ?: "none"
  }

  fun setSelectedWallpaperId(id: String) {
    prefs.edit().putString(KEY_SELECTED_WALLPAPER_ID, id).apply()
  }

  fun getWallpaperDimAlpha(): Float {
    return 0.83f
  }

  fun setWallpaperDimAlpha(alpha: Float = 0.83f) {
    prefs.edit().putFloat(KEY_WALLPAPER_DIM_ALPHA, 0.83f).apply()
  }

  /**
   * Detecta y devuelve la imagen ubicada en la carpeta del proyecto 'app/src/main/assets/wallpaper/'.
   * Si el usuario cambia o reemplaza la imagen dentro de esa carpeta, la aplicación la carga automáticamente.
   */
  fun getProjectFolderWallpaper(): WallpaperItem? {
    try {
      val assetManager = context.assets
      val fileList = assetManager.list(PROJECT_WALLPAPER_DIR) ?: emptyArray()
      val validExtensions = setOf("jpg", "jpeg", "png", "webp")
      val imageFiles = fileList.filter { name ->
        val ext = name.substringAfterLast('.', "").lowercase(Locale.ROOT)
        validExtensions.contains(ext)
      }.sorted()

      val firstImage = imageFiles.firstOrNull()
      if (firstImage != null) {
        return WallpaperItem(
          id = "project_wallpaper_$firstImage",
          title = "Fondo del Proyecto ($firstImage)",
          fileName = firstImage,
          assetPath = "$PROJECT_WALLPAPER_DIR/$firstImage",
          isDefaultNone = false
        )
      }
    } catch (_: Exception) {
      // Manejo seguro ante cualquier error
    }
    return null
  }

  /**
   * Escanea de forma dinámica la carpeta especial 'app/src/main/assets/wallpapers/'
   * reconociendo cualquier archivo de imagen (.jpg, .jpeg, .png, .webp) que el usuario coloque ahí.
   */
  fun scanAvailableWallpapers(): List<WallpaperItem> {
    val items = mutableListOf<WallpaperItem>()

    // Opción para restablecer a tema estándar oscuro
    items.add(DEFAULT_NONE_WALLPAPER)

    try {
      val assetManager = context.assets
      val fileList = assetManager.list(ASSET_WALLPAPERS_DIR) ?: emptyArray()

      val validExtensions = setOf("jpg", "jpeg", "png", "webp")

      val imageFiles = fileList.filter { name ->
        val ext = name.substringAfterLast('.', "").lowercase(Locale.ROOT)
        validExtensions.contains(ext)
      }.sorted()

      for (fileName in imageFiles) {
        val baseName = fileName.substringBeforeLast('.')
        val humanTitle = formatWallpaperTitle(baseName)
        items.add(
          WallpaperItem(
            id = fileName,
            title = humanTitle,
            fileName = fileName,
            assetPath = "$ASSET_WALLPAPERS_DIR/$fileName",
            isDefaultNone = false
          )
        )
      }
    } catch (_: Exception) {
      // Manejo seguro ante cualquier error de lectura de assets
    }

    return items
  }

  private fun formatWallpaperTitle(rawName: String): String {
    val clean = rawName.replace(Regex("^img[0-9]\\.wallspic\\.com-", RegexOption.IGNORE_CASE), "")
      .replace(Regex("^wallspic-", RegexOption.IGNORE_CASE), "")
      .replace(Regex("-[0-9]+x[0-9]+$", RegexOption.IGNORE_CASE), "")

    return when (clean.lowercase(Locale.ROOT)) {
      "great_dane-mountain-cat-pet_rock-rock" -> "Husky en las Rocas de Montaña"
      "canyon-antelope_canyon-natural_environment-bedrock-natural_landscape" -> "Cañón del Antílope"
      "dawn-sun-sunset-afterglow-evening" -> "Amanecer Dorado y Sol"
      "sunlight-evening-sunset-horizon-morning" -> "Horizonte al Atardecer"
      "light-grass-morning-sky-cloud" -> "Pradera Matutina y Cielo"
      "moon-ecoregion-natural_environment-natural_landscape-landscape" -> "Paisaje Lunar Nocturno"
      "branch-winter-tree_stump-trunk-natural_environment" -> "Tronco de Bosque y Musgo"
      "astronomical_object-planet-atmosphere-exoplanet-universe" -> "Planeta y Atmósfera Espacial"
      "outer_space-astronomy-moon-satellite-sky" -> "Luna en el Espacio Profundo"
      "outer_space-milky_way-star-night-galaxy" -> "Vía Láctea y Galaxia de Estrellas"
      "star-dawn-astronomy-atmosphere-night_sky" -> "Cielo Nocturno Estrellado"
      "atmosphere-earth-wisgoon_social_network-planet-nature" -> "Tierra desde el Espacio"
      "available-mountainous_landforms-mountain-hill-highland" -> "Cordillera de Montañas"
      "black-abstract-material_design-geometric-architecture" -> "Geometría Negra Abstracta"
      "tree-boston-cars-plant-tire" -> "Árboles de Otoño"
      "freeway-road-free-philadelphia_freeway-roc_nation" -> "Autopista y Carretera Abierta"
      "64_bit_computing-windows_10-water-azure-plant" -> "Agua Azul Cristalina y Naturaleza"
      "ios_26-idownloadblog-tiktok-terrain-geological_formation" -> "Formación Geológica"
      "husky_mountain_cliffs" -> "Husky en las Rocas (Montaña)"
      "cosmic_nebula" -> "Galaxia Cósmica (Nebulosa)"
      "sunset_mountains" -> "Atardecer en las Cumbres"
      else -> {
        clean.replace('_', ' ')
          .replace('-', ' ')
          .split(' ')
          .filter { it.isNotBlank() }
          .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
          }
      }
    }
  }
}
