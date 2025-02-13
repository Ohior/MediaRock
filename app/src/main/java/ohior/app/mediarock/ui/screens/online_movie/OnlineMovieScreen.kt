package ohior.app.mediarock.ui.screens.online_movie

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ohior.app.mediarock.R
import ohior.app.mediarock.getWhenNotNull
import ohior.app.mediarock.model.WebPageItem
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.ui.compose_utils.DisplayLottieAnimation
import ohior.app.mediarock.ui.compose_utils.DisplayPopupMenu
import ohior.app.mediarock.ui.compose_utils.PullToRefresh
import ohior.app.mediarock.ui.compose_utils.createShimmer
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.ui.theme.primaryFontFamily
import ohior.app.mediarock.utils.ActionState
import ohior.app.mediarock.utils.GlobalViewModelStoreOwner
import ohior.app.mediarock.utils.WebMovieItemScreenType
import ohior.app.mediarock.whenNotNull


val myViewModel = ViewModelProvider(GlobalViewModelStoreOwner)[OnlineMovieScreenLogic::class.java]

private val popupMenuList = listOf("🗑 delete movie", "⭐ toggle favorites")

private val onlineMenuList = listOf(
    Pair("🎞 All", OnlineMovieScreenLogic.MenuAction.All),
    Pair("🎬 Movie", OnlineMovieScreenLogic.MenuAction.Movie),
    Pair("📼 Series", OnlineMovieScreenLogic.MenuAction.Series),
    Pair("⭐ Favorites", OnlineMovieScreenLogic.MenuAction.Favourite),
)

@Composable
private fun MovieDetails(
    modifier: Modifier,
    webScrap: WebPageItem,
    maxDetailLines: Int? = null
) {
    val style = MaterialTheme.typography
    val title = if (webScrap.isFavorite) "✨ ${webScrap.title.getWhenNotNull("no title") { it }}"
    else webScrap.title.getWhenNotNull("no title") { it }
    Column(modifier = modifier) {
        Text(
            text = title,
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
    Box(contentAlignment = Alignment.BottomEnd) {
        var movie: WebPageItem? by remember { mutableStateOf(null) }
        var movieMenu by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .padding(DeepSize.Small)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                .animateItem()
                .clickable { onClick() },
        ) {

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
            MovieDetails(
                modifier = Modifier
                    .padding(DeepSize.Small)
                    .fillMaxWidth(), webScrap
            )
        }
        IconButton(onClick = {
            movie = webScrap
            movieMenu = true
        }) {
            DisplayPopupMenu(
                show = movieMenu,
                onDismiss = { movieMenu = !movieMenu },
                onClick = {
                    movieMenu = !movieMenu
                    if (it.lowercase().contains("delete")) {
                        myViewModel.menuAction =
                            OnlineMovieScreenLogic.MenuAction.Delete(data = webScrap)
                    } else if (it.lowercase().contains("favorite")) {
                        myViewModel.menuAction =
                            OnlineMovieScreenLogic.MenuAction.MarkFavourite(data = webScrap)
                    }
                },
                listItems = popupMenuList
            ) { menu ->
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    text = menu,
                    fontFamily = primaryFontFamily,
                    fontSize = 18.sp
                )
            }
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.AutoMirrored.Outlined.MenuOpen,
                contentDescription = "movie menu"
            )
        }
    }
}


