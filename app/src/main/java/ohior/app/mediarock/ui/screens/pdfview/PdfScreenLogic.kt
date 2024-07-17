package ohior.app.mediarock.ui.screens.pdfview

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPdfReaderState

class PdfScreenLogic : ViewModel() {
    var isPdfSelected by mutableStateOf(false)
    var pdfUri by mutableStateOf<Uri>(Uri.EMPTY)

    fun getPdfVerticalState() = VerticalPdfReaderState(
        resource = ResourceType.Local(pdfUri),
        isZoomEnable = true
    )
}