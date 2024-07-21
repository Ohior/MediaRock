package ohior.app.mediarock.ui.screens.video

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import ohior.app.mediarock.ui.theme.White
import ohior.app.mediarock.utils.VideoType


@Composable
fun VideoScreen(viewModel: VideoScreenLogic, videoType: VideoType, navController: NavHostController) {
    val videoUri = remember {
        Uri.parse(videoType.videoPath)
    }
    val context = LocalContext.current
    val videoView = remember { viewModel.createVideoView(context, videoUri) }
    Box(
        contentAlignment = Alignment.TopEnd
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                videoView
            },
            update = {
                it.seekTo(viewModel.currentTime)
//                it.setOnTouchListener { v, event ->
//                    viewModel.isControlsDisplayed =  mediaController?.isShowing?.not() ?: false
//                    v.performClick()
//                }
                //Auto adjust VideoView size based on orientation
                // make sure to update android manifest "android:configChanges="orientation|screenSize|smallestScreenSize""
//                Add to Main activity
//                override fun onConfigurationChanged(newConfig: Configuration) {
//                    super.onConfigurationChanged(newConfig)
//                    // Handle configuration changes if needed
//                }
//            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                view.layoutParams.width = context.resources.displayMetrics.widthPixels
//                view.layoutParams.height = context.resources.displayMetrics.heightPixels
//            } else {
//                view.layoutParams.width = context.resources.displayMetrics.widthPixels
//                view.layoutParams.height = context.resources.displayMetrics.widthPixels * 9 / 16
//            }
            }
        )
        if (viewModel.isControlsDisplayed) {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = White,
                    containerColor = Color.DarkGray
                ), onClick = {
                    (context as Activity).requestedOrientation = if (viewModel.isNotLandscape) {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    viewModel.updateCurrentTime(videoView.currentPosition)
                    viewModel.updateisNotLandscape(!viewModel.isNotLandscape)
                    viewModel.updateIsContentDisplayed(!viewModel.isControlsDisplayed)
                }) {
                Icon(
                    imageVector = Icons.Outlined.ScreenRotation,
                    contentDescription = "screen rotation"
                )

            }
        }
    }
    BackHandler {
        if (!viewModel.isNotLandscape) {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }
        navController.popBackStack()
    }
}
//    val player = remember {
//        ExoPlayer.Builder(context).build().apply {
//            val mediaItem = MediaItem.fromUri(Uri.parse(videoType.videoPath))
//            setMediaItem(mediaItem)
//            prepare()
//            playWhenReady = true
//            seekTo(viewModel.currentTime)
//        }
//    }
//
//    Box(contentAlignment = Alignment.TopEnd) {
//        AndroidView(
//            factory = {
//                PlayerView(context).apply {
//                    this.player = player
//                }
//            },
//            modifier = Modifier
//                .fillMaxSize(),
//            update = { playerView ->
//                playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener {
//                    viewModel.isControlsDisplayed = it < 4
//                })
//            }
//        )
//        if (viewModel.isControlsDisplayed) {
//            IconButton(onClick = {
//                viewModel.isNotLandscape = !viewModel.isNotLandscape
//                viewModel.isControlsDisplayed = false
//                (context as Activity).requestedOrientation = if (viewModel.isNotLandscape) {
//                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                } else {
//                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                }
//            }) {
//                Icon(
//                    imageVector = Icons.Default.ScreenRotation,
//                    contentDescription = "Rotate Screen",
//                    tint = White
//                )
//            }
//        }
//    }
//    DisposableEffect(player) {
//        onDispose {
//            if (player.isPlaying) {
//                viewModel.currentTime = player.currentPosition
//            }
//            player.release()
//        }
//    }
