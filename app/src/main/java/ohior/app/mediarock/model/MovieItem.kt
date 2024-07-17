package ohior.app.mediarock.model

import android.graphics.Bitmap

data class MovieItem(
    val itemId: Long,
    val name: String,
    val path: String,
    val duration: Int,
    val size: Int,
    val lastModified: Int,
    val thumbnail: Bitmap? = null
)