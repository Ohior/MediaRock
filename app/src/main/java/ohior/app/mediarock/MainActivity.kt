package ohior.app.mediarock

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.service.FileManager
import ohior.app.mediarock.ui.compose_utils.BottomBarNavigation
import ohior.app.mediarock.ui.screens.download.DownloadScreen
import ohior.app.mediarock.ui.screens.local_movie.LocalMovieScreen
import ohior.app.mediarock.ui.screens.onboard.OnboardScreen
import ohior.app.mediarock.ui.screens.online_movie.OnlineMovieScreen
import ohior.app.mediarock.ui.screens.pdfview.PdfViewScreen
import ohior.app.mediarock.ui.screens.video.VideoScreen
import ohior.app.mediarock.ui.screens.video.VideoScreenLogic
import ohior.app.mediarock.ui.screens.web_movie_item.WebMovieItemScreen
import ohior.app.mediarock.ui.screens.web_movie_item.WebMovieItemScreenLogic
import ohior.app.mediarock.ui.theme.MediaRockTheme
import ohior.app.mediarock.utils.DownloadType
import ohior.app.mediarock.utils.LocalMovieType
import ohior.app.mediarock.utils.OnboardType
import ohior.app.mediarock.utils.OnlineMovieType
import ohior.app.mediarock.utils.PdfViewType
import ohior.app.mediarock.utils.VideoType
import ohior.app.mediarock.utils.WebMovieItemScreenType


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        installSplashScreen()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                FileManager.saveVideoAndModifyToDatabase(this@MainActivity)
            } catch (e: NullPointerException) {
                AppDatabase.deleteAllLocalMovie()
                FileManager.saveVideoAndModifyToDatabase(this@MainActivity)
                Toast.makeText(this@MainActivity,
                    "An error occurred. Recreating movies database",Toast.LENGTH_LONG).show()
            }
        }
        setContent {
            MediaRockTheme {
                val navController = rememberNavController()
                Scaffold(bottomBar = {
                    BottomBarNavigation(
                        navController,
                    )
                }) { pv ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pv),
                    ) {
                        NavigationComponent(navController)
                    }
                }
            }
        }
    }

    @Composable
    fun NavigationComponent(navController: NavHostController) {
        NavHost(navController, startDestination = OnboardType) {
            composable<OnlineMovieType> {
                OnlineMovieScreen(navController)
            }
            composable<LocalMovieType> {
                LocalMovieScreen(navController)
            }
            composable<VideoType> {
                val viewModel = viewModel<VideoScreenLogic>()
                VideoScreen(viewModel, it.toRoute<VideoType>(), navController)
            }
            composable<DownloadType> {
                DownloadScreen(it.toRoute<DownloadType>())
            }
            composable<WebMovieItemScreenType> {
                val viewModel = viewModel<WebMovieItemScreenLogic>()
                WebMovieItemScreen(viewModel, it.toRoute<WebMovieItemScreenType>(), navController)
            }
            composable<PdfViewType> {
                PdfViewScreen(navController)
            }
            composable<OnboardType> {
                OnboardScreen(navHostController = navController)
            }
        }
    }

}
