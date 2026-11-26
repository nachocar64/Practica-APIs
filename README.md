# Curio

Curio es una aplicación académica para Android que reúne cuatro catálogos y experiencias de descubrimiento. Home presenta las cuatro secciones en una cuadrícula Bento. La aplicación usa inglés y conserva la preferencia de tema claro u oscuro.

## Funcionalidades y servicios

| Sección | Funcionalidad | API |
| --- | --- | --- |
| Cats | Catálogo completo de razas, búsqueda sin distinción de mayúsculas, filtros de pelaje y fichas de raza | [Cat Facts](https://catfact.ninja/) |
| Extinct | Descubrimiento de animales, colección de sesión y ficha de historia natural | [Extinct Animals API](https://cheba-apis.vercel.app/) |
| Jokes | Categorías, chistes de una o dos partes y revelación del remate | [JokeAPI](https://v2.jokeapi.dev/) |
| Ponies | Catálogo paginado, búsqueda por nombre o ID, detalle y galería | [Pony API](https://ponyapi.net/) |

## Arquitectura

Retrofit y Gson convierten las respuestas HTTP en DTOs. Los repositorios reutilizan `RetrofitClient`, mapean los DTOs al dominio y devuelven `Resultado`. Los ViewModels publican `StateFlow` y ejecutan las solicitudes en `viewModelScope`. Compose observa el estado con `collectAsStateWithLifecycle()`. La navegación utiliza un único `Scaffold` y `NavHost`. Las fichas tienen cabecera local y comunican al Scaffold cuándo ocultar o restaurar las barras globales.

Paquetes principales bajo `com.example.practicaapis`:

| Paquete | Responsabilidad |
| --- | --- |
| `data.remote` | Cliente común, interfaces Retrofit y DTOs |
| `data`, `data.mappers`, `model`, `util` | Repositorios, mapeadores, modelos y `Resultado` |
| `ui.catFacts`, `ui.extinctAnimals`, `ui.jokes`, `ui.mlp` | Pantallas, estados y ViewModels por sección |
| `ui.home`, `ui.navigation`, `ui.theme`, `ui.components` | Home, navegación, tema y componentes compartidos |

Tecnologías principales: Kotlin, Jetpack Compose, Material 3 Expressive, Navigation Compose, Lifecycle, corrutinas, Retrofit, Gson, OkHttp y Coil. [Contratos de integración](docs/contratos.md)

## Compilar e instalar

Se necesita Android Studio con soporte para Android Gradle Plugin 9.5.2 y Android SDK 37. Usa el JDK integrado del IDE como Gradle JDK. La app admite Android API 24 o superior. Para las pruebas instrumentadas se necesita un emulador o dispositivo conectado. La descarga de dependencias y el consumo de APIs requieren Internet.

Desde PowerShell, en la raíz del repositorio:

```powershell
.\gradlew.bat :app:assembleDebug --console=plain
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

También puedes seleccionar el módulo `app` en Android Studio y ejecutarlo con Run.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:connectedDebugAndroidTest --console=plain
.\gradlew.bat :app:lintDebug --console=plain
```

## Diseño y limitaciones

- Los recursos decorativos de Cats se identifican como ilustraciones locales. Cat Facts no proporciona fotografías de raza.
- La colección de Extinct vive en la sesión de la sección. El contenido y las imágenes dependen del proveedor. Los campos e imágenes ausentes conservan una presentación legible.
- En la auditoría P-05, seis URLs representativas de imágenes de Pony API devolvieron HTTP 403 desde el entorno de pruebas. Coil prueba las alternativas del mismo personaje y muestra un marcador si todas fallan. Los datos del catálogo, búsqueda y detalle permanecen disponibles.
- Las imágenes de Previews de MLP pertenecen exclusivamente a las composiciones de vista previa. Las imágenes en ejecución proceden de los registros de la API.
- Material Symbols se distribuye con su licencia en `app/src/main/assets/curio/material_symbols_license.txt`. [Diseño oficial de Curio en Figma](https://www.figma.com/design/aUJ2V3lz1E4lCururEu1FP/Sin-t%C3%ADtulo?node-id=0-1)

## Resultados de validación P-05

- Compilación de referencia y compilación de integración correctas.
- 17 pruebas unitarias y 5 instrumentadas aprobadas en el emulador S23, sin fallos en la ejecución final de la suite.
- Sondas reales: Cat Facts devolvió 98 razas en dos páginas, Extinct devolvió un descubrimiento, JokeAPI devolvió formatos single y twopart en inglés con todas las banderas excluidas en falso, y Pony API devolvió dos páginas de 20 y resultados de búsqueda por nombre e ID.

- `lintDebug` terminó con 0 errores y 27 advertencias de mantenimiento. El informe se genera en `app/build/reports/lint-results-debug.html`.

| Recorrido en el emulador S23 | Resultado |
| --- | --- |
| A · Cats | Bento, búsqueda combinada con Curly, detalle de LaPerm desde la segunda página, Atrás y Home |
| B · Extinct | Bento, descubrimiento, detalle, regreso con la flecha local y nuevo descubrimiento |
| C · Jokes | Bento, Programming, revelación del remate, Another joke y barras visibles |
| D · Ponies | Bento, segunda página, búsqueda por ID, detalle, selección en galería, Atrás y restauración del catálogo |
| E · Navegación | Cinco destinos desde la barra inferior y destino seleccionado |
| F · Tema | Claro y oscuro, navegación entre secciones y preferencia persistente tras reiniciar |
| G · Conexión | Error sin red, reconexión y reintento exitoso en Cats |
| H · Rotación | Detalle de Cats y barras ocultas en horizontal, seguido de regreso a la búsqueda filtrada |

La revisión visual cubrió los nodos de referencia y Previews representativas. La aplicación usa controles nativos y datos variables. Los indicadores de las listas de Cats usan flechas y las imágenes remotas ausentes en Ponies muestran marcadores, por lo que algunas vistas difieren de los ejemplos del diseño. El recorrido de rotación se verificó en Cats. Las demás secciones se comprobaron con el emulador en orientación normal.
