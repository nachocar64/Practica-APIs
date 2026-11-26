package com.example.practicaapis.ui.mlp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practicaapis.R
import com.example.practicaapis.model.Pony
import com.example.practicaapis.ui.components.*
import com.example.practicaapis.ui.mlp.components.PonyGallery
import com.example.practicaapis.ui.mlp.components.PonyImage
import com.example.practicaapis.ui.theme.LocalCurioAccents
import kotlinx.coroutines.launch

@Composable
internal fun MlpDetailScreen(
    ponyId: Int,
    model: MlpDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by model.state.collectAsStateWithLifecycle()
    LaunchedEffect(ponyId) { model.load(ponyId) }
    MlpDetailContent(ponyId, state, onBack, onRetry = { model.load(ponyId, retry = true) }, modifier = modifier)
}

@Composable
internal fun MlpDetailContent(
    ponyId: Int,
    state: MlpDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 640.dp).fillMaxWidth().verticalScroll(scrollState)
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val backLabel = stringResource(R.string.mlp_back)
                FilledIconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp).semantics { contentDescription = backLabel },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    CurioIcon(CurioSymbol.ArrowForward, Modifier.rotate(180f))
                }
                Text(stringResource(R.string.mlp_detail_title, ponyId), style = MaterialTheme.typography.titleLarge)
            }
            when (state) {
                MlpDetailUiState.Loading -> LoadingContent()
                MlpDetailUiState.Error -> ErrorContent(stringResource(R.string.mlp_detail_error), onRetry)
                MlpDetailUiState.Missing -> EmptyContent(
                    message = stringResource(R.string.mlp_detail_missing),
                    actionLabel = stringResource(R.string.mlp_back),
                    onAction = onBack
                )
                is MlpDetailUiState.Ready -> PonyProfile(state.pony) {
                    scope.launch { scrollState.animateScrollTo(0) }
                }
            }
        }
    }
}

@Composable
private fun PonyProfile(pony: Pony, onImageSelected: () -> Unit) {
    var selectedImage by rememberSaveable(pony.id) { mutableIntStateOf(0) }
    val index = if (selectedImage in pony.images.indices) selectedImage else 0
    val imageCandidates = remember(pony.images, index) {
        pony.images.drop(index) + pony.images.take(index)
    }
    PonyImage(
        imageCandidates,
        Modifier.fillMaxWidth().height(236.dp).clip(RoundedCornerShape(36.dp))
    )
    Text(pony.name, style = MaterialTheme.typography.displaySmall.copy(fontSize = 36.sp, lineHeight = 44.sp))
    if (pony.species.isNotEmpty()) {
        Text(
            pony.species.joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = LocalCurioAccents.current.ponies.content
        )
    }

    val fields = listOf(
        R.string.mlp_alias to pony.alias,
        R.string.mlp_residence to pony.residence,
        R.string.mlp_occupation to pony.occupation
    ).mapNotNull { (label, value) -> value?.let { label to it } }

    if (fields.isNotEmpty()) {
        Surface(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                fields.forEach { (label, value) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            stringResource(label),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(value, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
    PonyGallery(pony.images, index) {
        selectedImage = it
        onImageSelected()
    }
}
