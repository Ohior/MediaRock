package ohior.app.mediarock.model

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class MovieItem(
    @Id var id: Long = 0,
    val itemId: Long,
    val name: String,
    val path: String,
    val duration: String,
    val size: String,
    val lastModified: String,
){
    fun getThumbnail(): Bitmap? {
        return try {
            val mMMR = MediaMetadataRetriever()
            mMMR.setDataSource(path)
            mMMR.frameAtTime
        }catch (iae: IllegalArgumentException){
            null
        }
    }
}