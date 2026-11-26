package com.example.practicaapis.data

import com.example.practicaapis.data.remote.extinctAnimals.AnimalResponse
import com.example.practicaapis.data.remote.extinctAnimals.Data
import com.example.practicaapis.data.remote.extinctAnimals.ExtinctAnimalsApiService
import com.example.practicaapis.model.ExtinctAnimal
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ExtinctAnimalsRepositoryTest {

    @Test
    fun discoveryIncludesAnimalsWithoutImages() = runBlocking {
        val repository = repository { requiresImage ->
            assertEquals(false, requiresImage)
            AnimalResponse(listOf(record), "success")
        }

        val animal = (repository.discover() as Resultado.Exito<ExtinctAnimal?>).datos!!
        assertEquals("Raphus cucullatus", animal.displayName)
        assertEquals("raphus cucullatus", animal.id)
        assertNull(animal.imageUrl)
        assertNull(animal.description)
        assertEquals("https://en.wikipedia.org/wiki/Dodo", animal.wikipediaUrl)
    }

    @Test
    fun wikimediaThumbnailUsesASupportedWidthWithoutChangingTheFile() = runBlocking {
        val file = "Example%20animal.svg"
        val directory = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/01/$file"
        val sample = record.copy(imageSrc = "$directory/220px-$file.png")
        val repository = repository { AnimalResponse(listOf(sample), "success") }

        val animal = (repository.discover() as Resultado.Exito<ExtinctAnimal?>).datos!!
        assertEquals("$directory/250px-$file.png", animal.imageUrl)
    }

    @Test
    fun missingScientificNameIsAnError() = runBlocking {
        val repository = repository { AnimalResponse(listOf(record.copy(binomialName = "")), "success") }
        assertTrue(repository.discover() is Resultado.Error)
    }

    @Test
    fun emptySuccessIsAnEmptyDiscovery() = runBlocking {
        val repository = repository { AnimalResponse(emptyList(), "success") }
        assertEquals(Resultado.Exito(null), repository.discover())
    }

    @Test
    fun networkFailureIsAnError() = runBlocking {
        val repository = repository { throw IOException("offline") }
        assertTrue(repository.discover() is Resultado.Error)
    }

    @Test
    fun cancellationPropagates() = runBlocking {
        val cancellation = CancellationException("cancelled")
        val repository = repository { throw cancellation }
        val result = runCatching { repository.discover() }
        assertSame(cancellation, result.exceptionOrNull())
    }

    private fun repository(response: suspend (Boolean?) -> AnimalResponse) =
        ExtinctAnimalsRepository(object : ExtinctAnimalsApiService {
            override suspend fun obtenerAnimalAleatorio(requiereImagen: Boolean?) = response(requiereImagen)
            override suspend fun obtenerAnimales(cantidad: Int): AnimalResponse =
                error("Discovery should call the random endpoint")
        })

    private val record = Data(
        binomialName = "Raphus cucullatus",
        commonName = "false",
        imageSrc = "false",
        lastRecord = "1662",
        location = "Mauritius",
        shortDesc = "false",
        wikiLink = "https://en.wikipedia.org/wiki/Dodo"
    )
}