@Composable
private fun MoviePopupMenu(context: Context) {
    if (myViewModel.menuAction is OnlineMovieScreenLogic.MenuAction.Delete) {
        val data =
            (myViewModel.menuAction as OnlineMovieScreenLogic.MenuAction.Delete).data as WebPageItem?
        AlertDialog(
            backgroundColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {
                myViewModel.menuAction = OnlineMovieScreenLogic.MenuAction.None
            },
            confirmButton = {
                TextButton(onClick = {
                    myViewModel.menuAction = OnlineMovieScreenLogic.MenuAction.None
                    data.whenNotNull { AppDatabase.deleteMovie(it) }
                }) {
                    Text(
                        "Delete", fontFamily = primaryFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

            },
            title = {
                Text(
                    text = "Delete ${data.getWhenNotNull("") { it.title }}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            text = {
                Text(
                    text = "If you delete ${data.getWhenNotNull("") { """"${it.title}"""" }}, it will be gone forever",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        )
    } else if (myViewModel.menuAction is OnlineMovieScreenLogic.MenuAction.MarkFavourite) {
        val data =
            (myViewModel.menuAction as OnlineMovieScreenLogic.MenuAction.MarkFavourite).data as WebPageItem
        AppDatabase.updateMovie(
            data.copy(isFavorite = !data.isFavorite)
        )
        Toast.makeText(
            context,
            if (!data.isFavorite) "${data.title} added to favourite"
            else "${data.title} removed from favourite",
            Toast.LENGTH_LONG
        ).show()
        myViewModel.menuAction = OnlineMovieScreenLogic.MenuAction.None
    }
}


@Composable
private fun LazyItemScope.NewMoviesRow(webScrap: WebPageItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(250.dp)
            .height(300.dp)
            .padding(DeepSize.Small)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.tertiary)
            .animateItem()
            .clickable { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
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
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                .padding(DeepSize.Small),
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
private fun OnlineMovies(
    databaseList: List<WebPageItem>,
    webPageList: List<WebPageItem>,
    navController: NavHostController
) {
    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            LazyRow {
                items(
                    webPageList.size,
//                    key = { webPageList[it].itemId }
                ) { web ->
                    NewMoviesRow(webScrap = webPageList[web]) {
                        navController.navigate(
                            WebMovieItemScreenType(
                                downloadUrl = webPageList[web].pageUrl,
                                description = webPageList[web].description
                            )
                        )
                    }
                }
            }
        }
        items(databaseList.size, key = { k ->
            databaseList[k].itemId
        }) { dbMovie ->
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
fun OnlineMovieScreen(navController: NavHostController) {
    val myViewModel = viewModel<OnlineMovieScreenLogic>()
    val databaseList by myViewModel.filteredDatabaseList.collectAsState()
    val focusManager = LocalFocusManager.current
    LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    MoviePopupMenu(LocalContext.current)
    PullToRefresh(
        isRefreshing = myViewModel.isPageRefreshing,
        onRefresh = {
            myViewModel.isPageRefreshing = true
            focusManager.clearFocus()
            keyboardController.whenNotNull { it.hide() }
            myViewModel.initWebPageList()
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = myViewModel.searchValue,
                onValueChange = { myViewModel.onSearchValueChanged(it.trim()) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodySmall,
                placeholder = {
                    Text(
                        text = "search movie(s) from database",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )
                },
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colorScheme.background.copy(
                        red = 0.3f,
                        blue = 0.3f,
                        green = 0.3f,
                    ),
                    textColor = MaterialTheme.colorScheme.onBackground
                ),
                trailingIcon = {
                    DisplayPopupMenu(
                        show = myViewModel.onlineMenu,
                        onDismiss = { myViewModel.onlineMenu = false },
                        onClick = { t ->
                            focusManager.clearFocus()
                            keyboardController.whenNotNull { it.hide() }
                            myViewModel.filterByFavorite(t.second)
                            myViewModel.onlineMenu = false
                        },
                        listItems = onlineMenuList,
                        title = {
                            Text(
                                text = "Filter By",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontFamily = primaryFontFamily,
                                fontSize = 18.sp
                            )
                        }
                    ) { menu ->
                        Text(
                            text = menu.first,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = primaryFontFamily,
                            fontSize = 18.sp
                        )
                    }


                    IconButton(onClick = { myViewModel.onlineMenu = !myViewModel.onlineMenu }) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Movie page menu"
                        )
                    }
                }
            )
            when (myViewModel.webPageListState) {
                is ActionState.Success -> {
                    OnlineMovies(
                        databaseList = databaseList,
                        webPageList = myViewModel.webPageList,
                        navController
                    )
                }

                is ActionState.Fail -> {
                    DisplayLottieAnimation(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        resId = R.raw.error_lottie,
                        text = (myViewModel.webPageListState as ActionState.Fail).message,
                    )
                }

                is ActionState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Red,
                        trackColor = Color.Yellow,
                    )
                    if (AppDatabase.allMovies().isEmpty()) {
                        DisplayLottieAnimation(
                            modifier = Modifier.fillMaxSize(),
                            resId = R.raw.empty_lottie
                        )
                    } else {
                        OnlineMovies(
                            databaseList = databaseList,
                            webPageList = myViewModel.webPageList,
                            navController
                        )
                    }

                }

                is ActionState.None -> {
                    DisplayLottieAnimation(
                        modifier = Modifier.fillMaxSize(),
                        resId = R.raw.empty_lottie
                    )
                }
            }
        }
    }
}
