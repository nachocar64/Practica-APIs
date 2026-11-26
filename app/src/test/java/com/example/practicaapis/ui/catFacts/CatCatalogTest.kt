package com.example.practicaapis.ui.catFacts

import com.example.practicaapis.data.CatRepository
import com.example.practicaapis.data.remote.catFacts.CatApiService
import com.example.practicaapis.data.remote.catFacts.Data
import com.example.practicaapis.data.remote.catFacts.catFacts
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CatCatalogTest {
    @Test
    fun searchAndFiltersUseAllPages() = runBlocking {
        val pages = mutableListOf<Int>()
        val service = BreedService { page ->
            pages += page
            val row = if (page == 1) {
                Data("Abyssinian", "Short", "Ethiopia", "Natural", "Ticked")
            } else {
                Data("LaPerm", "Rex (Short/Long)", "United States", "Mutation", " ")
            }
            catFacts(page, listOf(row), last_page = 2)
        }

        val result = CatRepository(service).getCatalog() as Resultado.Exito
        val state = CatUiState(catalog = result.datos, loading = false)
        val filtered = state.copy(query = "  laPeRm ", filter = CoatFilter.Curly).breeds

        assertEquals(listOf(1, 2), pages)
        assertEquals(listOf("LaPerm"), filtered.map { it.name })
        assertEquals(null, filtered.single().pattern)
        assertEquals(true, CoatFilter.SemiLong.matches("Semi Long"))
        assertEquals(true, CoatFilter.SemiLong.matches("Semi-long"))
        assertEquals(true, CoatFilter.Short.matches("Long/Short"))
    }

    @Test
    fun cancellationPropagatesOutOfRepository() {
        assertThrows(CancellationException::class.java) {
            runBlocking {
                CatRepository(BreedService { throw CancellationException() }).getCatalog()
            }
        }
    }

    private class BreedService(private val response: (Int) -> catFacts) : CatApiService {
        override suspend fun getCatBreeds(limit: Int?, page: Int?) = response(checkNotNull(page))

        override suspend fun getRandomCatFact(maxLength: Int?) =
            error("Unexpected random fact request")

        override suspend fun getCatFacts(limit: Int?, maxLength: Int?) =
            error("Unexpected facts request")
    }
}
