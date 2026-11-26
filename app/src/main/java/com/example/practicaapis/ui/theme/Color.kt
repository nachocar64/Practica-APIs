package com.example.practicaapis.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal data class CardColors(val container: Color, val content: Color)

internal data class CurioAccents(
    val cats: CardColors,
    val extinct: CardColors,
    val jokes: CardColors,
    val ponies: CardColors
)

internal val LightAccents = CurioAccents(
    CardColors(Color(0xFFFFE3CC), Color(0xFF5D3119)),
    CardColors(Color(0xFFDFEADB), Color(0xFF2D4B35)),
    CardColors(Color(0xFFFFEDB1), Color(0xFF514015)),
    CardColors(Color(0xFFF8DDED), Color(0xFF62364F))
)

internal val DarkAccents = CurioAccents(
    CardColors(Color(0xFF573B2B), Color(0xFFFFDCC2)),
    CardColors(Color(0xFF334838), Color(0xFFDFEED9)),
    CardColors(Color(0xFF51451F), Color(0xFFF9E7A8)),
    CardColors(Color(0xFF54354D), Color(0xFFF9D8ED))
)

internal val LocalCurioAccents = staticCompositionLocalOf { LightAccents }

internal val LightCurioColors = lightColorScheme(
    primary = Color(0xFF70509B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEBDDFC),
    onPrimaryContainer = Color(0xFF29232F),
    background = Color(0xFFFCF9FF),
    onBackground = Color(0xFF29232F),
    surface = Color(0xFFF3EEF7),
    onSurface = Color(0xFF29232F),
    surfaceVariant = Color(0xFFF3EEF7),
    onSurfaceVariant = Color(0xFF665D70),
    outline = Color(0xFFD3C8DD),
    error = Color(0xFFBA1A1A)
)

internal val DarkCurioColors = darkColorScheme(
    primary = Color(0xFFD4B9FF),
    onPrimary = Color(0xFF3D235D),
    primaryContainer = Color(0xFF49325F),
    onPrimaryContainer = Color(0xFFF5EDF8),
    background = Color(0xFF16131B),
    onBackground = Color(0xFFF5EDF8),
    surface = Color(0xFF242029),
    onSurface = Color(0xFFF5EDF8),
    surfaceVariant = Color(0xFF242029),
    onSurfaceVariant = Color(0xFFCFC2D6),
    outline = Color(0xFF514958),
    error = Color(0xFFFFB4AB)
)