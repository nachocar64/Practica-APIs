package com.example.practicaapis.ui.extinctAnimals.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.practicaapis.R

internal enum class ExtinctGlyph(val character: String) {
    Chevron("\uE5CC"),
    ImageUnavailable("\uF022"),
    Species("\uE99B"),
    Sync("\uE627")
}

private val Symbols = FontFamily(Font(R.font.material_symbols_rounded))

@Composable
internal fun ExtinctSymbol(
    icon: ExtinctGlyph,
    size: TextUnit = 24.sp,
    tint: Color = LocalContentColor.current
) {
    Text(
        text = icon.character,
        modifier = Modifier.clearAndSetSemantics {},
        style = TextStyle(fontFamily = Symbols, fontSize = size, lineHeight = size),
        color = tint,
        maxLines = 1
    )
}
