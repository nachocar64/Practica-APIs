package com.example.practicaapis.ui.extinctAnimals

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicaapis.R
import com.example.practicaapis.model.ExtinctAnimal
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.components.EmptyContent
import com.example.practicaapis.ui.components.ErrorContent
import com.example.practicaapis.ui.components.LoadingContent
import com.example.practicaapis.ui.extinctAnimals.components.ExtinctAnimalCard
import com.example.practicaapis.ui.extinctAnimals.components.ExtinctGlyph
import com.example.practicaapis.ui.extinctAnimals.components.ExtinctSymbol
import com.example.practicaapis.ui.extinctAnimals.components.FeaturedExtinctAnimalCard
import com.example.practicaapis.ui.theme.PracticaAPIsTheme

@Composable
fun ExtinctAnimalsScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onToggleTheme: () -> Unit = {}
) {
    val model: ExtinctAnimalsViewModel = viewModel()
    val state by model.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedAnimal = state.collection.firstOrNull { it.id == selectedId }
    val isDetailOpen = selectedAnimal != null
    val closeDetail: () -> Unit = { selectedId = null }

    LaunchedEffect(isDetailOpen, onDetailOpenChanged) {
        onDetailOpenChanged(isDetailOpen)
    }
    LaunchedEffect(selectedId, isDetailOpen) {
        if (selectedId != null && !isDetailOpen) closeDetail()
    }
    BackHandler(enabled = isDetailOpen, onBack = closeDetail)

    if (selectedAnimal != null) {
        ExtinctAnimalDetailScreen(selectedAnimal, onBack = closeDetail, modifier = modifier)
    } else {
        ExtinctAnimalsContent(
            state = state,
            onDiscover = model::discover,
            onAnimalClick = { selectedId = it.id },
            modifier = modifier,
            listState = listState
        )
    }
}

