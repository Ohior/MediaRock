package ohior.app.mediarock.ui.screens.onboard

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ohior.app.mediarock.R
import ohior.app.mediarock.service.AppDatabase
import ohior.app.mediarock.service.FileManager
import ohior.app.mediarock.service.PermissionManager
import ohior.app.mediarock.ui.theme.primaryFontFamily
import ohior.app.mediarock.utils.OnboardType
import ohior.app.mediarock.utils.OnlineMovieType

@Composable
fun OnboardScreen(navHostController: NavHostController) {
    var isPermGranted by remember {
        mutableStateOf(false)
    }
    var showPermissionPopup by remember {
        mutableStateOf(true)
    }
    var permissions = arrayOf<String>()

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
            showPermissionPopup = !b
            permissions = perm.toTypedArray()
            if (b) {
                try {
                    FileManager.saveVideoAndModifyToDatabase(context)
                } catch (e: NullPointerException) {
                    AppDatabase.deleteAllLocalMovie()
                    FileManager.saveVideoAndModifyToDatabase(context)
                    Toast.makeText(
                        context,
                        "An error occurred recreating movies database", Toast.LENGTH_LONG
                    ).show()
                } finally {
                    navHostController.navigate(OnlineMovieType) {
                        popUpTo(OnboardType) { inclusive = true }
                    }
                }
            }
        }
    }

    if (showPermissionPopup) {
        AlertDialog(
            onDismissRequest = {
                Toast.makeText(
                    context,
                    "Permissions Are needed for app to function properly",
                    Toast.LENGTH_LONG
                ).show()
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionPopup = false
                    permissionLauncher.launch(permissions)
                }) {
                    Text(
                        text = "Grant Permission",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                    )
                }
            },
            backgroundColor = Color.White,
            shape = RoundedCornerShape(10.dp),
            title = {
                Text(
                    text = "🔔 Permission Notice",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = primaryFontFamily,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                )
            },
            text = {
                Text(
                    text = "Storage permission is needed to download and get all movies",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = primaryFontFamily,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                )
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(200.dp),
            tint = MaterialTheme.colorScheme.onBackground,
            painter = painterResource(R.drawable.emoji_nature_48px),
            contentDescription = "App icon"
        )
    }
}