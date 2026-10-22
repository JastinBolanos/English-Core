# Ajustes y Copia de Seguridad JSON (Backup & Restore)

Implementación de un botón de engranaje minimalista flotante ubicado al costado de "Frases" (pegado al extremo derecho) que abre una bandeja flotante de Ajustes y Respaldo. Esta bandeja permite exportar todo el progreso del usuario (puntajes, palabras usadas, notas locales, borradores) en un archivo `.json` y restaurarlo mediante el selector de documentos nativo de Android (Storage Access Framework).

## User Review & Critical Decisions

> [!IMPORTANT]
> Confirmaciones recopiladas durante la fase de aclaración:
> - **Estilo del engranaje:** Flotante minimalista, sin caja contenedora, alineado al extremo derecho de la fila de pestañas (Vocabulario / Frases).
> - **Acciones iniciales en la bandeja:** Botón para "Exportar copia JSON" (guardar) y botón para "Restaurar progreso desde archivo" (buscar y seleccionar `.json` con SAF).
> - **Estructura modular:** Primero dejamos establecida la bandeja flotante y el acceso en el UI, preparando los contratos de serialización e importación/exportación para conectar todos los módulos de datos de la app.

## 1. Overview & Core Concept

- **Qué hace:** Proporciona al usuario un punto de acceso directo para gestionar sus datos, permitiéndole generar una copia de respaldo portátil de su aprendizaje (`backup_progreso.json`) y recuperarla en cualquier momento o dispositivo sin perder su historial, puntuaciones ni notas.
- **Público objetivo:** Estudiantes de inglés que desean asegurar su progreso diario acumulado frente a cambios de dispositivo, reinstalaciones o desinstalaciones.
- **Valor clave:** Cero pérdida de datos y total soberanía del usuario sobre sus notas y progreso mediante formato abierto JSON.

## 2. User Experience & Visual Design

### Flujo de interacción
1. **Acceso:** En la barra de pestañas, mientras "Vocabulario" y "Frases" permanecen perfectamente centrados, el icono de engranaje (`Settings`) se ubica flotante en el margen derecho.
2. **Apertura de la bandeja:** Al pulsar el engranaje, emerge una bandeja flotante semitransparente con desenfoque de fondo y borde celeste neón (siguiendo el lenguaje visual de la app).
3. **Contenido de la bandeja:**
   - **Cabecera:** Título "Ajustes y Respaldo" con botón de cierre (✕).
   - **Tarjeta de estado actual:** Resumen en vivo de datos detectados (ej. Palabras usadas con trofeo, cantidad de notas locales guardadas, fecha del día).
   - **Botón Exportar JSON:** Lanza el guardado de documento (`CreateDocument`) para que el usuario elija dónde guardar su archivo `.json`.
   - **Botón Restaurar JSON:** Lanza el selector de archivos (`OpenDocument`) para buscar un archivo de respaldo y restaurar la información al instante con confirmación visual.
4. **Cierre:** Al pulsar fuera de la bandeja o en el botón de cierre, se oculta suavemente.

### Identidad Visual y Estilo
- **Botón Engranaje:** Icono `Icons.Outlined.Settings` en color celeste neón (`Color(0xFF38BDF8)`), con efecto ripple sutil y sin caja para mantener la estética flotante acordada.
- **Bandeja Flotante:** Fondo oscuro slate `Color(0xFF0F172A)` al 95% de opacidad, borde `Color(0xFF38BDF8)` con radio de 18.dp, sombras de elevación y diseño adaptable centrado.

## 3. Key Product Decisions & Trade-Offs

- **Storage Access Framework (SAF) vs Almacenamiento Privado Exclusivo:**
  - *Decisión:* Usar los contratos nativos de Jetpack Compose `ActivityResultContracts.CreateDocument` y `ActivityResultContracts.OpenDocument`.
  - *Por qué:* Permite al usuario guardar el archivo en su carpeta de Descargas, Google Drive o enviarlo por mensajería, garantizando que el archivo sobreviva a la desinstalación de la aplicación.
- **Serialización en JSON:**
  - *Decisión:* Modelo de datos estructurado `AppBackupData` que consolida las preferencias, progreso de palabras, contador del trofeo y notas locales en un único esquema legible y versionable (`version = 1`).

## 4. Technical Architecture & Data Strategy

### Diagrama de Componentes y Flujo

```
┌─────────────────────────────────────────────────────────────┐
│                       VocabularyScreen                      │
│                                                             │
│   [Vocabulario]     [Frases]               [⚙ Settings]     │
└───────────────────────────────────────────────────┬─────────┘
                                                    │ Abre
                                                    ▼
┌─────────────────────────────────────────────────────────────┐
│                FloatingSettingsBackupTray                   │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Resumen de Progreso:                                  │  │
│  │ • Palabras usadas: N  • Notas guardadas: N            │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  [📥 Exportar Respaldo (.json)]   [📂 Restaurar Archivo]    │
└───────────────┬───────────────────────────────┬─────────────┘
                │                               │
                │ CreateDocument                │ OpenDocument
                ▼                               ▼
       Android SAF File Picker         Android SAF File Picker
       (Guarda progreso.json)          (Lee y restaura progreso)
```

### Estructura del Archivo JSON de Respaldo (`AppBackupData`)
```json
{
  "version": 1,
  "exportedAt": "2026-10-21T21:40:00",
  "usedWordsCount": 1,
  "usedWordsList": ["hello"],
  "localNotes": [
    { "id": "1", "content": "Anotación de gramática..." }
  ],
  "draftWords": []
}
```

### Archivos a crear y modificar
1. `app/src/main/java/com/example/presentation/settings/FloatingSettingsBackupTray.kt`: Componente Composable de la bandeja flotante con vista previa de datos, botones de exportar/restaurar e integración SAF.
2. `app/src/main/java/com/example/presentation/vocabulary/VocabularyScreen.kt`:
   - Integración del botón de engranaje flotante en la fila de pestañas empujado al borde derecho mediante `Box` con `align(Alignment.CenterEnd)`.
   - Control de visibilidad `isSettingsTrayOpen`.
   - Conexión de los launchers de SAF para exportación e importación de `.json`.
