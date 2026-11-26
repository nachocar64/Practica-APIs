package com.example.practicaapis.ui.mlp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practicaapis.R
import com.example.practicaapis.model.Pony
import com.example.practicaapis.ui.mlp.components.LocalPonyPreviewImages
import com.example.practicaapis.ui.mlp.components.PonyGallery
import com.example.practicaapis.ui.theme.PracticaAPIsTheme

private val twilight = Pony(
    id = 1,
    name = "Twilight Sparkle",
    alias = "Princess Twilight Sparkle",
    images = listOf(
        "preview://mlp_preview_twilight",
        "preview://mlp_preview_unicorn"
    ),
    species = listOf("Alicorn", "Unicorn", "Human"),
    occupation = "Ruler of Equestria",
    residence = "Canterlot · Castle of Friendship, Ponyville",
    sex = "Female",
    url = null
)

private val sampleCatalog = MlpUiState(
    loadingCatalog = false,
    catalog = listOf(
        twilight,
        Pony(2, "Applejack", null, listOf("preview://mlp_preview_applejack"),
            listOf("Earth"), null, null, null, null),
        Pony(3, "Fluttershy", null, listOf("preview://mlp_preview_fluttershy"),
            listOf("Pegasus"), null, null, null, null),
        Pony(4, "Rarity", null, listOf("preview://mlp_preview_rarity"),
            listOf("Unicorn"), null, null, null, null)
    )
)

@Preview(name = "Catalog light", widthDp = 412, heightDp = 800)
@Composable
private fun CatalogLightPreview() {
    CatalogPreview(sampleCatalog)
}

@Preview(name = "Catalog dark", widthDp = 412, heightDp = 800)
@Composable
private fun CatalogDarkPreview() {
    CatalogPreview(sampleCatalog, darkTheme = true)
}

@Preview(name = "Detail", widthDp = 412, heightDp = 860)
@Composable
private fun DetailPreview() {
    PreviewTheme {
        Surface(color = MaterialTheme.colorScheme.background) { MlpDetailContent(twilight.id, MlpDetailUiState.Ready(twilight), {}, {}) }
    }
}

@Preview(name = "Gallery", widthDp = 412)
@Composable
private fun GalleryPreview() {
    var selected by remember { mutableIntStateOf(1) }
    PreviewTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            androidx.compose.foundation.layout.Box(Modifier.padding(24.dp)) {
                PonyGallery(twilight.images, selected) { selected = it }
            }
        }
    }
}

@Preview(name = "Loading", widthDp = 412, heightDp = 760)
@Composable
private fun LoadingPreview() {
    CatalogPreview(MlpUiState())
}

@Preview(name = "Error", widthDp = 412, heightDp = 760)
@Composable
private fun ErrorPreview() {
    CatalogPreview(MlpUiState(loadingCatalog = false, catalogError = true))
}

@Preview(name = "Empty search", widthDp = 412, heightDp = 760)
@Composable
private fun EmptyPreview() {
    CatalogPreview(MlpUiState(query = "Unknown character", loadingCatalog = false))
}

@Composable
private fun CatalogPreview(state: MlpUiState, darkTheme: Boolean = false) {
    PreviewTheme(darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) { MlpCatalogContent(state, {}, {}, {}, {}) }
    }
}

@Composable
private fun PreviewTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalPonyPreviewImages provides previewImages) {
        PracticaAPIsTheme(darkTheme = darkTheme, content = content)
    }
}

private val previewImages = mapOf(
    "preview://mlp_preview_twilight" to R.drawable.mlp_preview_twilight,
    "preview://mlp_preview_unicorn" to R.drawable.mlp_preview_unicorn,
    "preview://mlp_preview_applejack" to R.drawable.mlp_preview_applejack,
    "preview://mlp_preview_fluttershy" to R.drawable.mlp_preview_fluttershy,
    "preview://mlp_preview_rarity" to R.drawable.mlp_preview_rarity
)

