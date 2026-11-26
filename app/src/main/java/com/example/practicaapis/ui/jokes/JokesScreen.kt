package com.example.practicaapis.ui.jokes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicaapis.R
import com.example.practicaapis.model.Joke
import com.example.practicaapis.model.JokeCategory
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.components.EmptyContent
import com.example.practicaapis.ui.components.ErrorContent
import com.example.practicaapis.ui.components.LoadingContent
import com.example.practicaapis.ui.jokes.components.JokeCard
import com.example.practicaapis.ui.jokes.components.JokeCategorySelector
import com.example.practicaapis.ui.theme.PracticaAPIsTheme

@Composable
fun JokesScreen(
    modifier: Modifier = Modifier,
    onDetailOpenChanged: (Boolean) -> Unit = {},
    onToggleTheme: () -> Unit = {},
    viewModel: JokesViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        onDetailOpenChanged(false)
    }

    JokesContent(
        state = state,
        onCategorySelected = viewModel::selectCategory,
        onAnotherJoke = viewModel::fetchAnotherJoke,
        onShowPunchline = viewModel::showPunchline,
        onRetry = viewModel::retry,
        onResetCategories = viewModel::resetCategories,
        modifier = modifier
    )
}

@Composable
fun JokesContent(
    state: JokesUiState,
    onCategorySelected: (JokeCategory) -> Unit,
    onAnotherJoke: () -> Unit,
    onShowPunchline: () -> Unit,
    onRetry: () -> Unit,
    onResetCategories: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentJoke = state.joke
    val isErrorOrEmpty = (currentJoke == null) || (state.errorMessage != null)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isErrorOrEmpty) {
                // Header for error / empty / initial loading states
                Text(
                    text = stringResource(R.string.jokes_header_alt),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.jokes_subtitle_alt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                when {
                    state.isLoading || state.isRetrying -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LoadingContent(
                                title = stringResource(R.string.jokes_loading_title),
                                message = stringResource(R.string.jokes_loading_message)
                            )
                            Text(
                                text = stringResource(R.string.jokes_loading_footer),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    state.errorMessage != null -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            ErrorContent(
                                title = stringResource(R.string.jokes_offline_title),
                                message = stringResource(R.string.jokes_offline_message),
                                onRetry = onRetry
                            )
                            Text(
                                text = stringResource(R.string.jokes_offline_footer),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            EmptyContent(
                                title = stringResource(R.string.jokes_empty_title),
                                message = stringResource(R.string.jokes_empty_message),
                                actionLabel = stringResource(R.string.jokes_reset_categories),
                                onAction = onResetCategories
                            )
                            Text(
                                text = stringResource(R.string.jokes_empty_footer),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Main Success Flow Header
                Text(
                    text = stringResource(R.string.jokes_headline),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CurioIcon(
                        symbol = CurioSymbol.Sparkles,
                        size = 18.sp,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.jokes_family_friendly),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Category chips selector
                JokeCategorySelector(
                    selectedCategory = state.category,
                    onCategorySelected = onCategorySelected
                )

                // Main protagonist card
                JokeCard(
                    joke = currentJoke,
                    isPunchlineVisible = state.isPunchlineVisible,
                    onShowPunchline = onShowPunchline
                )

                // "Another joke" primary action button
                Button(
                    onClick = onAnotherJoke,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = !state.isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CurioIcon(
                            symbol = CurioSymbol.ArrowForward,
                            size = 20.sp,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(R.string.jokes_another_joke),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.jokes_footer_another),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Previews
@Preview(showBackground = true, name = "Two-Part Joke (Hidden Punchline)")
@Composable
private fun JokesContentPreviewTwoPartHidden() {
    PracticaAPIsTheme {
        JokesContent(
            state = JokesUiState(
                joke = Joke.TwoPart(
                    id = 1,
                    category = "Miscellaneous",
                    setup = "What do you call a bear with no teeth?",
                    delivery = "A gummy bear!"
                ),
                category = JokeCategory.ALL,
                isPunchlineVisible = false
            ),
            onCategorySelected = {},
            onAnotherJoke = {},
            onShowPunchline = {},
            onRetry = {},
            onResetCategories = {}
        )
    }
}

@Preview(showBackground = true, name = "Two-Part Joke (Revealed Punchline)")
@Composable
private fun JokesContentPreviewTwoPartRevealed() {
    PracticaAPIsTheme {
        JokesContent(
            state = JokesUiState(
                joke = Joke.TwoPart(
                    id = 1,
                    category = "Miscellaneous",
                    setup = "What do you call a bear with no teeth?",
                    delivery = "A gummy bear!"
                ),
                category = JokeCategory.ALL,
                isPunchlineVisible = true
            ),
            onCategorySelected = {},
            onAnotherJoke = {},
            onShowPunchline = {},
            onRetry = {},
            onResetCategories = {}
        )
    }
}

@Preview(showBackground = true, name = "Single Joke")
@Composable
private fun JokesContentPreviewSingle() {
    PracticaAPIsTheme {
        JokesContent(
            state = JokesUiState(
                joke = Joke.Single(
                    id = 2,
                    category = "Programming",
                    text = "My computer needs a break. It has too many windows open."
                ),
                category = JokeCategory.PROGRAMMING
            ),
            onCategorySelected = {},
            onAnotherJoke = {},
            onShowPunchline = {},
            onRetry = {},
            onResetCategories = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun JokesContentPreviewDark() {
    PracticaAPIsTheme(darkTheme = true) {
        JokesContent(
            state = JokesUiState(
                joke = Joke.TwoPart(
                    id = 1,
                    category = "Miscellaneous",
                    setup = "What do you call a bear with no teeth?",
                    delivery = "A gummy bear!"
                ),
                category = JokeCategory.ALL,
                isPunchlineVisible = true
            ),
            onCategorySelected = {},
            onAnotherJoke = {},
            onShowPunchline = {},
            onRetry = {},
            onResetCategories = {}
        )
    }
}

@Preview(showBackground = true, name = "Error / Offline State")
@Composable
private fun JokesContentPreviewError() {
    PracticaAPIsTheme {
        JokesContent(
            state = JokesUiState(
                errorMessage = "No internet connection available"
            ),
            onCategorySelected = {},
            onAnotherJoke = {},
            onShowPunchline = {},
            onRetry = {},
            onResetCategories = {}
        )
    }
}
