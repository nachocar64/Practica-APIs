package com.example.practicaapis.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaapis.data.CatRepository
import com.example.practicaapis.data.remote.catFacts.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatViewModel(
    private val repository: CatRepository = CatRepository()
) : ViewModel() {

    private val _catBreeds = MutableStateFlow<List<Data>>(emptyList())
    val catBreeds: StateFlow<List<Data>> = _catBreeds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchCatBreeds()
    }

    fun fetchCatBreeds() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getCatBreeds()
                _catBreeds.value = response.data
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Error al cargar los datos"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
