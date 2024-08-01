package ohior.app.mediarock.ui.screens.web_movie_item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ohior.app.mediarock.utils.ActionState
import ohior.app.mediarock.utils.WebMovieItemScreenType
import ohior.app.mediarock.whenNotNull
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class WebMovieItemScreenLogic : ViewModel() {
    var imageUrl by mutableStateOf<String?>(null)
    private var _downloadUrlList = mutableStateListOf<Pair<String, String>>()
    private var _movieInfoList = mutableStateListOf<Pair<String, String>>()
    val downloadUrlList: List<Pair<String, String>> = _downloadUrlList
    val movieInfoList: List<Pair<String, String>> = _movieInfoList
    var isContentLoaded by mutableStateOf<ActionState>(ActionState.None)

    var isPageRefreshing by mutableStateOf(false)
        private set

    fun setIsPageRefreshing(boolean: Boolean){
        isPageRefreshing = boolean
    }


    fun loadDownloadPage(webMovieItemScreenType: WebMovieItemScreenType) {
        viewModelScope.launch(Dispatchers.IO) {
            isContentLoaded = ActionState.Loading
            _downloadUrlList.clear()
            _movieInfoList.clear()
            try {
                webMovieItemScreenType.downloadUrl.whenNotNull { downloadUrl ->
                    Jsoup.connect(downloadUrl).get().let{ document ->
                        val iu = async { getImageUrl(document) }
                        val du = async { getDownloadUrl(document) }
                        val mi = async { getMovieInfo(document) }
                        imageUrl = iu.await()
                        _downloadUrlList.addAll(du.await())
                        _movieInfoList.addAll(mi.await())
                    }
                    isContentLoaded = ActionState.Success
                }
            } catch (e: Exception) {
                isContentLoaded = ActionState.Fail(message = "There has been a critical error on this page.")
            }
            isPageRefreshing = false
        }
    }

    private suspend fun getImageUrl(bodyItem: Document): String? {
        return withContext(Dispatchers.IO) {
            var imageUrl: String? = null
            val imageClass: Elements = bodyItem.getElementsByTag("img")
                .attr("fetchpriority", "high")
            for (imageC in imageClass) {
                if (!imageC.hasClass("avatar") &&
                    !imageC.hasClass("attachment-jannah-image-large") &&
                    imageC.hasClass("aligncenter")
                ) {
                    imageUrl = imageC.attr("src")
                    break
                }
            }
            return@withContext imageUrl
        }
    }

    private suspend fun getDownloadUrl(bodyItem: Document): MutableList<Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            val downloadPair: MutableList<Pair<String, String>> = mutableListOf()
            val elements = bodyItem.getElementsByTag("p")
            elements.forEach { element ->
                if (element.getElementsByTag("a").hasClass("fa-fa-download")) {
                    val text = element.text().replace("\\[.*?]".toRegex(), "").trim()
                    val link = element.getElementsByTag("a")
                        .attr("href")//element.children().last()?.attr("href")
                    downloadPair.add(Pair(link, text.lowercase()))
                }
            }
            return@withContext downloadPair
        }
    }


    private suspend fun getMovieInfo(bodyItem: Document): List<Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            val pairs = mutableListOf<Pair<String, String>>()
            val elements: Elements = bodyItem.getElementsByTag("blockquote")
            elements.forEach { element ->
                val lines = element.text().split(" ") // Split the string into lines
                var pairPart = ""
                var pairKey = ""
                var newPair = true
                for (line in lines) {
                    if (line.endsWith(":")) {
                        if (!newPair) {
                            pairs.add(
                                Pair(
                                    pairKey.replace("-", " "),
                                    pairPart.replace("-", " ")
                                )
                            )
                            pairPart = ""
                        }
                        pairKey = "$line "
                    } else {
                        val videoRegex = Regex(
                            """.*\.(mp4|avi|mov|mkv|flv|wmv|webm|mpeg|3gp|mpg|m4v)$""",
                            RegexOption.IGNORE_CASE
                        )
                        newPair = false
                        pairPart += "${line.replace(Regex("\\[.*]"), "")} "
                    }
                }
            }
            return@withContext pairs
        }
    }
}