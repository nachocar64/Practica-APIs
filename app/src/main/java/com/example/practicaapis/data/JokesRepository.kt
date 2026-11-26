package com.example.practicaapis.data

import com.example.practicaapis.data.mappers.toDomain
import com.example.practicaapis.data.remote.RetrofitClient
import com.example.practicaapis.data.remote.jokes.JokesApiService
import com.example.practicaapis.model.Joke
import com.example.practicaapis.model.JokeCategory
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException

class JokesRepository(
    private val apiService: JokesApiService = RetrofitClient.jokesApiService,
) {
    suspend fun getJoke(category: JokeCategory): Resultado<Joke> = try {
        val blacklistFlags = "nsfw,religious,political,racist,sexist,explicit"
        val response = apiService.obtenerChiste(
            categoria = category.apiName,
            blacklistFlags = blacklistFlags,
            lang = "en",
            safeMode = true
        )
        val joke = response.toDomain()
        Resultado.Exito(joke)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Couldn't load joke. Try again.")
    }
}
