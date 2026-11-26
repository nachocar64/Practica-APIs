package com.example.practicaapis.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import com.example.practicaapis.MainActivity
import com.example.practicaapis.model.Joke
import com.example.practicaapis.model.JokeCategory
import com.example.practicaapis.ui.jokes.JokesContent
import com.example.practicaapis.ui.jokes.JokesUiState
import com.example.practicaapis.ui.theme.PracticaAPIsTheme
import org.junit.Rule
import org.junit.Test

class CurioNavigationTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomBarSelectsAllDestinationsAndBackReturnsHome() {
        val selectable = SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)
        compose.onNode(hasText("Home") and selectable).assertIsSelected()
        for (route in listOf("Cats", "Extinct", "Jokes", "Ponies", "Home", "Cats")) {
            compose.onNode(hasText(route) and selectable).performClick()
            compose.onNode(hasText(route) and selectable).assertIsSelected()
        }
        pressBack()
        compose.onNode(hasText("Home") and selectable).assertIsSelected()
    }
}

class JokeInteractionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun revealsPunchlineAndLoadsAnotherJokeInTheSameCategory() {
        compose.setContent {
            var state by remember {
                mutableStateOf(
                    JokesUiState(
                        category = JokeCategory.PROGRAMMING,
                        joke = Joke.TwoPart(1, "Programming", "Fixture setup", "Fixture punchline")
                    )
                )
            }
            PracticaAPIsTheme {
                JokesContent(
                    state = state,
                    onCategorySelected = {},
                    onAnotherJoke = {
                        state = state.copy(
                            joke = Joke.Single(2, "Programming", "Second fixture"),
                            isPunchlineVisible = false
                        )
                    },
                    onShowPunchline = { state = state.copy(isPunchlineVisible = true) },
                    onRetry = {},
                    onResetCategories = {}
                )
            }
        }

        compose.onNodeWithText("Fixture punchline").assertDoesNotExist()
        compose.onNodeWithText("Show punchline").performClick()
        compose.onNodeWithText("Fixture punchline").assertExists()
        compose.onNodeWithText("Another joke").performClick()
        compose.onNodeWithText("Second fixture").assertExists()
    }
}
