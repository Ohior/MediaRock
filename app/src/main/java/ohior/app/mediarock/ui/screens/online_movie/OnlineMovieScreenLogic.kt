package ohior.app.mediarock.ui.screens.online_movie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ohior.app.mediarock.debugPrint
import ohior.app.mediarock.model.WebPageItem
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.utils.ActionState
import org.jsoup.Jsoup
import java.net.ConnectException
import java.nio.channels.UnresolvedAddressException

class OnlineMovieScreenLogic : ViewModel() {
    private var _webPageList = mutableStateListOf<WebPageItem>()
//    private var _databaseList = mutableStateListOf<WebPageItem>()
    val databaseList: StateFlow<List<WebPageItem>> = AppDatabase.getAllMovies().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private val webAddress = "https://9jarocks.net/"
    private val httpClient = HttpClient(CIO) {
        engine {
            requestTimeout = 0
        }
    }
    val webPageList: List<WebPageItem> = _webPageList
//    val databaseList: List<WebPageItem> = _databaseList
    var webPageListState by mutableStateOf<ActionState>(ActionState.None)

    init {
        initWebPageList()
    }

    fun initWebPageList() {
        viewModelScope.launch {
            _webPageList.clear()
//            _databaseList.addAll(AppDatabase.getAllMovies())
            loadWebItems()
        }
    }

    private suspend fun loadWebItems() {
        withContext(Dispatchers.IO) {
            try {
                httpClient.use { client ->
                    val count = AppDatabase.count + 1
                    val response = client.get(webAddress)
                    val doc = Jsoup.parse(response.bodyAsText())
                    doc.getElementsByClass("slide").forEach { slide ->
                        slide.getElementsByClass("grid-item").forEachIndexed { index, gridItem ->
                            val description = gridItem.getElementsByClass("thumb-desc").text()
                            val title: String = gridItem.getElementsByClass("thumb-title").text()
                            val movieUrl: String = gridItem.getElementsByTag("a")
                                .attr("href") // .attr("href")
                            val movieId: String? =
                                """-id(\w*)""".toRegex().find(movieUrl)?.value?.removePrefix("-")
//                        val lastUpdated = gridItem.getElementsByClass("thumb-meta").text()
                            val imageStyle = gridItem.attr("style")//, "background-image")
                            val regex = """\((.*?)\)""".toRegex()
                            val imageMatch = regex.find(imageStyle)
                            val imageUrl: String? = imageMatch?.value?.removeSurrounding(
                                "(",
                                ")"
                            )//?.groups?.get(1)?.value
                            _webPageList.add(
                                WebPageItem(
                                    id = count + index.toLong(),
                                    itemId = movieId,
                                    description = description,
                                    title = title,
                                    imageUrl = imageUrl,
                                    pageUrl = movieUrl,
                                    key = null
                                )
                            )
                            if (!AppDatabase.allMovies().any { it.itemId == movieId }) {
                                AppDatabase.addMovie(
                                    WebPageItem(
                                        itemId = movieId,
                                        description = description,
                                        title = title,
                                        imageUrl = imageUrl,
                                        pageUrl = movieUrl,
                                        key = null
                                    )
                                )
                            }
                        }
                    }
                }
                webPageListState = if (_webPageList.isEmpty()) ActionState.Fail(message = "Got empty movies from web") else ActionState.Success
            } catch (cte: UnresolvedAddressException) {
                webPageListState = ActionState.Fail(message = "the url is not resolved")
            } catch (cte: ConnectException) {
                webPageListState = ActionState.Fail(message = "Error connecting to the internet")
                debugPrint("DEBUG : "+cte.message.toString())
            } catch (cte: ConnectTimeoutException) {
                webPageListState = ActionState.Fail(message = "There was a connection timeout")
            }

        }

    }
}
