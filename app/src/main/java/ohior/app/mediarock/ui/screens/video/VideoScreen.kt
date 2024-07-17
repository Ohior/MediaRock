package ohior.app.mediarock.ui.screens.video

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import ohior.app.mediarock.ui.theme.White
import ohior.app.mediarock.utils.VideoType


@Composable
fun VideoScreen(viewModel: VideoScreenLogic, videoType: VideoType) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(videoType.videoPath))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            seekTo(viewModel.currentTime)
        }
    }

    Box(contentAlignment = Alignment.TopEnd) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                }
            },
            modifier = Modifier
                .fillMaxSize(),
            update = { playerView ->
                playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener {
                    viewModel.isControlsDisplayed = it < 4
                })
            }
        )
        if (viewModel.isControlsDisplayed) {
            IconButton(onClick = {
                viewModel.isLandscape = !viewModel.isLandscape
                viewModel.isControlsDisplayed = false
                (context as Activity).requestedOrientation = if (viewModel.isLandscape) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ScreenRotation,
                    contentDescription = "Rotate Screen",
                    tint = White
                )
            }
        }
    }
    DisposableEffect(player) {
        onDispose {
            if (player.isPlaying) {
                viewModel.currentTime = player.currentPosition
            }
            player.release()
        }
    }
}