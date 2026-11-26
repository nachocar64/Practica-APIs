package com.example.practicaapis

import com.example.practicaapis.data.JokesRepository
import com.example.practicaapis.data.mappers.toDomain
import com.example.practicaapis.data.remote.jokes.Flags
import com.example.practicaapis.data.remote.jokes.JokeResponse
import com.example.practicaapis.data.remote.jokes.JokesApiService
import com.example.practicaapis.model.Joke
import com.example.practicaapis.model.JokeCategory
import com.example.practicaapis.ui.jokes.JokesUiState
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JokesUnitTest {

    @Test
    fun mapper_convertsSingleJoke_successfully() {
        val dto = JokeResponse(
            category = "Programming",
            type = "single",
            joke = "A SQL query walks into a bar...",
            flags = Flags(),
            id = 42,
            safe = true,
            lang = "en",
            error = false
        )
        val domain = dto.toDomain()
        assertTrue(domain is Joke.Single)
        val single = domain as Joke.Single
        assertEquals(42, single.id)
        assertEquals("Programming", single.category)
        assertEquals("A SQL query walks into a bar...", single.text)
    }

    @Test
    fun mapper_convertsTwoPartJoke_successfully() {
        val dto = JokeResponse(
            category = "Miscellaneous",
            type = "twopart",
            setup = "Why don't scientists trust atoms?",
            delivery = "Because they make up everything!",
            flags = Flags(),
            id = 101,
            safe = true,
            lang = "en",
            error = false
        )
        val domain = dto.toDomain()
        assertTrue(domain is Joke.TwoPart)
        val twoPart = domain as Joke.TwoPart
        assertEquals(101, twoPart.id)
        assertEquals("Why don't scientists trust atoms?", twoPart.setup)
        assertEquals("Because they make up everything!", twoPart.delivery)
    }

    @Test(expected = IllegalArgumentException::class)
    fun mapper_rejectsUnsafeJoke() {
        val dto = JokeResponse(
            category = "Dark",
            type = "single",
            joke = "Unsafe joke",
            flags = Flags(),
            id = 1,
            safe = false,
            lang = "en",
            error = false
        )
        dto.toDomain()
    }

    @Test(expected = IllegalArgumentException::class)
    fun mapper_rejectsBlacklistedFlags() {
        val dto = JokeResponse(
            category = "Misc",
            type = "single",
            joke = "Bad joke",
            flags = Flags(nsfw = true),
            id = 1,
            safe = true,
            lang = "en",
            error = false
        )
        dto.toDomain()
    }

    @Test
    fun repository_returnsSuccess_whenApiReturnsValidJoke() = runBlocking {
        val mockService = object : JokesApiService {
            override suspend fun obtenerChiste(
                categoria: String,
                blacklistFlags: String?,
                lang: String,
                safeMode: Boolean?
            ): JokeResponse {
                return JokeResponse(
                    category = categoria,
                    type = "single",
                    joke = "Valid test joke",
                    flags = Flags(),
                    id = 1,
                    safe = true,
                    lang = "en",
                    error = false
                )
            }
        }
        val repository = JokesRepository(mockService)
        val result = repository.getJoke(JokeCategory.PROGRAMMING)
        assertTrue(result is Resultado.Exito)
        val joke = (result as Resultado.Exito).datos
        assertEquals("Valid test joke", (joke as Joke.Single).text)
    }

    @Test
    fun repository_returnsError_whenApiFails() = runBlocking {
        val mockService = object : JokesApiService {
            override suspend fun obtenerChiste(
                categoria: String,
                blacklistFlags: String?,
                lang: String,
                safeMode: Boolean?
            ): JokeResponse {
                throw RuntimeException("Network Error")
            }
        }
        val repository = JokesRepository(mockService)
        val result = repository.getJoke(JokeCategory.ALL)
        assertTrue(result is Resultado.Error)
        assertEquals("Network Error", (result as Resultado.Error).mensaje)
    }

    @Test
    fun uiState_managesPunchlineVisibilityAndCategory() {
        val initialState = JokesUiState(
            joke = Joke.TwoPart(
                id = 99,
                category = "Misc",
                setup = "Setup text",
                delivery = "Delivery text"
            ),
            category = JokeCategory.ALL,
            isPunchlineVisible = false
        )
        assertFalse(initialState.isPunchlineVisible)

        val updatedState = initialState.copy(isPunchlineVisible = true)
        assertTrue(updatedState.isPunchlineVisible)

        val categoryChangedState = updatedState.copy(category = JokeCategory.PROGRAMMING, isPunchlineVisible = false)
        assertEquals(JokeCategory.PROGRAMMING, categoryChangedState.category)
        assertFalse(categoryChangedState.isPunchlineVisible)
    }
}
