package ohior.app.mediarock.ui.screens.web_movie_item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ohior.app.mediarock.R
import ohior.app.mediarock.model.RichTextModel
import ohior.app.mediarock.ui.compose_utils.DisplayLottieAnimation
import ohior.app.mediarock.ui.compose_utils.RichText
import ohior.app.mediarock.ui.compose_utils.createShimmer
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.ui.theme.primaryFontFamily
import ohior.app.mediarock.utils.ActionState
import ohior.app.mediarock.utils.DownloadType
import ohior.app.mediarock.utils.WebMovieItemScreenType


private class WebMovieItemScreenImpl {

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = DeepSize.Medium)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(DeepSize.Small),
            contentAlignment = Alignment.BottomCenter
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalView.current.height / 3).dp),
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
                            .height((LocalView.current.height / 3).dp)
                            .createShimmer(listOf(Color.LightGray, Color.DarkGray))
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Filled.Error, contentDescription = "Error", tint = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((LocalView.current.height / 3).dp)
                            .createShimmer(listOf(Color.LightGray, Color.DarkGray))
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
    fun DisplayMoviePage(
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
}

private val webMovieItemScreenImpl = WebMovieItemScreenImpl()

@Composable
fun WebMovieItemScreen(
    viewModel: WebMovieItemScreenLogic,
    webMovieItemScreenType: WebMovieItemScreenType,
    navController: NavHostController
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.loadDownloadPage(webMovieItemScreenType)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        when (viewModel.isContentLoaded) {
            is ActionState.None -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                DisplayLottieAnimation(
                    modifier = Modifier.size((LocalView.current.width / 2).dp),
                    resId = R.raw.empty_lottie
                )
            }

            is ActionState.Success -> {
                webMovieItemScreenImpl.DisplayMoviePage(
                    description = webMovieItemScreenType.description,
                    viewModel = viewModel,
                    navController
                )
            }

            is ActionState.Fail -> {
                Column(modifier = Modifier
                    .clickable { viewModel.loadDownloadPage(webMovieItemScreenType) }) {
                    DisplayLottieAnimation(
                        modifier = Modifier.size((LocalView.current.width / 2).dp),
                        resId = R.raw.error_lottie
                    )
                    Text(
                        text = "Error, ${(viewModel.isContentLoaded as ActionState.Fail).message}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = primaryFontFamily
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            is ActionState.Loading -> Unit
        }
    }
}
