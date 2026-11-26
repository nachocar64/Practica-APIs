package com.example.practicaapis.ui.catFacts.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.practicaapis.R
import com.example.practicaapis.ui.theme.DarkAccents
import com.example.practicaapis.ui.theme.LocalCurioAccents

@Composable
internal fun CatArtwork(modifier: Modifier = Modifier) {
    val artwork = if (LocalCurioAccents.current.cats == DarkAccents.cats) {
        R.drawable.cats_local_artwork_dark
    } else {
        R.drawable.cats_local_artwork
    }
    Image(
        painter = painterResource(artwork),
        contentDescription = null,
        modifier = modifier.aspectRatio(238f / 208f)
    )
}
