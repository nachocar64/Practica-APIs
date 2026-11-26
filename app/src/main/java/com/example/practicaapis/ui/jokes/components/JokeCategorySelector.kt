package com.example.practicaapis.ui.jokes.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.practicaapis.R
import com.example.practicaapis.model.JokeCategory

@Composable
fun JokeCategorySelector(
    selectedCategory: JokeCategory,
    onCategorySelected: (JokeCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JokeCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            val label = when (category) {
                JokeCategory.ALL -> stringResource(R.string.jokes_category_all)
                JokeCategory.GENERAL -> stringResource(R.string.jokes_category_misc)
                JokeCategory.PROGRAMMING -> stringResource(R.string.jokes_category_programming)
            }
            val containerColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                label = "CategoryContainer"
            )
            val contentColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "CategoryContent"
            )

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onCategorySelected(category) },
                shape = MaterialTheme.shapes.small,
                color = containerColor,
                contentColor = contentColor
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Text(
                            text = "✓ ",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
