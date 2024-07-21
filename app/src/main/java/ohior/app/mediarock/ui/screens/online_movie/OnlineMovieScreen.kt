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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ohior.app.mediarock.R
import ohior.app.mediarock.getWhenNotNull
import ohior.app.mediarock.model.WebPageItem
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.ui.compose_utils.CreateLinearProgressBar
import ohior.app.mediarock.ui.compose_utils.DisplayLottieAnimation
import ohior.app.mediarock.ui.compose_utils.createShimmer
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.utils.ActionState
import ohior.app.mediarock.utils.WebMovieItemScreenType
import ohior.app.mediarock.whenNotNull



@Composable
private fun MovieDetails(modifier: Modifier, webScrap: WebPageItem, maxDetailLines: Int? = null) {
    val style = MaterialTheme.typography
    Column(modifier = modifier) {
        Text(
            text = webScrap.title.getWhenNotNull("no title") { it },
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            style = style.bodySmall
        )
        Text(
            text = webScrap.description.getWhenNotNull("no description") { it },
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            style = style.bodySmall,
            maxLines = maxDetailLines ?: Int.MAX_VALUE
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
            val showPopup = remember { mutableStateOf(false) }
            var movie: WebPageItem? = null
            if (showPopup.value) {
                AlertDialog(
                    onDismissRequest = { showPopup.value = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "delete warning",
                            tint = Color.Red
                        )
                    },
                    title = {
                        Text(text = "Don't delete. Dude")
                    },
                    text = {
                        Text(text = "Seriously, This will delete the movie in history 🎬 and you will not be able to get it back")
                    },
                    dismissButton = {
                        TextButton(onClick = { showPopup.value = false }) {
                            Text(text = "Cancel")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            movie.whenNotNull {
                                AppDatabase.deleteMovie(it)
                                showPopup.value = false
                            }
                        }) {
                            Text(text = "Okay! delete")
                        }
                    })
            }
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
            IconButton(onClick = { showPopup.value = true; movie = webScrap }) {
                Icon(
                    tint = MaterialTheme.colorScheme.surface,
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = "delete from database"
                )
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
private fun LazyItemScope.NewMoviesRow(webScrap: WebPageItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width((LocalView.current.width / 3).dp)
            .height((LocalView.current.width / 2).dp)
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
                .weight(1f)
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
        Text(
            text = webScrap.title.getWhenNotNull("no title") { it },
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall
        )
//        MovieDetails(
//            modifier = Modifier
//                .padding(DeepSize.Small), webScrap,
//            maxDetailLines = 3
//        )
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
                .height(300.dp)
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
        Text(
            text = webScrap.title.getWhenNotNull("no title") { it },
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
//        MovieDetails(
//            modifier = Modifier
//                .padding(DeepSize.Small), webScrap,
//            maxDetailLines = 3
//        )
    }

}

@Composable
private fun OnlineMovies(viewModel: OnlineMovieScreenLogic, navController: NavHostController) {

    val databaseList by viewModel.databaseList.collectAsState()

    val newMovie by remember {
        mutableStateOf(viewModel.webPageList)
    }
    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            LazyRow {
                items(newMovie.size, key = { newMovie[it].itemId }) { web ->
                    NewMoviesRow(webScrap = newMovie[web]) {
                        navController.navigate(
                            WebMovieItemScreenType(
                                downloadUrl = newMovie[web].pageUrl,
                                description = newMovie[web].description
                            )
                        )
                    }
                }
            }
        }

        items(databaseList.size, key = {databaseList[it].id}) { dbMovie ->
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


// COMPOSE SCREEN
@Composable
fun OnlineMovieScreen(viewModel: OnlineMovieScreenLogic, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (viewModel.webPageListState) {
            is ActionState.None -> {
                CreateLinearProgressBar(colors = listOf(Color.Red, Color.Yellow, Color.Blue))
                if (AppDatabase.allMovies().isEmpty()) {
                    DisplayLottieAnimation(
                        modifier = Modifier,
                        resId = R.raw.empty_lottie
                    )
                } else {
                    OnlineMovies(viewModel = viewModel, navController)
                }
            }

            is ActionState.Success -> {
                OnlineMovies(viewModel = viewModel, navController)
            }

            is ActionState.Fail -> {
                DisplayLottieAnimation(
                    modifier = Modifier.fillMaxSize(),
                    resId = R.raw.error_lottie,
                    text = (viewModel.webPageListState as ActionState.Fail).message,
                )
            }

            ActionState.Loading -> Unit
        }
    }
}
