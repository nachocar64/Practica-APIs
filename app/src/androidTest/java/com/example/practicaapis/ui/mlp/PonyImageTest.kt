package com.example.practicaapis.ui.mlp

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.decode.DataSource
import coil.intercept.Interceptor
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult
import com.example.practicaapis.ui.mlp.components.PonyImage
import com.example.practicaapis.ui.theme.PracticaAPIsTheme
import java.util.concurrent.CopyOnWriteArrayList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PonyImageTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun usesNextImageAfterFailureAndStopsAfterSuccess() {
        val requested = showImage(listOf("blocked", "available", "unused"))
        compose.waitUntil(5_000) { requested.size >= 2 }
        compose.waitForIdle()

        assertEquals(listOf("blocked", "available"), requested.toList())
        assertEquals(Color.Green, imageCorner())
    }

    @Test
    fun allImagesFailAndKeepThePlaceholder() {
        val requested = showImage(listOf("blocked", "also blocked"))
        compose.waitUntil(5_000) { requested.size >= 2 }
        compose.waitForIdle()

        assertEquals(listOf("blocked", "also blocked"), requested.toList())
        assertEquals(Color(0xFFF8DDED), imageCorner())
    }

    private fun showImage(candidates: List<String>): List<String> {
        val requested = CopyOnWriteArrayList<String>()
        compose.setContent {
            val context = LocalContext.current
            val loader = remember(context) {
                ImageLoader.Builder(context).components {
                    add(object : Interceptor {
                        override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
                            val model = chain.request.data.toString()
                            requested.add(model)
                            return if (model == "available") {
                                SuccessResult(
                                    ColorDrawable(android.graphics.Color.GREEN),
                                    chain.request,
                                    DataSource.MEMORY
                                )
                            } else {
                                ErrorResult(null, chain.request, IllegalStateException("Image unavailable"))
                            }
                        }
                    })
                }.build()
            }
            PracticaAPIsTheme(darkTheme = false) {
                PonyImage(candidates, Modifier.size(64.dp).testTag("pony-image"), imageLoader = loader)
            }
        }
        return requested
    }

    private fun imageCorner(): Color =
        compose.onNodeWithTag("pony-image").captureToImage().toPixelMap()[2, 2]
}
