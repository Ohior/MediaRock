package ohior.app.mediarock.ui.screens.pdfview

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PdfScreenLogic : ViewModel() {
    var isPdfSelected by mutableStateOf(false)
    var pdfUri: Uri = Uri.EMPTY
}