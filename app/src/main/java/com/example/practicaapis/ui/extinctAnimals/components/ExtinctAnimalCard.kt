package com.example.practicaapis.ui.extinctAnimals.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.practicaapis.R
import com.example.practicaapis.model.ExtinctAnimal
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.theme.LocalCurioAccents

@Composable
internal fun FeaturedExtinctAnimalCard(animal: ExtinctAnimal, onClick: () -> Unit) {
    val accent = LocalCurioAccents.current.extinct
    val typography = MaterialTheme.typography
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = accent.container, contentColor = accent.content)
    ) {
        ExtinctAnimalImage(animal, Modifier.fillMaxWidth().height(172.dp), describeImage = true)
        Row(
            Modifier.fillMaxWidth().heightIn(min = 120.dp).padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f).align(Alignment.Top), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(R.string.extinct_featured),
                    style = typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(animal.displayName, style = typography.headlineSmall)
                val scientificName = animal.binomialName.takeIf { animal.commonName != null }
                val subtitle = listOfNotNull(scientificName, animal.location).joinToString(" · ")
                if (subtitle.isNotEmpty()) Text(subtitle, style = typography.bodyMedium)
            }
            CurioIcon(CurioSymbol.ArrowForward, size = 22.sp)
        }
    }
}

@Composable
internal fun ExtinctAnimalCard(
    animal: ExtinctAnimal,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val top = if (isFirst) 24.dp else 0.dp
    val bottom = if (isLast) 24.dp else 0.dp
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom),
        color = colors.surface
    ) {
        Row(
            Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = if (isFirst) 12.dp else 4.dp,
                bottom = if (isLast) 12.dp else 4.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExtinctAnimalImage(animal, Modifier.size(64.dp).clip(RoundedCornerShape(22.dp)))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    animal.displayName,
                    style = typography.titleMedium.copy(fontSize = 20.sp, lineHeight = 24.sp)
                )
                val lastRecord = animal.lastRecord?.let {
                    stringResource(R.string.extinct_last_record_short, it)
                }
                val subtitle = listOfNotNull(animal.location, lastRecord).joinToString(" · ")
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, color = colors.onSurfaceVariant, style = typography.bodySmall)
                }
            }
            ExtinctSymbol(ExtinctGlyph.Chevron, tint = colors.onSurfaceVariant)
        }
    }
}

@Composable
internal fun ExtinctAnimalImage(
    animal: ExtinctAnimal,
    modifier: Modifier = Modifier,
    describeImage: Boolean = false,
    onFailure: () -> Unit = {}
) {
    val url = animal.imageUrl
    val colors = MaterialTheme.colorScheme
    var failed by remember(url) { mutableStateOf(false) }
    Surface(modifier, color = colors.surface, contentColor = colors.onSurfaceVariant) {
        Box(contentAlignment = Alignment.Center) {
            if (url == null || failed) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    ExtinctSymbol(ExtinctGlyph.ImageUnavailable, size = if (describeImage) 44.sp else 24.sp)
                    if (describeImage) {
                        Text(
                            stringResource(R.string.extinct_image_unavailable),
                            color = colors.onSurface,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                        )
                        Text(
                            stringResource(R.string.extinct_information_available),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                val context = LocalContext.current
                val request = remember(context, url) {
                    ImageRequest.Builder(context).data(url).apply {
                        if (url.startsWith("https://upload.wikimedia.org/")) {
                            setHeader("User-Agent", "Curio/1.0 (https://github.com/nachocar64/Practica-APIs)")
                        }
                    }.build()
                }
                AsyncImage(
                    model = request,
                    contentDescription = if (describeImage) {
                        stringResource(R.string.extinct_photo_description, animal.displayName)
                    } else null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onError = {
                        failed = true
                        onFailure()
                    }
                )
            }
        }
    }
}
