package com.example.practicaapis.data

import com.example.practicaapis.data.remote.RetrofitClient
import com.example.practicaapis.data.remote.catFacts.CatApiService
import com.example.practicaapis.data.remote.catFacts.CatFactDto
import com.example.practicaapis.data.remote.catFacts.CatFactsResponse
import com.example.practicaapis.data.remote.catFacts.catFacts

class CatRepository(
    private val apiService: CatApiService = RetrofitClient.catApiService
) {
    suspend fun getCatBreeds(limit: Int? = null): catFacts {
        return apiService.getCatBreeds(limit = limit)
    }

    suspend fun getRandomCatFact(maxLength: Int? = null): CatFactDto {
        return apiService.getRandomCatFact(maxLength = maxLength)
    }

    suspend fun getCatFacts(limit: Int? = null, maxLength: Int? = null): CatFactsResponse {
        return apiService.getCatFacts(limit = limit, maxLength = maxLength)
    }
}
