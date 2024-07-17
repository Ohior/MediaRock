package ohior.app.mediarock.ui.screens.local_movie

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.rizzi.bouquet.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ohior.app.mediarock.R
import ohior.app.mediarock.model.MovieItem
import ohior.app.mediarock.ui.compose_utils.DisplayLottieAnimation
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.ui.theme.primaryFontFamily
import ohior.app.mediarock.utils.ActionState
import ohior.app.mediarock.utils.VideoType
import ohior.app.mediarock.whenNotNull
import ohior.app.mediarock.whenNull
import java.util.Locale

private class LocalMovieScreenImpl {

    @Composable
    fun MovieList(videoPaths: List<MovieItem>, onclick: (String) -> Unit) {
        LazyColumn(content = {
            items(videoPaths, key = { it.itemId }) { videoPath ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(DeepSize.Small)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.tertiary)
                        .animateItem()
                        .clickable { onclick(videoPath.path) },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    videoPath.thumbnail.whenNotNull { vp ->
                        Image(
                            modifier = Modifier
                                .weight(1f)
                                .padding(DeepSize.Small),
                            bitmap = vp.asImageBitmap(),//bitmap.asImageBitmap(),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "Video Thumbnail"
                        )
                    }.whenNull {
                        Icon(
                            modifier = Modifier.size(50.dp),
                            imageVector = Icons.Outlined.VideoFile,
                            contentDescription = "Video Image"
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .padding(
                                top = DeepSize.Small,
                                bottom = DeepSize.Small,
                                end = DeepSize.Small
                            ),
                    ) {
                        Text(
                            videoPath.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            },
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = videoPath.path,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )

                    }

                }
            }
        })
    }
}

private val localMovieScreenImpl = LocalMovieScreenImpl()

@Composable
fun LocalMovieScreen(viewModel: LocalMovieScreenLogic, navHostController: NavHostController) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            if (result.containsValue(false)) {
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Permissions Are needed for app to function properly",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                viewModel.getAllVideoFiles(context)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LaunchedEffect(key1 = Unit, block = {
            viewModel.isPermissionsGranted(context) { b, plist ->
                if (b) viewModel.getAllVideoFiles(context)
                else permissionLauncher.launch(plist.toTypedArray())
            }
        })
        when (viewModel.movieItemListState) {
            is ActionState.None -> {
                DisplayLottieAnimation(
                    modifier = Modifier
                        .size(LocalView.current.width.dp()),
                    resId = R.raw.empty_lottie
                )
            }

           is ActionState.Fail -> {
                DisplayLottieAnimation(
                    modifier = Modifier.size((LocalView.current.width / 2).dp),
                    resId = R.raw.empty_lottie
                )
                Text(
                    text = "sorry! ${(viewModel.movieItemListState as ActionState.Fail).message}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = primaryFontFamily
                    ),
                    textAlign = TextAlign.Center
                )
            }

           is ActionState.Success -> {
                localMovieScreenImpl.MovieList(videoPaths = viewModel.movieItemList) { path ->
                    navHostController.navigate(VideoType(videoPath = path))
                }
            }

          is  ActionState.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                DisplayLottieAnimation(
                    modifier = Modifier.size((LocalView.current.width / 2).dp),
                    resId = R.raw.empty_lottie
                )
            }
        }
    }

}
