package ohior.app.mediarock.ui.screens.video

import android.content.Context
import android.net.Uri
import android.widget.VideoView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class VideoScreenLogic : ViewModel() {
    var isNotLandscape by mutableStateOf(false)
        private set
    var currentTime by mutableIntStateOf(0) // in milliseconds
        private set
    var isControlsDisplayed by mutableStateOf(false)
        private set

    private var offsetX by   mutableFloatStateOf(0f)
    private var offsetY by   mutableFloatStateOf(0f)

    fun updateOffset(x: Float, y: Float){
        offsetX = x
        offsetY = y
    }

    fun updateCurrentTime(currentPosition: Int) {
        currentTime = currentPosition
    }

    fun updateIsNotLandscape(boolean: Boolean) {
        isNotLandscape = boolean
    }

    fun updateIsContentDisplayed(boolean: Boolean) {
        isControlsDisplayed = boolean
    }


    fun createVideoView(context: Context, videoUri: Uri): VideoView {
        val mediaController = CustomMediaController(context).apply {
            setAnchorView(this)
            setOnVisibilityChangedListener {
                isControlsDisplayed = it
            }
        }
        val videoView = VideoView(context).apply {
            setVideoURI(videoUri)
            setMediaController(mediaController)
            setOnPreparedListener { start() }
            seekTo(currentTime)
        }
        return videoView
    }

}




