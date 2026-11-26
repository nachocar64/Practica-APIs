package com.example.practicaapis.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.practicaapis.R
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.home.HomeScreen

private const val DetailOpen = "curio.detailOpen"

@Composable
fun AppNavigation(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val selected = AppRoute.entries.find { it.route == entry?.destination?.route } ?: AppRoute.Home
    val detailState = entry?.savedStateHandle?.getStateFlow(DetailOpen, false)
    val isDetailOpen = detailState?.collectAsStateWithLifecycle()?.value ?: false
    val navigate: (AppRoute) -> Unit = { destination ->
        if (entry?.destination?.route != destination.route) {
            navController.navigate(destination.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { if (!isDetailOpen) CurioTopBar(selected, darkTheme, onToggleTheme) },
        bottomBar = { if (!isDetailOpen) CurioBottomBar(selected, navigate) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Home.route,
            modifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)
        ) {
            composable(AppRoute.Home.route) { HomeScreen(navigate) }
            AppRoute.entries.filter { it != AppRoute.Home }.forEach { route ->
                composable(route.route) { sectionEntry ->
                    val onDetailOpenChanged: (Boolean) -> Unit = { isOpen ->
                        sectionEntry.savedStateHandle[DetailOpen] = isOpen
                    }
                    SectionDestination(
                        route = route,
                        onDetailOpenChanged = onDetailOpenChanged,
                        onToggleTheme = onToggleTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun CurioTopBar(route: AppRoute, darkTheme: Boolean, onToggleTheme: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val action = stringResource(if (darkTheme) R.string.enable_light_theme else R.string.enable_dark_theme)
    Row(
        Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.safeDrawing.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        )).padding(start = 24.dp, end = 24.dp, top = 8.dp).heightIn(min = 48.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            stringResource(if (route == AppRoute.Home) R.string.brand_name else route.label),
            Modifier.weight(1f),
            style = MaterialTheme.typography.headlineSmall
        )
        FilledIconButton(
            onClick = onToggleTheme,
            modifier = Modifier.size(48.dp).semantics { contentDescription = action },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colors.primaryContainer,
                contentColor = colors.primary
            )
        ) {
            CurioIcon(if (darkTheme) CurioSymbol.LightMode else CurioSymbol.DarkMode)
        }
    }
}

@Composable
private fun CurioBottomBar(selected: AppRoute, onSelect: (AppRoute) -> Unit) {
    val colors = MaterialTheme.colorScheme
    NavigationBar(containerColor = colors.surface, tonalElevation = 0.dp) {
        AppRoute.entries.forEach { route ->
            NavigationBarItem(
                selected = route == selected,
                onClick = { onSelect(route) },
                icon = { CurioIcon(route.icon) },
                label = { Text(stringResource(route.label), style = MaterialTheme.typography.labelMedium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    selectedTextColor = colors.onSurface,
                    indicatorColor = colors.primaryContainer,
                    unselectedIconColor = colors.onSurfaceVariant,
                    unselectedTextColor = colors.onSurfaceVariant
                )
            )
        }
    }
}
