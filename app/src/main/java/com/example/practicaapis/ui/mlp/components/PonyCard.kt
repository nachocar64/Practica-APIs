package com.example.practicaapis.ui.mlp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.imageLoader
import com.example.practicaapis.model.Pony
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.theme.LocalCurioAccents

@Composable
internal fun PonyCard(pony: Pony, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalCurioAccents.current.ponies
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 200.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = colors.container, contentColor = colors.content)
    ) {
        Column(
            Modifier.padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PonyImage(
                urls = pony.images,
                modifier = Modifier.fillMaxWidth().height(112.dp).clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                pony.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            pony.species.firstOrNull()?.let { species ->
                Text(species, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
internal fun PonyImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    PonyImage(listOfNotNull(url), modifier, contentScale)
}

@Composable
internal fun PonyImage(
    urls: List<String>,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    imageLoader: ImageLoader = LocalContext.current.imageLoader
) {
    val colors = LocalCurioAccents.current.ponies
    var attempt by remember(urls) { mutableIntStateOf(0) }
    val currentAttempt = attempt
    val url = urls.getOrNull(currentAttempt)
    val previewImage = LocalPonyPreviewImages.current[url]?.let { painterResource(it) }
    var loaded by remember(urls, currentAttempt) { mutableStateOf(false) }
    Box(modifier.background(colors.container), contentAlignment = Alignment.Center) {
        if (previewImage != null) {
            Image(previewImage, contentDescription = null, modifier = Modifier.matchParentSize(), contentScale = contentScale)
        } else if (url != null) {
            AsyncImage(
                model = url,
                imageLoader = imageLoader,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize(),
                onLoading = { loaded = false },
                onSuccess = { loaded = true },
                onError = {
                    if (attempt == currentAttempt) attempt = currentAttempt + 1
                }
            )
        }
        if (previewImage == null && !loaded) {
            CurioIcon(CurioSymbol.Sparkles, tint = colors.content)
        }
    }
}

internal val LocalPonyPreviewImages = staticCompositionLocalOf<Map<String, Int>> { emptyMap() }
