package ohior.app.mediarock.model

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.compose.runtime.Stable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.File

@Stable
@Entity
data class MovieItem(
    @Id var id: Long = 0,
    val itemId: Long,
    val name: String,
    val path: String,
    val folderName: String = "",
    val durationLong: Long = 0,
    val sizeLong: Long = 0,
    val lastModifiedLong: Long = 0,
) {
    fun getThumbnail(context: Context): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ThumbnailUtils.createVideoThumbnail(File(path), Size(100, 150), null)
        }else {
            MediaStore.Video.Thumbnails.getThumbnail(
                context.contentResolver,
                itemId,
                MediaStore.Video.Thumbnails.MICRO_KIND,
                null
            )
        }
}
//            ThumbnailUtils.createVideoThumbnail(File(path), Size(100, 100), null)
//        return try {
//            val mMMR = MediaMetadataRetriever()
//            mMMR.setDataSource(path)
//            mMMR.frameAtTime
//        }catch (iae: IllegalArgumentException){
//            null
//        }
//        return null
    }
