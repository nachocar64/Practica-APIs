package com.example.practicaapis.ui.mlp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaapis.data.MlpRepository
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MlpDetailViewModel(private val repository: MlpRepository = MlpRepository()) : ViewModel() {
    private val mutableState = MutableStateFlow<MlpDetailUiState>(MlpDetailUiState.Loading)
    val state: StateFlow<MlpDetailUiState> = mutableState.asStateFlow()

    private var selectedId: Int? = null
    private var request: Job? = null

    fun load(id: Int, retry: Boolean = false) {
        if (selectedId == id && (!retry || request?.isActive == true)) return
        request?.cancel()
        selectedId = id
        mutableState.value = MlpDetailUiState.Loading
        request = viewModelScope.launch {
            val result = repository.detail(id)
            ensureActive()
            mutableState.value = when (result) {
                is Resultado.Exito -> result.datos?.let { MlpDetailUiState.Ready(it) } ?: MlpDetailUiState.Missing
                is Resultado.Error -> MlpDetailUiState.Error
            }
        }
    }

    fun close() {
        request?.cancel()
        selectedId = null
        mutableState.value = MlpDetailUiState.Loading
    }
}
