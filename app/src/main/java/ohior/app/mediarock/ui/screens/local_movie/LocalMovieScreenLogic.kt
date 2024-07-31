package ohior.app.mediarock.ui.screens.local_movie

import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ohior.app.mediarock.debugPrint
import ohior.app.mediarock.generateID
import ohior.app.mediarock.model.MovieItem
import ohior.app.mediarock.model.MovieItemFolder
import ohior.app.mediarock.service.AppDatabase


class LocalMovieScreenLogic : ViewModel() {
//    val localMovieList: StateFlow<List<MovieItem>> =
//        AppDatabase.getAllLocalMovies().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _localMovieList: List<MovieItem> = AppDatabase.allLocalMovies()
    var localMovieList by mutableStateOf<List<MovieItem>>(emptyList())
    var displayFolder by mutableStateOf(true)

    fun localMovieFolderList(): List<MovieItemFolder> {
        debugPrint("DEBUG : localMovieFolderList")
        val tempMovieItemFolder = mutableListOf<MovieItemFolder>()
        for (listItem in _localMovieList) {
            if (!tempMovieItemFolder.any { it.name == listItem.folderName }) {
                tempMovieItemFolder.add(
                    MovieItemFolder(
                        itemId = generateID(),
                        name = listItem.folderName,
                        movies = _localMovieList.filter { it.folderName == listItem.folderName }
                    )
                )
            }
        }
        return tempMovieItemFolder
    }


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

//
//    fun getAllVideoFiles(context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val query = context.contentResolver.query(
//                collection,
//                projection,
//                null,
//                null,
//                sortOrder
//            )
//            query?.use { cursor ->
//                // Cache column indices.
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
//                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
//                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
//                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//                val modifiedColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
//                var count = 0
//                while (cursor.moveToNext()) {
//                    count++
//                    // Get values of columns for a given video.
//                    val id = cursor.getLong(idColumn)
//                    val name = cursor.getString(nameColumn)
//                    val duration = cursor.getInt(durationColumn)
//                    val size = cursor.getInt(sizeColumn)
//                    val path = cursor.getString(pathColumn)
//                    val modified = cursor.getInt(modifiedColumn)
//
//                    // Stores column values, the contentUri, and the thumbnail in a local object.
//                    val movieItem = MovieItem(
//                        itemId = id,
//                        name = name,
//                        duration = convertLongToTime(duration.toLong()),
//                        size = formatFileSize(size.toLong()),
//                        lastModified = convertLongToTime(modified.toLong(), true),
//                        path = path,
////                        thumbnail = thumbnail
//                    )
//                    if (!AppDatabase.allLocalMovies().any { it.itemId == movieItem.itemId }) {
//                        AppDatabase.addLocalMovie(movieItem)
//                    }
//                }
//                debugPrint("Local movie count: $count")
////                val movies = AppDatabase.allLocalMovies()
////                for (movie in movies) {
////                }
//            }
//        }
//    }

//    fun getMoviesFolder(): List<MovieItemFolder>{
//        var folderName = ""
//        for (movie in localMovieList){
//            folderName = movie.path.split("/")(2)
//        }
//    }

    //    fun getMovieFolderList(): List<MovieItemFolder> {
//        val folderList = mutableListOf<MovieItemFolder>()
//        for (movie in localMovieList){
//            for (folder in folderList){
//                if (folder.name == movie.name){
//                    val newFolder = MovieItemFolder(
//                        name = movie.getFolderName(),
//                        movies =
//                    )
//                }
//            }
//        }
//    }
}