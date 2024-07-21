package ohior.app.mediarock.ui.screens.local_movie

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ohior.app.mediarock.convertLongToTime
import ohior.app.mediarock.debugPrint
import ohior.app.mediarock.formatFileSize
import ohior.app.mediarock.model.MovieItem
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.utils.ActionState


class LocalMovieScreenLogic : ViewModel() {
    val localMovieList: StateFlow<List<MovieItem>> =
        AppDatabase.getAllLocalMovies().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

//    private var _localMovieList = mutableStateListOf<MovieItem>()
//    val localMovieList : List<MovieItem> = _localMovieList
    var movieItemListState by mutableStateOf<ActionState>(ActionState.None)

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
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DATE_MODIFIED,
    )

    // Display videos in alphabetical order based on their display name.
    private val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"


    // Show only videos that are at least 5 minutes in duration.
//    private val selection = "${MediaStore.Video.Media.DURATION} >= ?"
//    private val selectionArgs = arrayOf(
//        TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
//    )


    fun getAllVideoFiles(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            movieItemListState = ActionState.Loading
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
                    val duration = cursor.getInt(durationColumn)
                    val size = cursor.getInt(sizeColumn)
                    val path = cursor.getString(pathColumn)
                    val modified = cursor.getInt(modifiedColumn)

                    // Stores column values, the contentUri, and the thumbnail in a local object.
                    val movieItem = MovieItem(
                        itemId = id,
                        name = name,
                        duration = convertLongToTime(duration.toLong()),
                        size = formatFileSize(size.toLong()),
                        lastModified = convertLongToTime(modified.toLong(), true),
                        path = path,
//                        thumbnail = thumbnail
                    )
                    if (!AppDatabase.allLocalMovies().any { it.itemId == movieItem.itemId }) {
                        AppDatabase.addLocalMovie(movieItem)
                    }
                }
//                val movies = AppDatabase.allLocalMovies()
//                for (movie in movies) {
//                }

                movieItemListState = ActionState.None
            }
        }
    }

    override fun onCleared() {
        debugPrint("Local movie cleared")
        super.onCleared()
    }
}