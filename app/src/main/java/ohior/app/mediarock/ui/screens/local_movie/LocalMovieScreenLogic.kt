package ohior.app.mediarock.ui.screens.local_movie

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ohior.app.mediarock.model.MovieItem
import ohior.app.mediarock.utils.ActionState


class LocalMovieScreenLogic : ViewModel() {
    private var _movieItemList = mutableStateListOf<MovieItem>()
    val movieItemList: List<MovieItem> = _movieItemList

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

    // Show only videos that are at least 5 minutes in duration.
//    private val selection = "${MediaStore.Video.Media.DURATION} >= ?"
//    private val selectionArgs = arrayOf(
//        TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
//    )

    // Display videos in alphabetical order based on their display name.
    private val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

//    private fun getAllVideoFiles(context: Context): ArrayList<MovieItem> {
//        val movieItemList: ArrayList<MovieItem> = ArrayList()
//        val query = context.contentResolver.query(
//            collection,
//            projection,
//            selection,
//            selectionArgs,
//            sortOrder
//        )
//        query?.use { cursor ->
//            // Cache column indices.
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
//            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
//            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
//            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            val modifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
//            while (cursor.moveToNext()) {
//                // Get values of columns for a given video.
//                val id = cursor.getLong(idColumn)
//                val name = cursor.getString(nameColumn)
//                val duration = cursor.getInt(durationColumn)
//                val size = cursor.getInt(sizeColumn)
//                val path = cursor.getString(pathColumn)
//                val modified = cursor.getInt(modifiedColumn)
//
//                val contentUri: Uri = ContentUris.withAppendedId(
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    id
//                )
//
//                // Stores column values and the contentUri in a local object
//                // that represents the media file.
////                videoList += MovieItem(contentUri, name, duration, size)
//                movieItemList.add(
//                    MovieItem(
//                        name = name,
//                        duration = duration,
//                        size = size,
//                        lastModified = modified,
//                        path = path
//                    )
//                )
//            }
//        }
//        return movieItemList
//    }

//    fun getAllVideoFiles(context: Context): MutableList<MovieItem> {
//        val movieItemList = mutableListOf<MovieItem>()
//        val query = context.contentResolver.query(
//            collection,
//            projection,
//            null,
//            null,
//            sortOrder
//        )
//        query?.use { cursor ->
//            // Cache column indices.
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
//            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
//            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
//            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            val modifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
//            while (cursor.moveToNext()) {
//                // Get values of columns for a given video.
//                val id = cursor.getLong(idColumn)
//                val name = cursor.getString(nameColumn)
//                val duration = cursor.getInt(durationColumn)
//                val size = cursor.getInt(sizeColumn)
//                val path = cursor.getString(pathColumn)
//                val modified = cursor.getInt(modifiedColumn)
//
//                val contentUri: Uri = ContentUris.withAppendedId(
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    id
//                )
//
//                // Stores column values and the contentUri in a local object
//                // that represents the media file.
////                videoList += MovieItem(contentUri, name, duration, size)
//                movieItemList.add(
//                    MovieItem(
//                        name = name,
//                        duration = duration,
//                        size = size,
//                        lastModified = modified,
//                        path = path
//                    )
//                )
//            }
//        }
//
//        return movieItemList
//    }


    fun getAllVideoFiles(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            movieItemListState = ActionState.Loading
            _movieItemList.clear()
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

                    // Get the video thumbnail.
                    val thumbnail: Bitmap? = ThumbnailUtils.createVideoThumbnail(
                        path,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )

                    // Stores column values, the contentUri, and the thumbnail in a local object.
                    _movieItemList.add(
                        MovieItem(
                            itemId = id,
                            name = name,
                            duration = duration,
                            size = size,
                            lastModified = modified,
                            path = path,
                            thumbnail = thumbnail
                        )
                    )
                }
            }
            movieItemListState =
                if (_movieItemList.isEmpty()) ActionState.Fail(message = "Could not get movie from device") else ActionState.Success
        }
    }

    private fun getAllPermissionsString(): Array<String> {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.addAll(
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.addAll(
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return permissions.toTypedArray()
    }

    fun isPermissionsGranted(context: Context, content: (Boolean, MutableList<String>) -> Unit) {
        val permissionsList = mutableListOf<String>()
        for (permission in getAllPermissionsString()) {
            if (ContextCompat.checkSelfPermission(context, permission) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                permissionsList.add(permission)
            }
        }
        content(permissionsList.isEmpty(), permissionsList)
    }
}