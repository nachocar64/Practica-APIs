package com.example.practicaapis.ui.components

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

enum class CurioSymbol(internal val glyph: String) {
    Explore("\uE87A"), Pets("\uE91D"), Eco("\uEA35"), Smile("\uE7F2"),
    Sparkles("\uE65F"), LightMode("\uE518"), DarkMode("\uE51C"),
    ArrowForward("\uE5C8"), Offline("\uE2C1"), SearchOff("\uEA76")
}

private val Symbols = FontFamily(Font(R.font.material_symbols_rounded))

/** Decorative symbol. Give the enclosing control an accessible label. */
@Composable
fun CurioIcon(
    symbol: CurioSymbol,
    modifier: Modifier = Modifier,
    size: TextUnit = 24.sp,
    tint: Color = LocalContentColor.current
) {
    Text(
        text = symbol.glyph,
        modifier = modifier.clearAndSetSemantics {},
        style = TextStyle(fontFamily = Symbols, fontSize = size, lineHeight = size),
        color = tint,
        maxLines = 1
    )
}
