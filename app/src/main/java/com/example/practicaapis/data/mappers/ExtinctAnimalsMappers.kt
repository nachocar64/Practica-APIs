package com.example.practicaapis.data.mappers

import com.example.practicaapis.data.remote.extinctAnimals.Data
import com.example.practicaapis.model.ExtinctAnimal
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

internal fun Data.toExtinctAnimal(): ExtinctAnimal? {
    val scientificName = binomialName.presentValue() ?: return null
    return ExtinctAnimal(
        binomialName = scientificName,
        commonName = commonName.presentValue(),
        imageUrl = imageSrc.httpsUrl()?.withWikimediaThumbnailSize()?.toString(),
        location = location.presentValue(),
        lastRecord = lastRecord.presentValue(),
        description = shortDesc.presentValue(),
        wikipediaUrl = wikiLink.httpsUrl()
            ?.takeIf { it.host == "wikipedia.org" || it.host.endsWith(".wikipedia.org") }
            ?.toString()
    )
}

private fun String?.presentValue(): String? =
    this?.trim()?.takeUnless { it.isEmpty() || it.equals("false", ignoreCase = true) }

private fun String?.httpsUrl(): HttpUrl? =
    presentValue()?.toHttpUrlOrNull()?.takeIf { it.isHttps }

private val wikimediaThumbnailWidth = Regex("""^(?:(?:lossy|lossless|page\d+)-)*(\d+)px-""")
private val wikimediaThumbnailSizes = listOf(20, 40, 60, 120, 250, 330, 500, 960, 1280, 1920, 3840)

// Wikimedia rejects direct requests for legacy thumbnail sizes such as 220px.
private fun HttpUrl.withWikimediaThumbnailSize(): HttpUrl {
    if (host != "upload.wikimedia.org" || pathSegments.getOrNull(2) != "thumb") return this
    val thumbnail = encodedPathSegments.last()
    val width = wikimediaThumbnailWidth.find(thumbnail)?.groups?.get(1) ?: return this
    val widthInPixels = width.value.toIntOrNull() ?: return this
    val standardWidth = wikimediaThumbnailSizes.firstOrNull { it >= widthInPixels }
        ?: wikimediaThumbnailSizes.last()
    if (widthInPixels == standardWidth) return this

    return newBuilder()
        .setEncodedPathSegment(
            encodedPathSegments.lastIndex,
            thumbnail.replaceRange(width.range, standardWidth.toString())
        )
        .build()
}
