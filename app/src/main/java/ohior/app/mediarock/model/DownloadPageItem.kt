package ohior.app.mediarock.model

data class DownloadPageItem(
    val imageUrl: String,
    val movieInfoList: List<Pair<String, String>>,
    val downloadUrl: String,
    val description: String
)
