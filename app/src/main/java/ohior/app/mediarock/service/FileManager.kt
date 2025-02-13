package ohior.app.mediarock.service

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ohior.app.mediarock.model.MovieItem

object FileManager {
    private val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }


    private val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DATE_MODIFIED,
    )

    // Display videos in alphabetical order based on their display name.
    private const val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"


    // Show only videos that are at least 5 minutes in duration.
//    private val selection = "${MediaStore.Video.Media.DURATION} >= ?"
//    private val selectionArgs = arrayOf(
//        TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
//    )

    suspend fun saveVideoAndModifyToDatabase(context: Context) {
        withContext(Dispatchers.IO) {
            val movieList = mutableListOf<MovieItem>()
            val query = context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )
            query?.use { cursor ->
                // Cache column indices.
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val folderColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val modifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val folderName = cursor.getString(folderColumn)
                    val duration = cursor.getInt(durationColumn)
                    val size = cursor.getInt(sizeColumn)
                    val path = cursor.getString(pathColumn)
                    val modified = cursor.getInt(modifiedColumn)

                    // Stores column values, the contentUri, and the thumbnail in a local object.
                    val movieItem = MovieItem(
                        itemId = id,
                        name = name ?: path.split("/").last(),
                        path = path,
                        folderName = folderName ?: "",
                        durationLong = duration.toLong(),
                        sizeLong = size.toLong(),
                        lastModifiedLong = modified.toLong(),
                    )
                    movieList.add(movieItem)
                }
                val dbMovies = AppDatabase.allLocalMovies()
                // add movies if they are not in database
                movieList.forEach { movie ->
                    if (!dbMovies.any { it.itemId == movie.itemId }) {
                        AppDatabase.addLocalMovie(movie)
                    }
                }
                // delete movies if they do not exist
                dbMovies.forEach { movie ->
                    if (!movieList.any { it.itemId == movie.itemId }) {
                        AppDatabase.deleteLocalMovie(movie)
                    }
                }
            }
        }
    }
}