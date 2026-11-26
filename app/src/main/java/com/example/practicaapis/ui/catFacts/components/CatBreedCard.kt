package com.example.practicaapis.ui.catFacts.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.practicaapis.model.CatBreed
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.theme.LocalCurioAccents
import java.util.Locale

@Composable
internal fun CatBreedCard(breed: CatBreed, featured: Boolean, onClick: () -> Unit) {
    val accent = LocalCurioAccents.current.cats
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = if (featured) 172.dp else 88.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (featured) accent.container else colors.surface,
            contentColor = if (featured) accent.content else colors.onSurface
        )
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = if (featured) 172.dp else 88.dp)
                .padding(horizontal = if (featured) 20.dp else 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!featured) {
                Surface(shape = MaterialTheme.shapes.medium, color = accent.container, contentColor = accent.content) {
                    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        CurioIcon(CurioSymbol.Pets)
                    }
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(if (featured) 4.dp else 0.dp)) {
                if (featured) {
                    Text(
                        breed.name.uppercase(Locale.ROOT),
                        modifier = Modifier.clearAndSetSemantics {},
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    breed.name,
                    style = if (featured) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = if (featured) FontWeight.Bold else FontWeight.Medium
                )
                BreedSummary(breed.country, breed.origin, featured)
                BreedSummary(breed.coat, breed.pattern, featured)
            }
            if (featured) {
                CatArtwork(Modifier.width(128.dp).weight(0.65f, fill = false))
            } else {
                CurioIcon(CurioSymbol.ArrowForward, tint = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BreedSummary(first: String?, second: String?, featured: Boolean) {
    val summary = listOfNotNull(first, second).joinToString(" · ")
    if (summary.isNotEmpty()) {
        Text(
            summary,
            color = if (featured) LocalContentColor.current else MaterialTheme.colorScheme.onSurfaceVariant,
            style = if (featured) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall
        )
    }
}
