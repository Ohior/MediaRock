package ohior.app.mediarock.ui.screens.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class VideoScreenLogic : ViewModel() {
    var isLandscape by mutableStateOf(false )
    var currentTime by mutableLongStateOf(0L) // in milliseconds
    var isControlsDisplayed by mutableStateOf(false)
}