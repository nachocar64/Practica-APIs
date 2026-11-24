package com.example.practicaapis.data

import com.example.practicaapis.data.remote.RetrofitClient
import com.example.practicaapis.data.remote.catFacts.CatApiService
import com.example.practicaapis.data.remote.catFacts.catFacts

class CatRepository(
    private val apiService: CatApiService = RetrofitClient.catApiService
) {
    suspend fun getCatBreeds(): catFacts {
        return apiService.getCatBreeds()
    }
}
