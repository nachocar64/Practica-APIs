package com.example.practicaapis.ui.extinctAnimals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaapis.data.ExtinctAnimalsRepository
import com.example.practicaapis.model.ExtinctAnimal
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal enum class DiscoveryStatus {
    Loading, Retrying, Ready, Empty, Error;

    val inProgress: Boolean get() = this == Loading || this == Retrying
}

internal data class ExtinctAnimalsUiState(
    val featured: ExtinctAnimal? = null,
    val collection: List<ExtinctAnimal> = emptyList(),
    val discoveryStatus: DiscoveryStatus = DiscoveryStatus.Ready
)

internal class ExtinctAnimalsViewModel(
    private val repository: ExtinctAnimalsRepository = ExtinctAnimalsRepository()
) : ViewModel() {
    private val mutableState = MutableStateFlow(ExtinctAnimalsUiState())
    val state = mutableState.asStateFlow()

    init {
        discover()
    }

    fun discover() {
        val current = mutableState.value
        if (current.discoveryStatus.inProgress) return
        val status = if (current.discoveryStatus == DiscoveryStatus.Error) {
            DiscoveryStatus.Retrying
        } else {
            DiscoveryStatus.Loading
        }
        mutableState.value = current.copy(discoveryStatus = status)

        viewModelScope.launch {
            val result = repository.discover()
            mutableState.update { previous ->
                when (result) {
                    is Resultado.Error -> previous.copy(discoveryStatus = DiscoveryStatus.Error)
                    is Resultado.Exito -> {
                        val animal = result.datos
                        if (animal == null) {
                            previous.copy(discoveryStatus = DiscoveryStatus.Empty)
                        } else {
                            previous.copy(
                                featured = animal,
                                collection = (previous.collection + animal).distinctBy(ExtinctAnimal::id),
                                discoveryStatus = DiscoveryStatus.Ready
                            )
                        }
                    }
                }
            }
        }
    }
}
