package com.example.practicaapis.ui.catFacts

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.practicaapis.model.CatBreed
import com.example.practicaapis.ui.theme.PracticaAPIsTheme

private val previewBreeds = listOf(
    CatBreed("Abyssinian", "Ethiopia", "Natural", "Short", "Ticked"),
    CatBreed("Aegean", "Greece", "Natural", "Semi-long", "Bicolor"),
    CatBreed("American Curl", "United States", "Mutation", "Semi-long", "All")
)
private val ready = CatUiState(catalog = previewBreeds, loading = false)

@Preview(name = "Catalog light", widthDp = 412, heightDp = 750)
@Composable
private fun CatalogLightPreview() = CatalogPreview(ready)

@Preview(name = "Catalog dark", widthDp = 412, heightDp = 750)
@Composable
private fun CatalogDarkPreview() = CatalogPreview(ready, darkTheme = true)

@Preview(name = "Detail light", widthDp = 412, heightDp = 860)
@Composable
private fun DetailLightPreview() = DetailPreview(darkTheme = false)

@Preview(name = "Detail dark", widthDp = 412, heightDp = 860)
@Composable
private fun DetailDarkPreview() = DetailPreview(darkTheme = true)

@Preview(name = "Loading", widthDp = 412, heightDp = 750)
@Composable
private fun LoadingPreview() = CatalogPreview(CatUiState())

@Preview(name = "Error", widthDp = 412, heightDp = 750)
@Composable
private fun ErrorPreview() = CatalogPreview(CatUiState(loading = false, error = true))

@Preview(name = "Empty", widthDp = 412, heightDp = 750)
@Composable
private fun EmptyPreview() = CatalogPreview(ready.copy(query = "Zzz"))

@Composable
private fun CatalogPreview(state: CatUiState, darkTheme: Boolean = false) {
    PracticaAPIsTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CatCatalogContent(state, {}, {}, {}, {}, {})
        }
    }
}

@Composable
private fun DetailPreview(darkTheme: Boolean) {
    PracticaAPIsTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CatDetailScreen(previewBreeds.first(), onBack = {})
        }
    }
}
