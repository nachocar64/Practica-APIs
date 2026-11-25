package com.example.practicaapis.data.remote

import com.example.practicaapis.data.remote.catFacts.CatApiService
import com.example.practicaapis.data.remote.extinctAnimals.ExtinctAnimalsApiService
import com.example.practicaapis.data.remote.jokes.JokesApiService
import com.example.practicaapis.data.remote.mlp.MlpApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL_CAT_FACTS = "https://catfact.ninja/"
    private const val BASE_URL_EXTINCT_ANIMALS = "https://extinct-api.herokuapp.com/api/v1/"
    private const val BASE_URL_JOKES = "https://v2.jokeapi.dev/"
    private const val BASE_URL_MLP = "https://ponyapi.net/v1/"

    private val clienteHttp: OkHttpClient = OkHttpClient.Builder()
        // Muestra cada petición en Logcat (filtrar por "OkHttp"). Útil para depurar y para clase.
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    val catApiService: CatApiService by lazy {
        createRetrofit(BASE_URL_CAT_FACTS).create(CatApiService::class.java)
    }

    val extinctAnimalsApiService: ExtinctAnimalsApiService by lazy {
        createRetrofit(BASE_URL_EXTINCT_ANIMALS).create(ExtinctAnimalsApiService::class.java)
    }

    val jokesApiService: JokesApiService by lazy {
        createRetrofit(BASE_URL_JOKES).create(JokesApiService::class.java)
    }

    val mlpApiService: MlpApiService by lazy {
        createRetrofit(BASE_URL_MLP).create(MlpApiService::class.java)
    }

    private fun createRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
