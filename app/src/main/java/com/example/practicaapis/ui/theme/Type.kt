package com.example.practicaapis.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Android's default sans-serif family is Roboto.
internal val Typography = Typography(
    displaySmall = curioText(40, 48, FontWeight.Bold),
    headlineSmall = curioText(28, 32, FontWeight.Bold),
    titleLarge = curioText(24, 28, FontWeight.Bold),
    bodyLarge = curioText(16, 20),
    bodyMedium = curioText(14, 16),
    bodySmall = curioText(12, 16),
    labelLarge = curioText(16, 21, FontWeight.Medium),
    labelMedium = curioText(12, 16, FontWeight.Medium)
)

private fun curioText(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = line.sp
    )