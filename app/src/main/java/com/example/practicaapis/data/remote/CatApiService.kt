package com.example.practicaapis.data.remote

import com.example.practicaapis.model.catFacts.catFacts
import retrofit2.http.GET

interface CatApiService {
    @GET("breeds")
    suspend fun getCatBreeds(): catFacts
}
