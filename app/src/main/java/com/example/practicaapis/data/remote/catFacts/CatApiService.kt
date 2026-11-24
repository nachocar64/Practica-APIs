package com.example.practicaapis.data.remote.catFacts

import retrofit2.http.GET

interface CatApiService {
    @GET("breeds")
    suspend fun getCatBreeds(): catFacts
}
