package com.example.practicaapis.data

import com.example.practicaapis.data.mappers.toCatBreed
import com.example.practicaapis.data.remote.RetrofitClient
import com.example.practicaapis.data.remote.catFacts.Data
import com.example.practicaapis.model.CatBreed
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import com.example.practicaapis.data.remote.catFacts.CatApiService
import com.example.practicaapis.data.remote.catFacts.CatFactDto
import com.example.practicaapis.data.remote.catFacts.CatFactsResponse
import com.example.practicaapis.data.remote.catFacts.catFacts

class CatRepository(
    private val apiService: CatApiService = RetrofitClient.catApiService
) {
    suspend fun getCatalog(): Resultado<List<CatBreed>> = try {
        val breeds = mutableListOf<CatBreed>()
        var page = 1
        var hasNext: Boolean
        do {
            currentCoroutineContext().ensureActive()
            val response = apiService.getCatBreeds(limit = 50, page = page)
            check(response.current_page == page) { "Unexpected Cat Facts page." }
            val rows: List<Data?>? = response.data
            breeds += checkNotNull(rows).mapNotNull { it?.toCatBreed() }
            hasNext = response.last_page?.let { page < it } ?: (response.next_page_url != null)
            page++
        } while (hasNext)
        Resultado.Exito(breeds.distinctBy { it.name.lowercase() })
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        Resultado.Error("Could not load the Cat Facts breed catalog.")
    }

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
