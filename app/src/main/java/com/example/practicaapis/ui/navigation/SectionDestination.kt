package com.example.practicaapis.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.practicaapis.ui.catFacts.CatScreen
import com.example.practicaapis.ui.extinctAnimals.ExtinctAnimalsScreen
import com.example.practicaapis.ui.jokes.JokesScreen
import com.example.practicaapis.ui.mlp.MlpScreen

@Composable
internal fun SectionDestination(
    route: AppRoute,
    onDetailOpenChanged: (Boolean) -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (route) {
        AppRoute.Cats -> CatScreen(
            modifier = modifier,
            onDetailOpenChanged = onDetailOpenChanged,
            onToggleTheme = onToggleTheme
        )
        AppRoute.Extinct -> ExtinctAnimalsScreen(
            modifier = modifier,
            onDetailOpenChanged = onDetailOpenChanged,
            onToggleTheme = onToggleTheme
        )
        AppRoute.Jokes -> JokesScreen(
            modifier = modifier,
            onDetailOpenChanged = onDetailOpenChanged,
            onToggleTheme = onToggleTheme
        )
        AppRoute.Ponies -> MlpScreen(
            modifier = modifier,
            onDetailOpenChanged = onDetailOpenChanged,
            onToggleTheme = onToggleTheme
        )
        AppRoute.Home -> Unit
    }
}
