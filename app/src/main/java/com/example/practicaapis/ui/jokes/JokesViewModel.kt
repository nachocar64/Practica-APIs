package com.example.practicaapis.ui.jokes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaapis.data.JokesRepository
import com.example.practicaapis.model.Joke
import com.example.practicaapis.model.JokeCategory
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JokesUiState(
    val joke: Joke? = null,
    val category: JokeCategory = JokeCategory.ALL,
    val isLoading: Boolean = false,
    val isRetrying: Boolean = false,
    val errorMessage: String? = null,
    val isPunchlineVisible: Boolean = false,
)

class JokesViewModel(
    private val repository: JokesRepository = JokesRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(JokesUiState(isLoading = true))
    val uiState: StateFlow<JokesUiState> = _uiState.asStateFlow()

    private var requestJob: Job? = null

    init {
        loadJoke()
    }

    fun selectCategory(category: JokeCategory) {
        if (_uiState.value.category == category && (_uiState.value.joke != null) && (_uiState.value.errorMessage == null)) {
            return
        }
        loadJoke(category)
    }

    fun fetchAnotherJoke() {
        loadJoke(_uiState.value.category)
    }

    fun retry() {
        loadJoke(_uiState.value.category, isRetry = true)
    }

    fun resetCategories() {
        loadJoke(JokeCategory.ALL)
    }

    fun showPunchline() {
        _uiState.update { it.copy(isPunchlineVisible = true) }
    }

    private fun loadJoke(category: JokeCategory = _uiState.value.category, isRetry: Boolean = false) {
        if (requestJob?.isActive == true) return
        requestJob = viewModelScope.launch {
            _uiState.update { current ->
                current.copy(
                    category = category,
                    isLoading = true,
                    isRetrying = isRetry,
                    errorMessage = null,
                    isPunchlineVisible = false
                )
            }
            try {
                when (val result = repository.getJoke(category)) {
                    is Resultado.Exito -> {
                        _uiState.update { current ->
                            current.copy(
                                joke = result.datos,
                                isLoading = false,
                                isRetrying = false,
                                errorMessage = null,
                                isPunchlineVisible = false
                            )
                        }
                    }
                    is Resultado.Error -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                isRetrying = false,
                                errorMessage = result.mensaje
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            }
        }
    }
}
