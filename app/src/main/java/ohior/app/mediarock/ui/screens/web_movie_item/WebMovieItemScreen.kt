package ohior.app.mediarock.ui.screens.web_movie_item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ohior.app.mediarock.R
import ohior.app.mediarock.model.RichTextModel
import ohior.app.mediarock.ui.compose_utils.DisplayLottieAnimation
import ohior.app.mediarock.ui.compose_utils.PullToRefresh
import ohior.app.mediarock.ui.compose_utils.RichText
import ohior.app.mediarock.ui.compose_utils.createShimmer
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.utils.ActionState
import ohior.app.mediarock.utils.DownloadType
import ohior.app.mediarock.utils.WebMovieItemScreenType


@Composable
private fun MovieInfoList(movieInfo: List<Pair<String, String>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        movieInfo.forEach { pair ->
            RichText(
                richTextList = listOf(
                    RichTextModel(
                        text = pair.first,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    ),
                    RichTextModel(
                        text = pair.second,
                        spanStyle = SpanStyle(fontWeight = FontWeight.SemiBold)
                    )
                )
            )
            Divider(modifier = Modifier.padding(bottom = DeepSize.Small))
        }
    }
}

@Composable
private fun MovieImage(viewModel: WebMovieItemScreenLogic, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = DeepSize.Medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(DeepSize.Small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DeepSize.Small)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillBounds,
            model = ImageRequest.Builder(LocalContext.current)
                .data(viewModel.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "movie image",
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .createShimmer(listOf(Color.LightGray, Color.DarkGray))
                )
//                        .height((LocalView.current.height / 3).dp)
            },
            error = {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Error",
                    tint = Color.Red,
                )
            }
        )
        Text(
            text = description,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
                .padding(vertical = DeepSize.Small),
        )
    }

}


@Composable
private fun DisplayMoviePage(
    description: String,
    viewModel: WebMovieItemScreenLogic,
    navController: NavHostController
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        item(span = { GridItemSpan(currentLineSpan = 2) }) {
            MovieImage(viewModel, description)
        }
        item(span = { GridItemSpan(currentLineSpan = 2) }) {
            MovieInfoList(viewModel.movieInfoList)
        }
        items(viewModel.downloadUrlList) { downloadPair ->
            ElevatedButton(onClick = {
                navController.navigate(DownloadType(downloadPair.first))
            }) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "download file"
                )
                Text(
                    text = downloadPair.second,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun WebMovieItemScreen(
    viewModel: WebMovieItemScreenLogic,
    webMovieItemScreenType: WebMovieItemScreenType,
    navController: NavHostController
) {
    LaunchedEffect(key1 = null) {
        viewModel.loadDownloadPage(webMovieItemScreenType)
    }
    PullToRefresh(isRefreshing = viewModel.isPageRefreshing, onRefresh = {
        viewModel.setIsPageRefreshing(true)
        viewModel.loadDownloadPage(webMovieItemScreenType)
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (viewModel.isContentLoaded) {
                is ActionState.Success -> {
                    DisplayMoviePage(
                        description = webMovieItemScreenType.description,
                        viewModel = viewModel,
                        navController
                    )
                }

                is ActionState.Fail -> {
                    DisplayLottieAnimation(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        resId = R.raw.error_lottie,
                        text = (viewModel.isContentLoaded as ActionState.Fail).message,
                    )
                }

                is ActionState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Red,
                        trackColor = Color.Yellow,
                    )
                    DisplayLottieAnimation(
                        modifier = Modifier.weight(1f),
                        resId = R.raw.empty_lottie
                    )
                }
                is  ActionState.None->{
                    DisplayLottieAnimation(
                        modifier = Modifier.fillMaxSize(),
                        resId = R.raw.empty_lottie
                    )
                }
            }
        }
    }
}