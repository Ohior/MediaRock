package ohior.app.mediarock.ui.screens.online_movie

import androidx.compose.runtime.collectAsState
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
import kotlin.time.Duration.Companion.minutes

class OnlineMovieScreenLogic : ViewModel() {
    private var _webPageList = mutableStateListOf<WebPageItem>()
    val databaseList: StateFlow<List<WebPageItem>> = AppDatabase.getAllMovies().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private val webAddress = "https://9jarocks.net/"
    private val httpClient = HttpClient(CIO) {
        engine {
            requestTimeout = 1.minutes.inWholeMilliseconds
        }
    }
    val webPageList: List<WebPageItem> = _webPageList
    var webPageListState by mutableStateOf<ActionState>(ActionState.None)

    init {
        initWebPageList()
    }

    private fun initWebPageList() {
        viewModelScope.launch {
            _webPageList.clear()
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
                            val description = gridItem.getElementsByClass("thumb-desc").text().replace("\\[.*?]".toRegex(), "").trim()
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
                            val webPageItem = WebPageItem(
                                itemId = movieId ?:System.nanoTime().toString(),
                                description = description,
                                title = title,
                                imageUrl = imageUrl,
                                pageUrl = movieUrl
                            )
                            _webPageList.add(webPageItem)
                            if (!AppDatabase.allMovies().any { it.itemId == movieId }) {
                                AppDatabase.addMovie(webPageItem)
                            }
                        }
                    }
                }
                webPageListState = if (_webPageList.isEmpty()) ActionState.Fail(message = "Got empty movies from web") else ActionState.Success
            } catch (cte: UnresolvedAddressException) {
                webPageListState = ActionState.Fail(message = "can't get online movies")
            } catch (cte: ConnectException) {
                webPageListState = ActionState.Fail(message = "Error connecting to the internet")
            } catch (cte: ConnectTimeoutException) {
                webPageListState = ActionState.Fail(message = "There was a connection timeout")
            }

        }

    }
}
