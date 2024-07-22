package ohior.app.mediarock.ui.screens.local_movie

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ohior.app.mediarock.R
import ohior.app.mediarock.model.MovieItem
import ohior.app.mediarock.model.RichTextModel
import ohior.app.mediarock.service.FileManager
import ohior.app.mediarock.ui.compose_utils.DisplayLottieAnimation
import ohior.app.mediarock.ui.compose_utils.PullToRefresh
import ohior.app.mediarock.ui.compose_utils.RichText
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.utils.VideoType
import ohior.app.mediarock.whenNotNull
import ohior.app.mediarock.whenNull
import java.util.Locale


//@Composable
//private fun MovieList(videoPaths: List<MovieItem>, onclick: (String) -> Unit) {
//    LazyColumn(content = {
//        items(videoPaths, key = { it.itemId }) { videoPath ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(125.dp)
//                    .padding(DeepSize.Small)
//                    .clip(MaterialTheme.shapes.small)
//                    .background(MaterialTheme.colorScheme.tertiary)
//                    .animateItem()
//                    .clickable { onclick(videoPath.path) },
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                videoPath.getThumbnail().whenNotNull { vp ->
//                    Image(
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(DeepSize.Small),
//                        bitmap = vp.asImageBitmap(),//bitmap.asImageBitmap(),
//                        contentScale = ContentScale.FillBounds,
//                        contentDescription = "Video Thumbnail"
//                    )
//                }.whenNull {
//                    Icon(
//                        modifier = Modifier.size(50.dp),
//                        imageVector = Icons.Outlined.RunningWithErrors,
//                        contentDescription = "Video Image"
//                    )
//                }
//                Column(
//                    modifier = Modifier
//                        .weight(2f)
//                        .padding(
//                            top = DeepSize.Small,
//                            bottom = DeepSize.Small,
//                            end = DeepSize.Small
//                        ),
//                ) {
//                    Text(
//                        videoPath.name.replaceFirstChar {
//                            if (it.isLowerCase()) it.titlecase(
//                                Locale.ROOT
//                            ) else it.toString()
//                        },
//                        overflow = TextOverflow.Ellipsis,
//                        maxLines = 2,
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    RichText(
//                        richTextList = listOf(
//                            RichTextModel(
//                                "modified : ",
//                                spanStyle = SpanStyle(
//                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
//                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
//                                )
//                            ),
//                            RichTextModel(
//                                spanStyle = SpanStyle(
//                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
//                                    color = MaterialTheme.colorScheme.onPrimary
//                                ),
//                               text =  videoPath.lastModified,
//                            )
//                        )
//                    )
//                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                        Text(
//                            text = videoPath.duration,
//                            overflow = TextOverflow.Ellipsis,
//                            maxLines = 1,
//                            style = MaterialTheme.typography.labelSmall
//                        )
//                        Spacer(modifier = Modifier.weight(1f))
//                        Text(
//                            text = videoPath.size,
//                            overflow = TextOverflow.Ellipsis,
//                            maxLines = 1,
//                            style = MaterialTheme.typography.labelSmall
//                        )
//                    }
//
//                }
//
//            }
//        }
//    })
//}

@Composable
private fun LocalMovieList(
    localMovieList: List<MovieItem>,
    context: Context,
    onClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), content = {
        items(localMovieList, key = { it.itemId }) { videoPath ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .padding(DeepSize.Small)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .animateItem()
                    .clickable { onClick(videoPath.path) },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                videoPath.getThumbnail(context).whenNotNull { vp ->
                    Image(
                        modifier = Modifier
                            .height(125.dp)
                            .weight(1f)
                            .padding(DeepSize.Small),
                        bitmap = vp.asImageBitmap(),//bitmap.asImageBitmap(),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = "Video Thumbnail"
                    )
                }.whenNull {
                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .padding(DeepSize.Small),
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Video Image"
                    )
                }
                MovieDetails(
                    modifier = Modifier
                        .weight(2f)
                        .padding(
                            top = DeepSize.Small,
                            bottom = DeepSize.Small,
                            end = DeepSize.Small
                        ), videoPath
                )

            }
        }
    })
}

@Composable
private fun MovieDetails(modifier: Modifier, videoPath: MovieItem) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            videoPath.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodySmall
        )
        RichText(
            richTextList = listOf(
                RichTextModel(
                    "modified : ",
                    spanStyle = SpanStyle(
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                ),
                RichTextModel(
                    spanStyle = SpanStyle(
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    text = videoPath.lastModified,
                )
            )
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = videoPath.duration,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = videoPath.size,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall
            )
        }

    }
}


// COMPOSE SCREEN
@Composable
fun LocalMovieScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val refresh = remember { mutableStateOf(false) }
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<LocalMovieScreenLogic>()
    val localMovieList by viewModel.localMovieList.collectAsState()
//    val localMovieList = viewModel.localMovieList

//    LaunchedEffect(key1 = null) {
//        debugPrint("LAUNCH EFFECT CALLED")
//        viewModel.getAllVideoFiles(context)
//    }
    if (localMovieList.isNotEmpty()) {
//        LocalMovieList(localMovieList = localMovieList, context = context) { path ->
//            navHostController.navigate(VideoType(videoPath = path))
//        }

        PullToRefresh(
            isRefreshing = refresh.value,
            onRefresh = {
                scope.launch {
                    refresh.value = true
                    FileManager.saveVideoAndModifyToDatabase(context)
                    refresh.value = false
                }
            },
            content = {
                LocalMovieList(localMovieList = localMovieList, context = context) { path ->
                    navHostController.navigate(VideoType(videoPath = path))
                }
            },
        )

    } else {
        DisplayLottieAnimation(modifier = Modifier.fillMaxSize(), resId = R.raw.empty_lottie)
    }
}
