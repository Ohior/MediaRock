package ohior.app.mediarock.service

import android.content.Context
import io.objectbox.BoxStore
import ohior.app.mediarock.model.MyObjectBox

object ObjectBox {
    lateinit var store: BoxStore
        private set

    fun initializeBoxStore(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context)
            .build()
    }
}