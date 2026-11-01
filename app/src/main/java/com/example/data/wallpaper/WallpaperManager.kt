package com.example.data.wallpaper

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Administrador reactivo para el fondo de pantalla general de la aplicación.
 */
class WallpaperManager(private val storage: WallpaperStorage) {
  private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
  val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers.asStateFlow()

  private val _selectedWallpaper = MutableStateFlow<WallpaperItem>(WallpaperStorage.DEFAULT_NONE_WALLPAPER)
  val selectedWallpaper: StateFlow<WallpaperItem> = _selectedWallpaper.asStateFlow()

  private val _dimAlpha = MutableStateFlow(storage.getWallpaperDimAlpha())
  val dimAlpha: StateFlow<Float> = _dimAlpha.asStateFlow()

  init {
    refresh()
  }

  fun refresh() {
    val list = storage.scanAvailableWallpapers()
    _wallpapers.value = list

    // Si existe una imagen en la carpeta del proyecto ('wallpaper/'), la usa automáticamente como fondo
    val projectWp = storage.getProjectFolderWallpaper()
    if (projectWp != null) {
      _selectedWallpaper.value = projectWp
    } else {
      val savedId = storage.getSelectedWallpaperId()
      val found = list.find { 
        it.id == savedId || it.fileName == savedId || it.id.substringBeforeLast('.') == savedId 
      } ?: WallpaperStorage.DEFAULT_NONE_WALLPAPER
      _selectedWallpaper.value = found
    }
    _dimAlpha.value = storage.getWallpaperDimAlpha()
  }

  fun selectWallpaper(item: WallpaperItem) {
    _selectedWallpaper.value = item
    storage.setSelectedWallpaperId(item.id)
  }

  fun updateDimAlpha(alpha: Float = 0.83f) {
    val clamped = alpha.coerceIn(0.75f, 0.85f)
    _dimAlpha.value = clamped
    storage.setWallpaperDimAlpha(clamped)
  }

  fun applyWallpaperSettings(wallpaperId: String, dimAlpha: Float? = null) {
    storage.setSelectedWallpaperId(wallpaperId)
    val alphaToSet = (dimAlpha ?: 0.83f).coerceIn(0.75f, 0.85f)
    storage.setWallpaperDimAlpha(alphaToSet)
    _dimAlpha.value = alphaToSet
    refresh()
  }
}
