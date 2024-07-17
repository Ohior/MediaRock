package ohior.app.mediarock.service

import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ohior.app.mediarock.model.WebPageItem
import ohior.app.mediarock.model.WebPageItem_


object AppDatabase {
    private val objectBox = ObjectBox.store.boxFor(WebPageItem::class)
    var count: Long = objectBox.count()

    // Extension function to convert Query to Flow
    private fun <T> Query<T>.asFlow(): Flow<List<T>> = callbackFlow {
        val subscription = this@asFlow.subscribe().observer { data ->
            trySend(data).isSuccess
        }
        awaitClose { subscription.cancel() }
    }

    fun allMovies(): List<WebPageItem> = objectBox.all
    fun getAllMovies(): Flow<List<WebPageItem>> {
        return objectBox.query().build().asFlow()
    }

    fun addMovie(movie: WebPageItem): Long = objectBox.put(movie)

    fun deleteMovie(movie: WebPageItem): Boolean = objectBox.remove(movie)

    fun updateMovie(movie: WebPageItem): Long = objectBox.put(movie)

    fun getMoviesByKey(movieKey: String): MutableList<WebPageItem> {
        val query = objectBox
            .query(WebPageItem_.key.equal(movieKey))
            .order(WebPageItem_.title)
            .build()
        val results = query.find()
        query.close()
        return results
    }
}