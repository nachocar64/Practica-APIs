package com.example.practicaapis.ui.mlp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaapis.data.MlpRepository
import com.example.practicaapis.model.Pony
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MlpViewModel(private val repository: MlpRepository = MlpRepository()) : ViewModel() {
    private val mutableState = MutableStateFlow(MlpUiState())
    val state: StateFlow<MlpUiState> = mutableState.asStateFlow()

    private var offset = 0
    private var pageJob: Job? = null
    private var searchJob: Job? = null

    init {
        loadMore()
    }

    fun loadMore() {
        if (pageJob?.isActive == true || !state.value.hasMore) return
        val initial = offset == 0
        mutableState.update {
            it.copy(
                loadingCatalog = initial,
                loadingMore = !initial,
                catalogError = false,
                pageError = false
            )
        }
        pageJob = viewModelScope.launch {
            val result = repository.catalog(PAGE_SIZE, offset)
            ensureActive()
            when (result) {
                is Resultado.Exito -> {
                    val page = result.datos
                    offset += page.receivedCount
                    mutableState.update {
                        it.copy(
                            catalog = (it.catalog + page.ponies).distinctBy(Pony::id),
                            hasMore = page.receivedCount >= PAGE_SIZE,
                            loadingCatalog = false,
                            loadingMore = false
                        )
                    }
                }
                is Resultado.Error -> mutableState.update {
                    it.copy(
                        catalogError = initial,
                        pageError = !initial,
                        loadingCatalog = false,
                        loadingMore = false
                    )
                }
            }
        }
    }

    fun changeQuery(query: String) {
        val previous = state.value.query.trim()
        mutableState.update { it.copy(query = query) }
        if (query.trim() != previous) search(debounce = true)
    }

    fun retry() {
        if (state.value.isSearching) search(debounce = false) else loadMore()
    }

    private fun search(debounce: Boolean) {
        searchJob?.cancel()
        val query = state.value.query.trim()
        mutableState.update {
            it.copy(searchResults = emptyList(), searchError = false, loadingSearch = query.isNotEmpty())
        }
        if (query.isEmpty()) return
        searchJob = viewModelScope.launch {
            if (debounce) delay(350)
            val result = repository.search(query)
            ensureActive()
            mutableState.update {
                when (result) {
                    is Resultado.Exito -> it.copy(searchResults = result.datos.ponies, loadingSearch = false)
                    is Resultado.Error -> it.copy(searchError = true, loadingSearch = false)
                }
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
