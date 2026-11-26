package com.example.practicaapis.data.mappers

import com.example.practicaapis.data.remote.mlp.Data
import com.example.practicaapis.model.Pony
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

// Gson can assign null to Kotlin properties when JSON fields are absent or null.
internal fun Data.toPony(): Pony? {
    val displayName = name.cleaned() ?: return null
    if (id <= 0) return null
    return Pony(
        id = id,
        name = displayName,
        alias = alias.cleaned(),
        images = image.cleaned().mapNotNull { it.remoteUrl() }.distinct(),
        species = kind.cleaned(),
        occupation = occupation.cleaned(),
        residence = residence.cleaned(),
        sex = sex.cleaned(),
        url = url.remoteUrl()
    )
}

private fun String?.cleaned(): String? = this?.trim()?.takeIf(String::isNotEmpty)

private fun List<String?>?.cleaned(): List<String> =
    orEmpty().mapNotNull { it.cleaned() }.distinct()

private fun String?.remoteUrl(): String? = cleaned()?.toHttpUrlOrNull()?.toString()
