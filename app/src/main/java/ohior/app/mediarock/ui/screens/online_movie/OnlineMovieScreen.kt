package ohior.app.mediarock.ui.screens.online_movie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ohior.app.mediarock.R
import ohior.app.mediarock.getWhenNotNull
import ohior.app.mediarock.model.WebPageItem
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.ui.compose_utils.DisplayLottieAnimation
import ohior.app.mediarock.ui.compose_utils.createShimmer
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.ui.theme.primaryFontFamily
import ohior.app.mediarock.utils.ActionState
import ohior.app.mediarock.utils.WebMovieItemScreenType

private class OnlineMovieScreenImpl {

    @Composable
    fun MovieDetails(modifier: Modifier, webScrap: WebPageItem) {
        val style = MaterialTheme.typography
        Column(modifier = modifier) {
            Text(
                text = webScrap.title.getWhenNotNull("no title") { it },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                style = style.bodyMedium
            )
            Text(
                text = webScrap.description.getWhenNotNull("no description") { it },
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                style = style.bodySmall
            )
        }
    }

    @Composable
    private fun LazyStaggeredGridItemScope.DBMovies(webScrap: WebPageItem, onClick: () -> Unit) {
        Column(
            modifier = Modifier
                .padding(DeepSize.Small)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                .animateItem()
                .clickable { onClick() },
        ) {
            Box(contentAlignment = Alignment.TopEnd) {

                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DeepSize.Small),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(webScrap.imageUrl)
                        .crossfade(true)
                        .build(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .createShimmer(
                                    listOf(
                                        Color.Black.copy(alpha = 0.1f),
                                        Color.Black.copy(alpha = 0.9f)
                                    )
                                )
                        )
                    },
                    error = {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    contentScale = ContentScale.Fit,
                    contentDescription = "Movie Image",
                )
                IconButton(onClick = {AppDatabase.deleteMovie(webScrap) }) {
                    Icon(imageVector = Icons.Outlined.DeleteForever, contentDescription = "delete from database")
                }
            }
            MovieDetails(
                modifier = Modifier
                    .padding(DeepSize.Small)
                    .fillMaxWidth(), webScrap
            )
        }
    }

    @Composable
    private fun LazyStaggeredGridItemScope.NewMovies(webScrap: WebPageItem, onClick: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DeepSize.Small)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.tertiary)
                .animateItem()
                .clickable { onClick() },
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .height((LocalView.current.width / 2.5).dp)
                    .padding(DeepSize.Small),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(webScrap.imageUrl)
                    .crossfade(true)
                    .build(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .createShimmer(
                                listOf(
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.9f)
                                )
                            )
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                contentScale = ContentScale.FillBounds,
                contentDescription = "Movie Image",
            )
            MovieDetails(
                modifier = Modifier
                    .padding(
                        top = DeepSize.Small,
                        bottom = DeepSize.Small,
                        end = DeepSize.Small
                    ), webScrap
            )
        }

    }

    @Composable
    fun OnlineMovies(viewModel: OnlineMovieScreenLogic, navController: NavHostController) {

        val databaseList by viewModel.databaseList.collectAsState()

        val newMovie by remember {
            mutableStateOf(viewModel.webPageList)
        }
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
            items(
                newMovie.size,
                key = { it },
                span = { StaggeredGridItemSpan.Companion.FullLine }) { pos ->
                NewMovies(webScrap = newMovie[pos]) {
                    navController.navigate(
                        WebMovieItemScreenType(
                            downloadUrl = newMovie[pos].pageUrl,
                            description = newMovie[pos].description
                        )
                    )
                }
            }

            items(databaseList.size) { dbMovie ->
                DBMovies(webScrap = databaseList[dbMovie]) {
                    navController.navigate(
                        WebMovieItemScreenType(
                            downloadUrl = databaseList[dbMovie].pageUrl,
                            description = databaseList[dbMovie].description
                        )
                    )
                }
            }
        }
    }
}


private val onlineMovieScreenImpl = OnlineMovieScreenImpl()

@Composable
fun OnlineMovieScreen(viewModel: OnlineMovieScreenLogic, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (viewModel.webPageListState) {
            is ActionState.None -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                if (AppDatabase.allMovies().isEmpty()) {
                    DisplayLottieAnimation(
                        modifier = Modifier.size((LocalView.current.width / 2).dp),
                        resId = R.raw.empty_lottie
                    )
                } else {
                    onlineMovieScreenImpl.OnlineMovies(viewModel = viewModel, navController)
                }
            }

            is ActionState.Success -> {
                onlineMovieScreenImpl.OnlineMovies(viewModel = viewModel, navController)
            }

            is ActionState.Fail -> {
                DisplayLottieAnimation(
                    modifier = Modifier.size((LocalView.current.width / 2).dp),
                    resId = R.raw.error_lottie
                )
                Text(
                    text = "could not get movies at this time, ${(viewModel.webPageListState as ActionState.Fail).message}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = primaryFontFamily
                    ),
                    textAlign = TextAlign.Center
                )
            }

            ActionState.Loading -> Unit
        }
    }
}
