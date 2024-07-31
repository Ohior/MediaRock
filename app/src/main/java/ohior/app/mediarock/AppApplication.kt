package ohior.app.mediarock

import android.app.Application
import ohior.app.mediarock.service.ObjectBox

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.initializeBoxStore(this)
    }
}