package com.example.practicaapis.ui.jokes.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicaapis.R
import com.example.practicaapis.model.Joke
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.theme.DarkAccents
import com.example.practicaapis.ui.theme.LocalCurioAccents

@Composable
fun JokeCard(
    joke: Joke,
    isPunchlineVisible: Boolean,
    onShowPunchline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = LocalCurioAccents.current.jokes
    val isDark = accents == DarkAccents.jokes

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 320.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = accents.container,
            contentColor = accents.content
        )
    ) {
        AnimatedContent(
            targetState = joke,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "JokeCardContent"
        ) { currentJoke ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header badge
                val categoryText = when (currentJoke.category.lowercase()) {
                    "miscellaneous", "misc" -> "MISC"
                    "programming" -> "PROGRAMMING"
                    else -> currentJoke.category.uppercase()
                }
                val typeText = if (currentJoke is Joke.TwoPart) "TWO-PART" else "SINGLE"
                val badgeText = "$categoryText · $typeText"

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CurioIcon(
                        symbol = CurioSymbol.Smile,
                        size = 32.sp,
                        tint = accents.content
                    )
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = accents.content
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Main body text
                val mainText = when (currentJoke) {
                    is Joke.Single -> currentJoke.text
                    is Joke.TwoPart -> currentJoke.setup
                }

                Text(
                    text = mainText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = accents.content,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom action / punchline / explanation
                when (currentJoke) {
                    is Joke.Single -> {
                        Text(
                            text = stringResource(R.string.jokes_single_explanation),
                            style = MaterialTheme.typography.bodyMedium,
                            color = accents.content.copy(alpha = 0.8f)
                        )
                    }
                    is Joke.TwoPart -> {
                        val pillContainerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
                        val punchlineContentColor = MaterialTheme.colorScheme.onSurface
                        val buttonTextColor = MaterialTheme.colorScheme.primary

                        if (!isPunchlineVisible) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .clickable { onShowPunchline() },
                                shape = MaterialTheme.shapes.large,
                                color = pillContainerColor
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CurioIcon(
                                        symbol = CurioSymbol.ArrowForward,
                                        size = 20.sp,
                                        tint = buttonTextColor
                                    )
                                    Text(
                                        text = stringResource(R.string.jokes_show_punchline),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = buttonTextColor
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = isPunchlineVisible,
                            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                            exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                color = pillContainerColor
                            ) {
                                Text(
                                    text = currentJoke.delivery,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = punchlineContentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
