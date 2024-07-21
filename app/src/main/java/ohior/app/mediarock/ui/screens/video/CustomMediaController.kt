package ohior.app.mediarock.ui.screens.video

import android.content.Context
import android.widget.MediaController

class CustomMediaController(
    context: Context,
) : MediaController(context) {

    private var onVisibilityChangedListener: ((Boolean) -> Unit)? = null

    fun setOnVisibilityChangedListener(listener: (Boolean) -> Unit) {
        onVisibilityChangedListener = listener
    }

    override fun show() {
        super.show()
        onVisibilityChangedListener?.invoke(true)
    }

    override fun hide() {
        super.hide()
        onVisibilityChangedListener?.invoke(false)
    }
}