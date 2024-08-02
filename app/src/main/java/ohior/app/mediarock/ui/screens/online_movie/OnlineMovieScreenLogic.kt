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
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ohior.app.mediarock.model.WebPageItem
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.utils.ActionState
import org.jsoup.Jsoup
import java.net.ConnectException
import java.nio.channels.UnresolvedAddressException
import kotlin.time.Duration.Companion.minutes

class OnlineMovieScreenLogic : ViewModel() {

    // Original StateFlow of list of WebPageItem
    private val _databaseList: StateFlow<List<WebPageItem>> =
        AppDatabase.getAllMovies().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered StateFlow
    private val _filteredDatabaseList = MutableStateFlow<List<WebPageItem>>(emptyList())
    val filteredDatabaseList: StateFlow<List<WebPageItem>> = _filteredDatabaseList.asStateFlow()


    private val webAddress = "https://9jarocks.net/"
    private var _webPageList = mutableStateListOf<WebPageItem>()
    val webPageList: List<WebPageItem> = _webPageList
    var webPageListState by mutableStateOf<ActionState>(ActionState.None)
    var searchValue by mutableStateOf("")
    var isPageRefreshing by mutableStateOf(false)

    init {
        initWebPageList()
    }

    fun onSearchValueChanged(search: String) {
        searchValue = search
        viewModelScope.launch {
            _databaseList
                .map { list ->
                    if (search.isNotEmpty()) {
                        list.filter { webPageItem ->
                            // Your filtering condition, e.g., only include items with id greater than 10
                            webPageItem.title.contains(search, true)
                        }
                    }else list
                }
                .collect { filteredList ->
                    _filteredDatabaseList.value = filteredList
                }
        }
    }

    fun initWebPageList() {
        onSearchValueChanged("")
        viewModelScope.launch {
            _webPageList.clear()
            loadWebItems()
            isPageRefreshing = false
        }
    }

    private suspend fun loadWebItems() {
        withContext(Dispatchers.IO) {
            val httpClient = HttpClient(CIO) {
                engine {
                    requestTimeout = 1.minutes.inWholeMilliseconds
                }
            }
            try {
                httpClient.use { client ->
                    webPageListState = ActionState.Loading
                    val response = client.get(webAddress)
                    val doc = Jsoup.parse(response.bodyAsText())
                    doc.getElementsByClass("slide").forEach { slide ->
                        slide.getElementsByClass("grid-item").forEachIndexed { _, gridItem ->
                            val description = gridItem.getElementsByClass("thumb-desc").text()
                                .replace("\\[.*?]".toRegex(), "").trim()
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
                                itemId = movieId ?: System.nanoTime().toString(),
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

                webPageListState =
                    if (_webPageList.isEmpty()) ActionState.Fail(message = "Got empty movies from web") else ActionState.Success
//                if (newData){
//                    _onlineDatabaseList.clear()
//                    _onlineDatabaseList.addAll(AppDatabase.allMovies())
//                }
            } catch (cte: UnresolvedAddressException) {
                webPageListState = ActionState.Fail(message = "can't get online movies")
            } catch (cte: ConnectTimeoutException) {
                webPageListState = ActionState.Fail(message = "There was a connection timeout")
            } catch (cte: ConnectException) {
                webPageListState = ActionState.Fail(message = "Error connecting to the internet")
            } catch (cte: HttpRequestTimeoutException) {
                webPageListState = ActionState.Fail(message = "Request timeout has expired")

            }
        }
    }
}
