package ohior.app.mediarock.model

import ohior.app.mediarock.convertLongToTime
import ohior.app.mediarock.formatFileSize

data class MovieItemFolder(
    val itemId: Long,
    val name: String,
    val movies: List<MovieItem> = emptyList()
){
    fun getSizeAndDuration():Pair<String,String> {
        var totalSize:Long = 0
        var totalDuration:Long = 0
        for (movie in movies) {
            totalSize += movie.sizeLong
            totalDuration += movie.durationLong
        }

        return Pair(formatFileSize(totalSize), convertLongToTime(totalDuration, true))
    }
}
