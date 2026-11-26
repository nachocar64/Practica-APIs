package com.example.practicaapis.ui.extinctAnimals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicaapis.R
import com.example.practicaapis.model.ExtinctAnimal
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.extinctAnimals.components.ExtinctAnimalImage
import com.example.practicaapis.ui.extinctAnimals.components.ExtinctGlyph
import com.example.practicaapis.ui.extinctAnimals.components.ExtinctSymbol
import com.example.practicaapis.ui.theme.LocalCurioAccents

@Composable
internal fun ExtinctAnimalDetailScreen(
    animal: ExtinctAnimal,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val accent = LocalCurioAccents.current.extinct
    val photo = animal.imageUrl
    var linkFailed by remember(animal.wikipediaUrl) { mutableStateOf(false) }
    var imageFailed by remember(photo) { mutableStateOf(false) }
    var imageAttempt by remember(photo) { mutableIntStateOf(0) }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 640.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val backLabel = stringResource(R.string.extinct_back_to_museum)
                val backRotation = if (LocalLayoutDirection.current == LayoutDirection.Ltr) 180f else 0f
                FilledIconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp).semantics { contentDescription = backLabel },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = colors.surface,
                        contentColor = colors.onSurface
                    )
                ) {
                    CurioIcon(CurioSymbol.ArrowForward, Modifier.rotate(backRotation))
                }
                Text(
                    stringResource(R.string.extinct_detail_title),
                    style = typography.titleLarge.copy(fontSize = 22.sp)
                )
            }

            key(imageAttempt) {
                ExtinctAnimalImage(
                    animal = animal,
                    modifier = Modifier.fillMaxWidth()
                        .height(if (photo == null || imageFailed) 236.dp else 256.dp)
                        .clip(RoundedCornerShape(if (photo == null || imageFailed) 32.dp else 24.dp)),
                    describeImage = true,
                    onFailure = { imageFailed = true }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExtinctSymbol(ExtinctGlyph.Species, size = 20.sp, tint = accent.content)
                Text(
                    stringResource(R.string.extinct_species_label),
                    style = typography.labelMedium,
                    color = accent.content,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                animal.displayName,
                modifier = Modifier.semantics { heading() },
                style = typography.displaySmall.copy(fontSize = 44.sp, lineHeight = 52.sp)
            )
            if (animal.commonName != null) {
                Text(
                    animal.binomialName,
                    style = typography.titleMedium.copy(fontSize = 20.sp, lineHeight = 24.sp),
                    color = colors.onSurfaceVariant
                )
            }

            Surface(
                Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = accent.container,
                contentColor = accent.content
            ) {
                Column(
                    Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AnimalDetailField(stringResource(R.string.extinct_location), animal.location)
                    AnimalDetailField(stringResource(R.string.extinct_last_record), animal.lastRecord)
                }
            }

            Text(
                stringResource(R.string.extinct_description),
                modifier = Modifier.semantics { heading() },
                style = typography.titleLarge.copy(fontSize = 22.sp)
            )
            Text(
                animal.description ?: stringResource(R.string.extinct_no_description),
                style = typography.bodyLarge,
                color = colors.onSurfaceVariant
            )

            animal.wikipediaUrl?.let { url ->
                DetailAction(stringResource(R.string.extinct_read_on_wikipedia)) {
                    linkFailed = try {
                        uriHandler.openUri(url)
                        false
                    } catch (_: IllegalArgumentException) {
                        true
                    }
                }
                if (linkFailed) {
                    Text(
                        stringResource(R.string.extinct_link_error),
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        color = colors.error
                    )
                }
            }

            if (photo?.startsWith("https://upload.wikimedia.org/wikipedia/commons/") == true) {
                Text(
                    stringResource(R.string.extinct_photo_source),
                    style = typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            if (imageFailed) {
                DetailAction(stringResource(R.string.extinct_retry_image)) {
                    imageFailed = false
                    imageAttempt++
                }
            }
        }
    }
}

@Composable
private fun AnimalDetailField(label: String, value: String?) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(
            value ?: stringResource(R.string.extinct_unknown),
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailAction(label: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primaryContainer,
            contentColor = colors.primary
        )
    ) {
        CurioIcon(CurioSymbol.ArrowForward, size = 20.sp)
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
