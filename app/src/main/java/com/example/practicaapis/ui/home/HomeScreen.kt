package com.example.practicaapis.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import com.example.practicaapis.R
import com.example.practicaapis.ui.components.CurioIcon
import com.example.practicaapis.ui.components.CurioSymbol
import com.example.practicaapis.ui.navigation.AppRoute
import com.example.practicaapis.ui.theme.CardColors
import com.example.practicaapis.ui.theme.LocalCurioAccents

@Composable
fun HomeScreen(onNavigate: (AppRoute) -> Unit, modifier: Modifier = Modifier) {
    val accents = LocalCurioAccents.current
    val context = LocalContext.current.applicationContext
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(SvgDecoder.Factory()) }.build()
    }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 560.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.home_title), style = MaterialTheme.typography.displaySmall)
            Text(
                stringResource(R.string.home_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DiscoveryCard(AppRoute.Cats, R.string.home_cats, accents.cats, 240.dp, onNavigate) {
                        CatArtwork(imageLoader)
                    }
                    DiscoveryCard(AppRoute.Jokes, R.string.home_jokes, accents.jokes, 164.dp, onNavigate) {
                        Box(Modifier.height(36.dp), contentAlignment = Alignment.CenterStart) {
                            CurioIcon(CurioSymbol.Smile, size = 36.sp)
                        }
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DiscoveryCard(AppRoute.Extinct, R.string.home_extinct, accents.extinct, 188.dp, onNavigate) {
                        Box(
                            Modifier.fillMaxWidth().height(64.dp).clip(MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Artwork(
                                "assetdodo.png", imageLoader,
                                Modifier.offset(y = (-12).dp).fillMaxWidth().requiredHeight(94.dp)
                                    .wrapContentHeight(Alignment.Top)
                            )
                        }
                    }
                    DiscoveryCard(AppRoute.Ponies, R.string.home_ponies, accents.ponies, 216.dp, onNavigate) {
                        Artwork("assettwilight.png", imageLoader, Modifier.fillMaxWidth().height(88.dp))
                    }
                }
            }
            Row(
                Modifier.heightIn(min = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurioIcon(CurioSymbol.Sparkles, tint = MaterialTheme.colorScheme.primary)
                Text(
                    stringResource(R.string.home_footer),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun DiscoveryCard(
    route: AppRoute,
    @StringRes subtitle: Int,
    colors: CardColors,
    minHeight: Dp,
    onNavigate: (AppRoute) -> Unit,
    artwork: @Composable () -> Unit
) {
    Card(
        onClick = { onNavigate(route) },
        modifier = Modifier.fillMaxWidth().heightIn(min = minHeight),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = colors.container, contentColor = colors.content)
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            artwork()
            Row(Modifier.fillMaxWidth().heightIn(min = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(route.label), Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                CurioIcon(CurioSymbol.ArrowForward, size = 20.sp)
            }
            Text(stringResource(subtitle), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun CatArtwork(imageLoader: ImageLoader) {
    Box(Modifier.widthIn(max = 128.dp).fillMaxWidth().aspectRatio(128f / 112f)) {
        CatLayer("body.svg", imageLoader, 0.2171f, 0.4072f, 0.5589f, 0.5149f)
        CatLayer("head.svg", imageLoader, 0.238f, 0.079f, 0.441f, 0.458f, rotation = -12f)
        CatLayer("tail.svg", imageLoader, 0.2161f, 0.675f, 0.5629f, 0.2577f)
        CatLayer("sparkles.svg", imageLoader, 0.1094f, 0.2071f, 0.7562f, 0.1358f)
    }
}

@Composable
private fun CatLayer(
    file: String,
    imageLoader: ImageLoader,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    rotation: Float = 0f
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Artwork(
            file, imageLoader,
            Modifier.offset(x = maxWidth * x, y = maxHeight * y)
                .size(maxWidth * width, maxHeight * height).rotate(rotation)
        )
    }
}

@Composable
private fun Artwork(file: String, imageLoader: ImageLoader, modifier: Modifier) {
    AsyncImage(
        model = "file:///android_asset/curio/$file",
        imageLoader = imageLoader,
        contentDescription = null,
        modifier = modifier
    )
}
