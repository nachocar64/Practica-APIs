package com.example.practicaapis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicaapis.R

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.loading_title),
    message: String = stringResource(R.string.loading_message)
) {
    SectionContent(modifier, title, message, leading = {
        CircularProgressIndicator(Modifier.size(56.dp))
    }) {
        listOf(1f, 0.81f, 0.56f).forEach { width ->
            Box(
                Modifier.fillMaxWidth(width).height(16.dp).background(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                    MaterialTheme.shapes.extraSmall
                )
            )
        }
        Text(stringResource(R.string.loading_status), color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.error_title)
) {
    SectionContent(modifier, title, message, leading = {
        CurioIcon(CurioSymbol.Offline, size = 56.sp, tint = MaterialTheme.colorScheme.error)
    }) {
        StateAction(stringResource(R.string.try_again), onRetry)
    }
}

@Composable
fun EmptyContent(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.empty_title),
    message: String = stringResource(R.string.empty_message),
    actionLabel: String = stringResource(R.string.clear_search),
    onAction: (() -> Unit)? = null
) {
    SectionContent(modifier, title, message, leading = {
        CurioIcon(CurioSymbol.SearchOff, size = 56.sp, tint = MaterialTheme.colorScheme.primary)
    }) {
        if (onAction != null) StateAction(actionLabel, onAction)
    }
}

@Composable
private fun StateAction(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CurioIcon(CurioSymbol.ArrowForward)
            Text(label)
        }
    }
}

@Composable
private fun SectionContent(
    modifier: Modifier,
    title: String,
    message: String,
    leading: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth().heightIn(min = 440.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            leading()
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}
