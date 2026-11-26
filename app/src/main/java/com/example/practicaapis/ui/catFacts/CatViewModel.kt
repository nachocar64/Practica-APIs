package com.example.practicaapis.ui.catFacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaapis.data.CatRepository
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CatViewModel(private val repository: CatRepository = CatRepository()) : ViewModel() {
    private val mutableState = MutableStateFlow(CatUiState())
    val state: StateFlow<CatUiState> = mutableState.asStateFlow()
    private var request: Job? = null

    init {
        reload()
    }

    fun reload() {
        if (request?.isActive == true) return
        mutableState.update { it.copy(loading = true, error = false) }
        request = viewModelScope.launch {
            val result = repository.getCatalog()
            ensureActive()
            mutableState.update { current ->
                when (result) {
                    is Resultado.Exito -> current.copy(catalog = result.datos, loading = false)
                    is Resultado.Error -> current.copy(loading = false, error = true)
                }
            }
        }
    }

    fun changeQuery(query: String) {
        mutableState.update { it.copy(query = query) }
    }

    fun selectFilter(filter: CoatFilter) {
        mutableState.update { it.copy(filter = filter) }
    }

    fun clearSearch() {
        mutableState.update { it.copy(query = "", filter = CoatFilter.All) }
    }
}
