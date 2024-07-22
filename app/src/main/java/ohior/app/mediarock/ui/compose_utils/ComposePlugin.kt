package ohior.app.mediarock.ui.compose_utils

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
import ohior.app.mediarock.ui.theme.primaryFontFamily
import ohior.app.mediarock.utils.LocalMovieType
import ohior.app.mediarock.utils.OnboardType
import ohior.app.mediarock.utils.OnlineMovieType
import ohior.app.mediarock.utils.PdfViewType
import ohior.app.mediarock.utils.ScreenHolder
import ohior.app.mediarock.utils.VideoType
import ohior.app.mediarock.whenNotNull


@Composable
fun DisplayLottieAnimation(
    modifier: Modifier,
    resId: Int,
    lottieSize: Dp = 300.dp,
    text: String? = null,
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
        fontFamily = primaryFontFamily,
        textAlign = TextAlign.Center
    )
) {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId)
    )
    val progressLottie by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
        speed = 0.5F
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier.size(lottieSize),
            composition = lottieComposition,
            progress = { progressLottie }
        )
        text.whenNotNull { Text(text = it, style = style) }
    }
}


@Composable
fun RichText(
    modifier: Modifier = Modifier,
    richTextList: List<RichTextModel>,
    textAlign: TextAlign? = null
) {
    Text(
        modifier = modifier,
        textAlign = textAlign,
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
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
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
fun BottomBarNavigation(
    navController: NavHostController,
) {
    val bottomBar = listOf(
        ScreenHolder(
            screen = OnlineMovieType,
            imageVector = Icons.Outlined.Web,
            contentDescription = "Online movies"
        ),
        ScreenHolder(
            screen = LocalMovieType,
            imageVector = Icons.Outlined.Movie,
            contentDescription = "Local movies"
        ),
        ScreenHolder(
            screen = PdfViewType,
            imageVector = Icons.Outlined.PictureAsPdf,
            contentDescription = "PDF reader"
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    currentDestination.whenNotNull { curDes ->
        if (
            !curDes.hierarchy.any { it.hasRoute(VideoType::class) } &&
            !curDes.hierarchy.any { it.hasRoute(OnboardType::class) }
        ) {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.surface.copy(
                    blue = 0.2f,
                    red = 0.2f,
                    green = 0.2f
                )
            ) {
                bottomBar.forEach { bb ->
                    BottomNavigationItem(
//                        modifier = Modifier.weight(1F),
                        alwaysShowLabel = !curDes.hierarchy.any { it.route == bb.screen.javaClass.name },
                        enabled = !curDes.hierarchy.any { it.route == bb.screen.javaClass.name },
                        selected = false,//curDes.hierarchy.any { it.route == bb.screen.javaClass.name },
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
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun RotateScreenButton(modifier: Modifier = Modifier, isVertical: Boolean) {
    val context = LocalContext.current
    IconButton(onClick = {
        (context as Activity).requestedOrientation = if (isVertical) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }, modifier = modifier) {
        Icon(Icons.Filled.ScreenRotation, contentDescription = "Rotate screen", tint = White)
    }
}

@Composable
fun CreateLinearProgressBar(
    colors: List<Color>,
    depth: Dp = 10.dp,
    speedMillis: Int = 2000
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(depth)
            .createShimmer(colors, speedMillis = speedMillis)
    )
}

fun Modifier.createShimmer(colors: List<Color>, speedMillis: Int = 2000): Modifier = composed {
    val size = remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "shimmer remember animation transition")
    val startOffsetX = transition.animateFloat(
        initialValue = -2 * size.value.width.toFloat(),
        targetValue = 2 * size.value.width.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = speedMillis)),
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
    ) {
        content()
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.Red,
            contentColor = Color.Yellow
        )

    }
}