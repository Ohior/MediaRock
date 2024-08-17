package ohior.app.mediarock.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf

object AppConstants {
    val bottomMenuItems = listOf(
        ScreenHolder(
            imageVector = Icons.Outlined.PictureAsPdf,
            contentDescription = "PDF reader",
            screen = PdfViewType
        ),
    )
}