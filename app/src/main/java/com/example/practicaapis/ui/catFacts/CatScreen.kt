package com.example.practicaapis.ui.catFacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicaapis.R
import com.example.practicaapis.data.remote.catFacts.CatFactDto
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.components.EmptyContent
import com.example.practicaapis.ui.components.ErrorContent
import com.example.practicaapis.ui.components.LoadingContent

@Composable
fun CatScreen(
    modifier: Modifier = Modifier,
    viewModel: CatViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val breeds by viewModel.catBreeds.collectAsStateWithLifecycle()
    val facts by viewModel.catFacts.collectAsStateWithLifecycle()
    val limitInput by viewModel.limitInput.collectAsStateWithLifecycle()
    val maxLengthInput by viewModel.maxLengthInput.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Título principal
        Text(
            text = stringResource(R.string.cats_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Pestañas (Razas vs Datos Curiosos)
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == CatTab.BREEDS,
                onClick = { viewModel.selectTab(CatTab.BREEDS) },
                text = { Text("Razas (breeds)") },
                icon = { CurioIcon(CurioSymbol.Pets) }
            )
            Tab(
                selected = selectedTab == CatTab.FACTS,
                onClick = { viewModel.selectTab(CatTab.FACTS) },
                text = { Text("Datos Curiosos (facts)") },
                icon = { CurioIcon(CurioSymbol.Sparkles) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sección de parámetros de la API (Límite, Longitud Máxima y Botón de Consulta)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Parámetros de la API",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Parámetro 'limit'
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { viewModel.updateLimit(it) },
                        label = { Text("Límite (limit)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Parámetro opcional 'max_length' solo para FACTS
                    if (selectedTab == CatTab.FACTS) {
                        OutlinedTextField(
                            value = maxLengthInput,
                            onValueChange = { viewModel.updateMaxLength(it) },
                            label = { Text("Máx. Longitud") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    // Botón de consulta a la API
                    Button(
                        onClick = { viewModel.loadData() },
                        modifier = Modifier.height(56.dp)
                    ) {
                        CurioIcon(CurioSymbol.ArrowForward)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Consultar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Contenido de la pantalla (Carga, Error, Lista)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    LoadingContent(
                        title = "Consultando CatFacts API...",
                        message = "Obteniendo datos de gatos con los parámetros especificados."
                    )
                }
                errorMessage != null -> {
                    ErrorContent(
                        message = errorMessage!!,
                        onRetry = { viewModel.loadData() }
                    )
                }
                selectedTab == CatTab.BREEDS && breeds.isEmpty() -> {
                    EmptyContent(
                        title = "No se encontraron razas",
                        message = "Intenta cambiar el parámetro de límite y volver a consultar."
                    )
                }
                selectedTab == CatTab.FACTS && facts.isEmpty() -> {
                    EmptyContent(
                        title = "No se encontraron datos curiosos",
                        message = "Intenta cambiar el límite o la longitud máxima."
                    )
                }
                selectedTab == CatTab.BREEDS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(breeds) { breed ->
                            CatBreedCard(breed = breed)
                        }
                    }
                }
                selectedTab == CatTab.FACTS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(facts) { fact ->
                            CatFactCard(fact = fact)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatBreedCard(breed: com.example.practicaapis.data.remote.catFacts.Data) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = breed.breed,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "País: ${breed.country.ifBlank { "N/A" }}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Origen: ${breed.origin.ifBlank { "N/A" }}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Pelaje: ${breed.coat.ifBlank { "N/A" }}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Patrón: ${breed.pattern.ifBlank { "N/A" }}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CatFactCard(fact: CatFactDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = fact.fact,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "${fact.length} caracteres",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
