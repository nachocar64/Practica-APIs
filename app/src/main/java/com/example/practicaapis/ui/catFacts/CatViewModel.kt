package com.example.practicaapis.ui.catFacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicaapis.data.CatRepository
import com.example.practicaapis.data.remote.catFacts.CatFactDto
import com.example.practicaapis.data.remote.catFacts.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CatTab {
    BREEDS,
    FACTS
}

class CatViewModel(
    private val repository: CatRepository = CatRepository()
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(CatTab.BREEDS)
    val selectedTab: StateFlow<CatTab> = _selectedTab.asStateFlow()

    private val _catBreeds = MutableStateFlow<List<Data>>(emptyList())
    val catBreeds: StateFlow<List<Data>> = _catBreeds.asStateFlow()

    private val _catFacts = MutableStateFlow<List<CatFactDto>>(emptyList())
    val catFacts: StateFlow<List<CatFactDto>> = _catFacts.asStateFlow()

    private val _limitInput = MutableStateFlow("10")
    val limitInput: StateFlow<String> = _limitInput.asStateFlow()

    private val _maxLengthInput = MutableStateFlow("")
    val maxLengthInput: StateFlow<String> = _maxLengthInput.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadData()
    }

    fun selectTab(tab: CatTab) {
        if (_selectedTab.value != tab) {
            _selectedTab.value = tab
            loadData()
        }
    }

    fun updateLimit(limit: String) {
        _limitInput.value = limit.filter { it.isDigit() }
    }

    fun updateMaxLength(maxLength: String) {
        _maxLengthInput.value = maxLength.filter { it.isDigit() }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val limit = _limitInput.value.toIntOrNull()
                val maxLength = _maxLengthInput.value.toIntOrNull()

                when (_selectedTab.value) {
                    CatTab.BREEDS -> {
                        val response = repository.getCatBreeds(limit = limit)
                        _catBreeds.value = response.data
                    }
                    CatTab.FACTS -> {
                        val response = repository.getCatFacts(limit = limit, maxLength = maxLength)
                        _catFacts.value = response.data
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Error al conectar con la API de Gatos"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
