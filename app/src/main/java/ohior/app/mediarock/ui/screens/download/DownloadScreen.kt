package ohior.app.mediarock.ui.screens.download


import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import ohior.app.mediarock.ui.theme.DeepSize
import ohior.app.mediarock.ui.theme.primaryFontFamily
import ohior.app.mediarock.utils.DownloadType

private fun handleDownloads(url: String, context: Context, mimeType: String, userAgent: String) {
    val request = DownloadManager.Request(Uri.parse(url)).apply {
        setMimeType(mimeType)
        addRequestHeader("User-Agent", userAgent)
        setDescription("Downloading file...")
        setTitle(Uri.parse(url).lastPathSegment)
        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            Uri.parse(url).lastPathSegment
        )
    }
    val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
    Toast.makeText(context, "download don start!", Toast.LENGTH_SHORT).show()
}

@Composable
fun DownloadScreen(downloadType: DownloadType) {
    val isPageLoading = remember {
        mutableStateOf(true)
    }
    val webClient = remember {
        object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isPageLoading.value = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isPageLoading.value = false
            }
        }
    }
    if (isPageLoading.value) {
        AlertDialog(
            contentColor = MaterialTheme.colorScheme.background,
            backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
            title = {
                Text(
                    text = "wait!😱 wait! 📢 wait!😱",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = primaryFontFamily,
                        fontWeight = FontWeight.Bold,
                    )
                )
            },
            text = { LinearProgressIndicator() },
            onDismissRequest = { isPageLoading.value = false },
            buttons = {})
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(DeepSize.Small)
                .background(MaterialTheme.colorScheme.surface),
            text = "Credits to 9jarocks.net",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        AndroidView(modifier = Modifier.weight(1f), factory = {
            WebView(it).apply {
                settings.apply {
                    javaScriptEnabled = true
                    loadsImagesAutomatically = false
                    blockNetworkImage = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    domStorageEnabled = true
                    builtInZoomControls = true
                    displayZoomControls = false
                    userAgentString =
                        "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36"
                }
                webViewClient = webClient
                webChromeClient = WebChromeClient()
                // Handle download requests
                setDownloadListener { url, userAgent, _, mimeType, _ ->
                    handleDownloads(
                        url = url,
                        userAgent = userAgent,
                        mimeType = mimeType,
                        context = context
                    )
                }
                loadUrl(downloadType.downloadUrl)
//                loadData(
//                    Base64.encodeToString(
//                        downloadScreenType.downloadPage.toByteArray(),
//                        Base64.NO_PADDING
//                    ), "text/html", "base64"
//                )
            }
        })
    }
}