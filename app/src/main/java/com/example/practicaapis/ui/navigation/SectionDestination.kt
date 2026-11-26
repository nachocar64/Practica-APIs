package com.example.practicaapis.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.practicaapis.R
import com.example.practicaapis.ui.catFacts.CatScreen
import com.example.practicaapis.ui.components.EmptyContent

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun SectionDestination(
    route: AppRoute,
    onDetailOpenChanged: (Boolean) -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (route) {
        AppRoute.Cats -> CatScreen(modifier = modifier)
        AppRoute.Extinct, AppRoute.Jokes, AppRoute.Ponies -> {
            Box(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
                EmptyContent(title = stringResource(route.label), message = stringResource(R.string.section_pending))
            }
        }
        AppRoute.Home -> Unit
    }
}
