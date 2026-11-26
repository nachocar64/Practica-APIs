package com.example.practicaapis.ui.mlp

import com.example.practicaapis.model.Pony

internal sealed interface MlpDetailUiState {
    data object Loading : MlpDetailUiState
    data object Error : MlpDetailUiState
    data object Missing : MlpDetailUiState
    data class Ready(val pony: Pony) : MlpDetailUiState
}
