package com.example.practicaapis.ui.mlp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicaapis.R
import com.example.practicaapis.model.Pony
import com.example.practicaapis.ui.components.*
import com.example.practicaapis.ui.mlp.components.PonyCard

@Composable
fun MlpScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onToggleTheme: () -> Unit = {}
) {
    val model: MlpViewModel = viewModel()
    val state by model.state.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
    val catalogGrid = rememberLazyGridState()
    val searchGrid = rememberLazyGridState()
    val isDetailOpen = selectedId != null
    val detailModel: MlpDetailViewModel? = if (isDetailOpen) viewModel() else null
    val closeDetail = {
        detailModel?.close()
        selectedId = null
    }

    LaunchedEffect(isDetailOpen, onDetailOpenChanged) {
        onDetailOpenChanged(isDetailOpen)
    }
    BackHandler(enabled = isDetailOpen, onBack = closeDetail)

    val id = selectedId
    if (id != null && detailModel != null) {
        MlpDetailScreen(id, detailModel, onBack = closeDetail, modifier = modifier)
    } else {
        MlpCatalogContent(
            state = state,
            onQueryChange = model::changeQuery,
            onPonyClick = { selectedId = it.id },
            onRetry = model::retry,
            onLoadMore = model::loadMore,
            modifier = modifier,
            gridState = if (state.isSearching) searchGrid else catalogGrid
        )
    }
}

@Composable
internal fun MlpCatalogContent(
    state: MlpUiState,
    onQueryChange: (String) -> Unit,
    onPonyClick: (Pony) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState()
) {
    val showStateHeading = state.isLoading || state.hasError || state.ponies.isEmpty()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 136.dp),
            state = gridState,
            modifier = Modifier.widthIn(max = 840.dp).fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(
                        stringResource(if (showStateHeading) R.string.mlp_state_heading else R.string.mlp_heading),
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 36.sp, lineHeight = 44.sp)
                    )
                    if (showStateHeading) {
                        Text(
                            stringResource(R.string.mlp_state_label),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    if (state.isSearching || !state.loadingCatalog && !state.catalogError) {
                        PonySearchField(state.query, onQueryChange)
                    }
                    if (!state.isLoading && !state.hasError && state.ponies.isNotEmpty()) {
                        Text(
                            stringResource(if (state.isSearching) R.string.mlp_search_results else R.string.mlp_catalog),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            when {
                state.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) { LoadingContent() }
                state.hasError -> item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorContent(stringResource(R.string.mlp_catalog_error), onRetry)
                }
                state.ponies.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyContent(
                        title = stringResource(R.string.mlp_empty_title),
                        message = stringResource(
                            if (state.isSearching) R.string.mlp_no_matches else R.string.mlp_no_characters
                        ),
                        onAction = if (state.isSearching) ({ onQueryChange("") }) else null
                    )
                }
                else -> items(state.ponies, key = Pony::id) { pony ->
                    PonyCard(pony, onClick = { onPonyClick(pony) })
                }
            }
            if (!state.isSearching && state.catalog.isNotEmpty() && state.hasMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadMoreContent(state.loadingMore, state.pageError, onLoadMore)
                }
            }
        }
    }
}

@Composable
private fun PonySearchField(query: String, onQueryChange: (String) -> Unit) {
    val focus = LocalFocusManager.current
    val colors = MaterialTheme.colorScheme
    val searchLabel = stringResource(R.string.mlp_search)
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = searchLabel },
        singleLine = true,
        shape = CircleShape,
        placeholder = { Text(searchLabel) },
        leadingIcon = {
            Canvas(Modifier.size(24.dp)) {
                val stroke = 2.dp.toPx()
                drawCircle(colors.onSurfaceVariant, radius = size.width * 0.24f,
                    center = Offset(size.width * 0.4f, size.height * 0.4f), style = Stroke(stroke))
                drawLine(colors.onSurfaceVariant, Offset(size.width * 0.58f, size.height * 0.58f),
                    Offset(size.width * 0.8f, size.height * 0.8f), stroke, cap = StrokeCap.Round)
            }
        },
        trailingIcon = if (query.isNotEmpty()) {
            { TextButton(onClick = { onQueryChange("") }) { Text(stringResource(R.string.mlp_clear)) } }
        } else null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun LoadMoreContent(loading: Boolean, failed: Boolean, onLoadMore: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (failed) {
            Text(stringResource(R.string.mlp_page_error), color = colors.error)
        }
        Button(
            onClick = onLoadMore,
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primaryContainer,
                contentColor = colors.primary
            )
        ) {
            if (loading) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                CurioIcon(CurioSymbol.ArrowForward)
            }
            Spacer(Modifier.width(8.dp))
            Text(stringResource(when {
                loading -> R.string.mlp_loading_more
                failed -> R.string.mlp_retry_page
                else -> R.string.mlp_load_more
            }))
        }
    }
}
