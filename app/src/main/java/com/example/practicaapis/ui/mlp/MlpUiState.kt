package com.example.practicaapis.ui.mlp

import com.example.practicaapis.model.Pony

internal data class MlpUiState(
    val query: String = "",
    val catalog: List<Pony> = emptyList(),
    val searchResults: List<Pony> = emptyList(),
    val loadingCatalog: Boolean = true,
    val loadingMore: Boolean = false,
    val loadingSearch: Boolean = false,
    val catalogError: Boolean = false,
    val pageError: Boolean = false,
    val searchError: Boolean = false,
    val hasMore: Boolean = true
) {
    val isSearching: Boolean get() = query.isNotBlank()
    val ponies: List<Pony> get() = if (isSearching) searchResults else catalog
    val isLoading: Boolean get() = if (isSearching) loadingSearch else loadingCatalog
    val hasError: Boolean get() = if (isSearching) searchError else catalogError
}
