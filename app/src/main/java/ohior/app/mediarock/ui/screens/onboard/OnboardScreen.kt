package ohior.app.mediarock.ui.screens.onboard

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import ohior.app.mediarock.R
import ohior.app.mediarock.service.PermissionManager
import ohior.app.mediarock.utils.OnboardType
import ohior.app.mediarock.utils.OnlineMovieType

@Composable
fun OnboardScreen(navHostController: NavHostController) {
    var isPermGranted by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            if (result.containsValue(false)) {
                Toast.makeText(
                    context,
                    "Permissions Are needed for app to function properly",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                isPermGranted = true
            }
        }
    )
    LaunchedEffect(isPermGranted) {
        PermissionManager.checkPermissionsGranted(context) { b, perm ->
            if (b) {
                delay(3000)
                navHostController.navigate(OnlineMovieType) {
                    popUpTo(OnboardType) { inclusive = true }
                }
            } else permissionLauncher.launch(perm.toTypedArray())
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Icon(
            modifier = Modifier.size(200.dp),
            tint = MaterialTheme.colorScheme.onBackground,
            painter = painterResource(R.drawable.emoji_nature_48px),
            contentDescription = "App icon"
        )
    }
}