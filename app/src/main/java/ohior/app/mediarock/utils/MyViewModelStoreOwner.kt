package ohior.app.mediarock.utils

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

// Global ViewModelStoreOwner
object GlobalViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
//    fun getViewModelStore(): ViewModelStore = viewModelStore
}