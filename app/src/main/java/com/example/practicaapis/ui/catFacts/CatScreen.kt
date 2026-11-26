package com.example.practicaapis.ui.catFacts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicaapis.R
import com.example.practicaapis.model.CatBreed
import com.example.practicaapis.ui.catFacts.components.CatBreedCard
import com.example.practicaapis.ui.components.EmptyContent
import com.example.practicaapis.ui.components.ErrorContent
import com.example.practicaapis.ui.components.LoadingContent

@Composable
fun CatScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onToggleTheme: () -> Unit = {}
) {
    val model: CatViewModel = viewModel()
    val state by model.state.collectAsStateWithLifecycle()
    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val focus = LocalFocusManager.current
    val selected = state.catalog.firstOrNull { it.name == selectedName }
    val closeDetail = { selectedName = null }
    val detailOpen = selected != null

    LaunchedEffect(detailOpen, onDetailOpenChanged) {
        onDetailOpenChanged(detailOpen)
    }
    BackHandler(enabled = detailOpen, onBack = closeDetail)

    if (selected != null) {
        CatDetailScreen(selected, onBack = closeDetail, modifier = modifier)
    } else {
        CatCatalogContent(
            state = state,
            onQueryChange = model::changeQuery,
            onFilterChange = model::selectFilter,
            onBreedClick = {
                focus.clearFocus()
                selectedName = it.name
            },
            onRetry = model::reload,
            onClear = model::clearSearch,
            modifier = modifier,
            listState = listState
        )
    }
}

@Composable
internal fun CatCatalogContent(
    state: CatUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (CoatFilter) -> Unit,
    onBreedClick: (CatBreed) -> Unit,
    onRetry: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val breeds = state.breeds
    val showStatus = state.loading || state.error || breeds.isEmpty()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 640.dp).fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        stringResource(if (showStatus) R.string.cats_state_heading else R.string.cats_heading),
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp, lineHeight = 36.sp)
                    )
                    if (showStatus) {
                        Text(
                            stringResource(R.string.cats_source),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    if (!state.loading && !state.error) {
                        CatSearchField(state.query, onQueryChange)
                        CatFilters(state.filter, onFilterChange)
                    }
                    if (!showStatus) {
                        Text(stringResource(R.string.cats_results), fontWeight = FontWeight.Medium)
                    }
                }
            }
            when {
                state.loading -> item { LoadingContent() }
                state.error -> item { ErrorContent(stringResource(R.string.cats_load_error), onRetry) }
                breeds.isEmpty() -> item {
                    EmptyContent(
                        title = stringResource(R.string.cats_empty_title),
                        message = stringResource(R.string.cats_empty_message),
                        onAction = if (state.query.isNotBlank() || state.filter != CoatFilter.All) onClear else null
                    )
                }
                else -> {
                    itemsIndexed(breeds.take(3), key = { _, breed -> breed.name }) { index, breed ->
                        CatBreedCard(breed, featured = index == 0, onClick = { onBreedClick(breed) })
                    }
                    item {
                        Text(
                            stringResource(R.string.cats_catalog_artwork),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    itemsIndexed(breeds.drop(3), key = { _, breed -> breed.name }) { _, breed ->
                        CatBreedCard(breed, featured = false, onClick = { onBreedClick(breed) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CatFilters(selected: CoatFilter, onSelect: (CoatFilter) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CoatFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelect(filter) },
                label = { Text(stringResource(filter.label)) },
                modifier = Modifier.heightIn(min = 48.dp).widthIn(min = if (filter == CoatFilter.SemiLong) 112.dp else 76.dp),
                shape = MaterialTheme.shapes.medium,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = colors.surface,
                    labelColor = colors.onSurfaceVariant,
                    selectedContainerColor = colors.primaryContainer,
                    selectedLabelColor = colors.primary
                ),
                border = null
            )
        }
    }
}

@Composable
private fun CatSearchField(query: String, onQueryChange: (String) -> Unit) {
    val label = stringResource(R.string.cats_search)
    val colors = MaterialTheme.colorScheme
    val focus = LocalFocusManager.current
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
        singleLine = true,
        shape = CircleShape,
        placeholder = { Text(label) },
        leadingIcon = {
            Canvas(Modifier.size(24.dp)) {
                val stroke = 2.dp.toPx()
                drawCircle(
                    colors.onSurfaceVariant,
                    radius = size.width * 0.24f,
                    center = Offset(size.width * 0.4f, size.height * 0.4f),
                    style = Stroke(stroke)
                )
                drawLine(
                    colors.onSurfaceVariant,
                    Offset(size.width * 0.58f, size.height * 0.58f),
                    Offset(size.width * 0.8f, size.height * 0.8f),
                    stroke,
                    cap = StrokeCap.Round
                )
            }
        },
        trailingIcon = if (query.isEmpty()) null else {
            { TextButton(onClick = { onQueryChange("") }) { Text(stringResource(R.string.cats_clear)) } }
        },
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
