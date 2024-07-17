package ohior.app.mediarock.ui.compose_utils

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ohior.app.mediarock.model.RichTextModel
import ohior.app.mediarock.ui.theme.White
import ohior.app.mediarock.utils.LocalMovieType
import ohior.app.mediarock.utils.OnlineMovieType
import ohior.app.mediarock.utils.PdfViewType
import ohior.app.mediarock.utils.ScreenHolder
import ohior.app.mediarock.utils.VideoType
import ohior.app.mediarock.whenNotNull


@Composable
fun DisplayLottieAnimation(modifier: Modifier, resId: Int) {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId)
    )
    val progressLottie by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
        speed = 0.5F
    )
    LottieAnimation(
        modifier = modifier,
        composition = lottieComposition,
        progress = { progressLottie }
    )
}


@Composable
fun RichText(modifier: Modifier = Modifier, richTextList: List<RichTextModel>) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onPrimary
        ),
        text = buildAnnotatedString {
            for (richText in richTextList) {
                withStyle(
                    style = richText.spanStyle
                ) {
                    append(richText.text)
                }
            }
        }
    )
}

@Composable
fun AppLifecycleObserver(
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {},
    onAny: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate()
                Lifecycle.Event.ON_STOP -> onStop()
                Lifecycle.Event.ON_PAUSE -> onPause()
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_ANY -> onAny()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun BottomNavigationButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.clickable { onClick() }
    ) {
        icon()
        if (!selected) label() else Spacer(modifier = Modifier)
    }
}

@Composable
fun BottomBarNavigation(
    navController: NavHostController,
) {
    val bottomBar = listOf(
        ScreenHolder(
            screen = OnlineMovieType,
            imageVector = Icons.Filled.Web,
            contentDescription = "Online movies"
        ),
        ScreenHolder(
            screen = LocalMovieType,
            imageVector = Icons.Filled.Movie,
            contentDescription = "Local movies"
        ),
        ScreenHolder(
            screen = PdfViewType,
            imageVector = Icons.Filled.PictureAsPdf,
            contentDescription = "PDF reader"
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    currentDestination.whenNotNull { curDes ->
        if (!curDes.hierarchy.any { it.hasRoute(VideoType::class) }) {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.surface.copy(
                    blue = 0.2f,
                    red = 0.2f,
                    green = 0.2f
                )
            ) {
                bottomBar.forEach { bb ->
                    BottomNavigationButton(
                        modifier = Modifier.weight(1F),
                        selected = curDes.hierarchy.any { it.route == bb.screen.javaClass.name },
                        onClick = {
                            navController.navigate(bb.screen) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                // Avoid multiple copies of the same destination when
                                // re-selecting the same item
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = { Icon(bb.imageVector, contentDescription = bb.contentDescription) },
                        label = {
                            Text(
                                bb.contentDescription,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun RotateScreenButton(modifier: Modifier = Modifier) {
    val isVertical = remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    IconButton(onClick = {
        isVertical.value = !isVertical.value
        (context as Activity).requestedOrientation = if (isVertical.value) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }, modifier = modifier) {
        Icon(Icons.Filled.ScreenRotation, contentDescription = "Rotate screen", tint = White)
    }
}

fun Modifier.createShimmer(colors: List<Color>): Modifier = composed {
    val size = remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "shimmer remember animation transition")
    val startOffsetX = transition.animateFloat(
        initialValue = -2 * size.value.width.toFloat(),
        targetValue = 2 * size.value.width.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 2000)),
        label = "shimmer animation"
    )
    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(startOffsetX.value, 0f),
            end = Offset(
                startOffsetX.value + size.value.width.toFloat(),
                size.value.height.toFloat()
            ),
        )
    )
        .onGloballyPositioned { size.value = it.size }
}