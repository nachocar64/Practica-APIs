# Contratos de Curio

## Paquetes

Todos los paquetes se encuentran bajo `com.example.practicaapis`.

| Paquete | Responsabilidad |
| --- | --- |
| `data.remote` y `data.remote.{catFacts,extinctAnimals,jokes,mlp}` | RetrofitClient, servicios y DTO de red |
| `data`, `data.mappers`, `model` | Repositorios, conversiones y modelos de dominio |
| `util` | `Resultado<out T>`, con `Exito(datos: T)` y `Error(mensaje: String)` |
| `ui.catFacts`, `ui.extinctAnimals`, `ui.jokes`, `ui.mlp` | Screen, ViewModel, UiState y componentes de cada sección |
| `ui.home`, `ui.navigation`, `ui.theme`, `ui.components` | Home, rutas, Scaffold, tema y componentes compartidos |

## Pantallas y navegación

Las cuatro pantallas se implementan en sus paquetes de sección durante el trabajo individual.

```kotlin
// ui.catFacts
@Composable
fun CatScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    onToggleTheme: () -> Unit = {}
)

// ui.extinctAnimals
@Composable
fun ExtinctAnimalsScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    onToggleTheme: () -> Unit = {}
)

// ui.jokes
@Composable
fun JokesScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    onToggleTheme: () -> Unit = {}
)

// ui.mlp
@Composable
fun MlpScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    onToggleTheme: () -> Unit = {}
)
```

El Scaffold muestra las dos barras globales en Home y en la vista principal de cada sección. Cuando una sección abre un detalle, llama a `onDetailOpenChanged(true)` para ocultar las dos barras. Al cerrar el detalle, comunica `false` para restaurarlas. El estado de las barras pertenece a la entrada de navegación de cada sección. El Scaffold sigue aplicando los insets del sistema durante el detalle.

La sección administra su detalle y su cabecera local. Si el detalle se representa como estado local, guarda la selección con `rememberSaveable` o `SavedStateHandle`, y usa la misma acción para la flecha local y el botón Atrás de Android:

```kotlin
val closeDetail = { selectedId = null }
val isDetailOpen = selectedId != null

LaunchedEffect(isDetailOpen, onDetailOpenChanged) {
    onDetailOpenChanged(isDetailOpen)
}
BackHandler(enabled = isDetailOpen, onBack = closeDetail)
```

Con el detalle cerrado, Atrás sigue el historial del NavHost global. `onToggleTheme()` cambia el tema de toda la app y conserva la preferencia. El botón del shell está disponible en las vistas principales. Aplica `modifier` al contenedor raíz y agrega el padding propio del contenido.

Solo el integrador modifica `SectionDestination.kt` durante P-05. En ese paso conecta cada Screen y pasa `modifier` y ambos callbacks por nombre. Mientras tanto se conserva `ui.CatScreen` y las otras rutas provisionales. Para verificar la UI, cada integrante puede usar `@Preview` con `PracticaAPIsTheme`, estados de muestra y callbacks de prueba.

Rutas principales de `AppRoute` en `ui.navigation` — `home`, `cats`, `extinct`, `jokes` y `ponies`. Home usa `HomeScreen(onNavigate: (AppRoute) -> Unit, modifier: Modifier = Modifier)`.

## Recursos de texto

Cada integrante coloca sus textos en inglés en su archivo bajo `app/src/main/res/values/`. Usa el prefijo indicado para evitar colisiones en `R.string`.

| Sección | Archivo | Prefijo |
| --- | --- | --- |
| `CatScreen` | `strings_cats.xml` | `cats_` |
| `ExtinctAnimalsScreen` | `strings_extinct.xml` | `extinct_` |
| `JokesScreen` | `strings_jokes.xml` | `jokes_` |
| `MlpScreen` | `strings_mlp.xml` | `mlp_` |

Los textos globales se mantienen en `strings.xml`.

## Componentes compartidos

```kotlin
LoadingContent(
    modifier: Modifier = Modifier,
    title: String = /* loading_title */,
    message: String = /* loading_message */
)

ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = /* error_title */
)

EmptyContent(
    modifier: Modifier = Modifier,
    title: String = /* empty_title */,
    message: String = /* empty_message */,
    actionLabel: String = /* clear_search */,
    onAction: (() -> Unit)? = null
)

CurioIcon(
    symbol: CurioSymbol,
    modifier: Modifier = Modifier,
    size: TextUnit = 24.sp,
    tint: Color = LocalContentColor.current
)
```

Los valores predeterminados de texto vienen de `res/values/strings.xml`. Los símbolos son decorativos. Etiqueta el control interactivo que los contiene. Para imágenes HTTPS usa `coil.compose.AsyncImage` con un recurso local de error o fallback cuando corresponda.

## Ejemplo MVVM

El ejemplo compila como un archivo Kotlin en el proyecto. Emplea la API de gatos y nombres de ejemplo para evitar colisiones con la implementación existente.

```kotlin
package com.example.practicaapis.example

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicaapis.data.remote.RetrofitClient
import com.example.practicaapis.ui.components.EmptyContent
import com.example.practicaapis.ui.components.ErrorContent
import com.example.practicaapis.ui.components.LoadingContent
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExampleBreed(val name: String)

class ExampleRepository {
    suspend fun load(): Resultado<List<ExampleBreed>> = try {
        val response = RetrofitClient.catApiService.getCatBreeds()
        Resultado.Exito(response.data.map { ExampleBreed(it.breed) })
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        Resultado.Error("Couldn’t load breeds. Try again.")
    }
}

sealed interface ExampleUiState {
    data object Loading : ExampleUiState
    data class Ready(val breeds: List<ExampleBreed>) : ExampleUiState
    data class Failed(val message: String) : ExampleUiState
}

class ExampleViewModel : ViewModel() {
    private val repository = ExampleRepository()
    private val mutableState = MutableStateFlow<ExampleUiState>(ExampleUiState.Loading)
    val state = mutableState.asStateFlow()
    private var request: Job? = null

    init {
        reload()
    }

    fun reload() {
        if (request?.isActive == true) return
        request = viewModelScope.launch {
            mutableState.value = ExampleUiState.Loading
            mutableState.value = when (val result = repository.load()) {
                is Resultado.Exito -> ExampleUiState.Ready(result.datos)
                is Resultado.Error -> ExampleUiState.Failed(result.mensaje)
            }
        }
    }
}

@Composable
fun ExampleScreen(modifier: Modifier = Modifier) {
    val model: ExampleViewModel = viewModel()
    val state by model.state.collectAsStateWithLifecycle()
    ExampleContent(state, onRetry = model::reload, modifier = modifier)
}

@Composable
fun ExampleContent(state: ExampleUiState, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    when (state) {
        ExampleUiState.Loading -> LoadingContent(modifier)
        is ExampleUiState.Failed -> ErrorContent(state.message, onRetry, modifier)
        is ExampleUiState.Ready -> if (state.breeds.isEmpty()) {
            EmptyContent(modifier)
        } else {
            LazyColumn(modifier = modifier) {
                items(state.breeds) { breed -> Text(breed.name) }
            }
        }
    }
}
```

En la implementación de cada sección, mueve el texto específico a sus recursos de strings en inglés y separa el mapeo DTO → dominio si se reutiliza.
