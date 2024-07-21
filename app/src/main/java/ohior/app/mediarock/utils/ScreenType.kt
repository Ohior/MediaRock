package ohior.app.mediarock.utils

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


@Serializable
object OnlineMovieType

@Serializable
object LocalMovieType

@Serializable
object PdfViewType

@Serializable
object OnboardType

@Serializable
data class WebMovieItemScreenType(val downloadUrl: String?, val description: String = "")

@Serializable
data class VideoType(val videoPath: String)

@Serializable
data class DownloadType(val downloadUrl: String)

data class ScreenHolder(val screen: Any, val imageVector: ImageVector, val contentDescription:String)