@Composable
internal fun ExtinctAnimalsContent(
    state: ExtinctAnimalsUiState,
    onDiscover: () -> Unit,
    onAnimalClick: (ExtinctAnimal) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val featured = state.featured
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            state = listState,
            modifier = Modifier.widthIn(max = 640.dp).fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 24.dp)
        ) {
            item {
                Text(
                    stringResource(
                        if (featured == null) R.string.extinct_museum_state_title else R.string.extinct_museum_title
                    ),
                    modifier = Modifier.padding(bottom = 16.dp).semantics { heading() },
                    style = typography.displaySmall.copy(fontSize = 36.sp, lineHeight = 44.sp)
                )
            }

            if (featured == null) {
                item { InitialMuseumContent(state.discoveryStatus, onDiscover) }
            } else {
                item {
                    FeaturedExtinctAnimalCard(featured) { onAnimalClick(featured) }
                }
                item {
                    DiscoveryButton(
                        state.discoveryStatus,
                        onDiscover,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                if (state.discoveryStatus == DiscoveryStatus.Error || state.discoveryStatus == DiscoveryStatus.Empty) {
                    item {
                        val isError = state.discoveryStatus == DiscoveryStatus.Error
                        Text(
                            stringResource(
                                if (isError) R.string.extinct_discovery_error else R.string.extinct_no_new_discovery
                            ),
                            modifier = Modifier.padding(top = 12.dp).semantics { liveRegion = LiveRegionMode.Polite },
                            color = if (isError) colors.error else colors.onSurfaceVariant,
                            style = typography.bodyMedium
                        )
                    }
                }
                item {
                    Column(Modifier.padding(top = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            stringResource(R.string.extinct_collection_title),
                            modifier = Modifier.semantics { heading() },
                            style = typography.titleMedium.copy(fontSize = 20.sp, lineHeight = 24.sp)
                        )
                        Text(
                            stringResource(R.string.extinct_collection_description),
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                itemsIndexed(state.collection, key = { _, animal -> animal.id }) { index, animal ->
                    ExtinctAnimalCard(
                        animal = animal,
                        isFirst = index == 0,
                        isLast = index == state.collection.lastIndex,
                        onClick = { onAnimalClick(animal) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InitialMuseumContent(status: DiscoveryStatus, onDiscover: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            stringResource(R.string.extinct_museum_label),
            style = typography.labelMedium,
            color = colors.primary
        )
        when (status) {
            DiscoveryStatus.Loading -> LoadingContent(
                title = stringResource(R.string.extinct_loading_title),
                message = stringResource(R.string.extinct_loading_message)
            )
            DiscoveryStatus.Retrying -> MuseumRetryContent()
            DiscoveryStatus.Error -> ErrorContent(
                message = stringResource(R.string.extinct_discovery_error),
                onRetry = onDiscover
            )
            DiscoveryStatus.Empty, DiscoveryStatus.Ready -> EmptyContent(
                title = stringResource(R.string.extinct_discovery_empty_title),
                message = stringResource(R.string.extinct_discovery_empty_message),
                actionLabel = stringResource(R.string.extinct_discover_first),
                onAction = onDiscover
            )
        }
        Text(
            stringResource(
                when (status) {
                    DiscoveryStatus.Error -> R.string.extinct_error_footer
                    DiscoveryStatus.Retrying -> R.string.extinct_retry_footer
                    else -> R.string.extinct_independent_loading
                }
            ),
            style = typography.bodyMedium,
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun MuseumRetryContent() {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Surface(
        Modifier.fillMaxWidth().heightIn(min = 440.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            Modifier.padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ExtinctSymbol(ExtinctGlyph.Sync, size = 56.sp, tint = colors.primary)
            Text(stringResource(R.string.extinct_retry_title), style = typography.headlineSmall)
            Text(stringResource(R.string.extinct_retry_message), color = colors.onSurfaceVariant)
            listOf(1f, 0.81f, 0.56f).forEach { width ->
                Box(
                    Modifier.fillMaxWidth(width).height(16.dp)
                        .background(colors.outline.copy(alpha = 0.45f), MaterialTheme.shapes.extraSmall)
                )
            }
            Text(
                stringResource(R.string.extinct_retrying),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                color = colors.primary,
                style = typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DiscoveryButton(status: DiscoveryStatus, onDiscover: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onDiscover,
        enabled = !status.inProgress,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp)
    ) {
        if (status.inProgress) {
            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
            CurioIcon(CurioSymbol.ArrowForward, size = 20.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            stringResource(
                when (status) {
                    DiscoveryStatus.Loading -> R.string.extinct_discovering
                    DiscoveryStatus.Retrying -> R.string.extinct_retrying
                    DiscoveryStatus.Error -> R.string.extinct_retry
                    DiscoveryStatus.Ready, DiscoveryStatus.Empty -> R.string.extinct_discover_another
                }
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private val previewDodo = ExtinctAnimal(
    binomialName = "Raphus cucullatus",
    commonName = "Dodo",
    imageUrl = null,
    location = "Mauritius",
    lastRecord = "1662",
    description = "A flightless bird that lived on the island of Mauritius.",
    wikipediaUrl = "https://en.wikipedia.org/wiki/Dodo"
)

@Preview(name = "Museum light", widthDp = 412, heightDp = 760)
@Preview(name = "Museum dark", widthDp = 412, heightDp = 760, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MuseumPreview() {
    PracticaAPIsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtinctAnimalsContent(
                state = ExtinctAnimalsUiState(featured = previewDodo, collection = listOf(previewDodo)),
                onDiscover = {},
                onAnimalClick = {}
            )
        }
    }
}

@Preview(name = "Error", widthDp = 412, heightDp = 760)
@Composable
private fun DiscoveryErrorPreview() {
    PracticaAPIsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtinctAnimalsContent(
                state = ExtinctAnimalsUiState(discoveryStatus = DiscoveryStatus.Error),
                onDiscover = {},
                onAnimalClick = {}
            )
        }
    }
}

@Preview(name = "Retrying", widthDp = 412, heightDp = 760)
@Composable
private fun MuseumRetryPreview() {
    PracticaAPIsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtinctAnimalsContent(
                state = ExtinctAnimalsUiState(discoveryStatus = DiscoveryStatus.Retrying),
                onDiscover = {},
                onAnimalClick = {}
            )
        }
    }
}

@Preview(name = "Detail without image", widthDp = 412, heightDp = 850, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AnimalDetailPreview() {
    PracticaAPIsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtinctAnimalDetailScreen(previewDodo, onBack = {})
        }
    }
}
