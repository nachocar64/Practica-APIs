package com.example.practicaapis.ui.mlp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.practicaapis.R

@Composable
internal fun PonyGallery(images: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    if (images.size < 2) return
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            stringResource(R.string.mlp_gallery),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val thumbnailWidth = (maxWidth - 12.dp) / 2
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(images, key = { _, url -> url }) { index, url ->
                    val label = stringResource(R.string.mlp_view_image, index + 1)
                    Surface(
                        onClick = { onSelect(index) },
                        modifier = Modifier.width(thumbnailWidth).height(124.dp).semantics {
                            contentDescription = label
                            selected = index == selectedIndex
                        },
                        shape = RoundedCornerShape(24.dp),
                        border = if (index == selectedIndex) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        PonyImage(url, Modifier.fillMaxSize(), ContentScale.Crop)
                    }
                }
            }
        }
        Text(
            pluralStringResource(R.plurals.mlp_images_available, images.size, images.size),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
