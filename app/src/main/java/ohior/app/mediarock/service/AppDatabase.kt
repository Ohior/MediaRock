package ohior.app.mediarock.service

import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ohior.app.mediarock.model.MovieItem
import ohior.app.mediarock.model.WebPageItem
import ohior.app.mediarock.model.WebPageItem_


object AppDatabase {
    private val objectBox = ObjectBox.store.boxFor(WebPageItem::class)
    var count: Long = objectBox.count()

    private val localMovieObjectBox = ObjectBox.store.boxFor(MovieItem::class)
    var localMovieCount: Long = localMovieObjectBox.count()

    // Extension function to convert Query to Flow
    private fun <T> Query<T>.asFlow(): Flow<List<T>> = callbackFlow {
        val subscription = this@asFlow.subscribe().observer { data ->
            trySend(data).isSuccess
        }
        awaitClose { subscription.cancel() }
    }

    //*********************************************************************************************
    // FOR ONLINE MOVIES
    fun allMovies(): List<WebPageItem> = objectBox.all
    fun getAllMovies(): Flow<List<WebPageItem>> {
        return objectBox.query().build().asFlow()
    }

    fun addMovie(movie: WebPageItem): Long = objectBox.put(movie)

    fun deleteMovie(movie: WebPageItem): Boolean = objectBox.remove(movie)

    fun updateMovie(movie: WebPageItem): Long = objectBox.put(movie)

    fun getMoviesByKey(movieId: String): MutableList<WebPageItem> {
        val query = objectBox
            .query(WebPageItem_.id.equal(movieId))
            .order(WebPageItem_.title)
            .build()
        val results = query.find()
        query.close()
        return results
    }
    //*********************************************************************************************
    // FOR LOCAL MOVIES
    fun allLocalMovies(): List<MovieItem> = localMovieObjectBox.all
    fun getAllLocalMovies(): Flow<List<MovieItem>> {
        return localMovieObjectBox.query().build().asFlow()
    }

    fun addLocalMovie(movie: MovieItem): Long = localMovieObjectBox.put(movie)

    fun deleteLocalMovie(movie: MovieItem): Boolean = localMovieObjectBox.remove(movie)
    fun deleteManyLocalMovie(movie: List<MovieItem>): Unit = localMovieObjectBox.remove(movie)

    //*********************************************************************************************
}
