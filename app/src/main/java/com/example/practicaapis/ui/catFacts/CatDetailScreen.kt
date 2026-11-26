package com.example.practicaapis.ui.catFacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.practicaapis.R
import com.example.practicaapis.model.CatBreed
import com.example.practicaapis.ui.catFacts.components.CatArtwork
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.theme.LocalCurioAccents

@Composable
internal fun CatDetailScreen(breed: CatBreed, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val accent = LocalCurioAccents.current.cats
    val backLabel = stringResource(R.string.cats_back)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 640.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                FilledIconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp).semantics { contentDescription = backLabel },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = colors.surface, contentColor = colors.onSurface
                    )
                ) { CurioIcon(CurioSymbol.ArrowForward, Modifier.rotate(180f)) }
                Text(stringResource(R.string.cats_detail_title), Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                Surface(shape = MaterialTheme.shapes.extraLarge, color = accent.container, contentColor = accent.content) {
                    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { CurioIcon(CurioSymbol.Pets) }
                }
            }
            Surface(
                Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                color = accent.container,
                contentColor = accent.content
            ) {
                Column(Modifier.padding(bottom = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CatArtwork(Modifier.widthIn(max = 238.dp).fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.cats_local_artwork), style = MaterialTheme.typography.labelMedium)
                }
            }
            Text(breed.name, style = MaterialTheme.typography.displaySmall)
            Text(stringResource(R.string.cats_detail_intro), color = colors.onSurfaceVariant)
            Surface(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = colors.surface) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    BreedField(stringResource(R.string.cats_country), breed.country)
                    BreedField(stringResource(R.string.cats_origin), breed.origin)
                    BreedField(stringResource(R.string.cats_coat), breed.coat)
                    BreedField(stringResource(R.string.cats_pattern), breed.pattern)
                }
            }
            Surface(
                Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = colors.primaryContainer,
                contentColor = colors.primary
            ) {
                Text(
                    stringResource(R.string.cats_artwork_note),
                    Modifier.heightIn(min = 76.dp).padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryContainer, contentColor = colors.primary)
            ) {
                CurioIcon(CurioSymbol.ArrowForward)
                Spacer(Modifier.width(8.dp))
                Text(backLabel)
            }
        }
    }
}

@Composable
private fun BreedField(label: String, value: String?) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value ?: stringResource(R.string.cats_unknown),
            Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}
