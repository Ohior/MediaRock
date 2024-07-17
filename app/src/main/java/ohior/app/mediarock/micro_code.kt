package ohior.app.mediarock

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

inline fun <T> T?.whenNull(block: () -> Unit): T? {
    if (this == null) block()
    return this@whenNull
}

inline fun <T> T?.whenNotNull(block: (T) -> Unit): T? {
    this?.let(block)
    return this@whenNotNull
}

fun <T : Any> NavHostController.navigateToScreen(route: T) {
    navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(this@navigateToScreen.graph.findStartDestination().id) {
            this.saveState = true
        }
        // Avoid multiple copies of the same destination when
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}

fun debugPrint(message: String, tag: String = "DEBUG") {
    Log.e(tag, message)
}

inline fun <T, R> T?.getWhenNotNull(defaultValue: R, block: (T) -> R): R {
    return this?.let(block) ?: defaultValue
}

inline fun String.openWebUrlPage(execute: (Document) -> Unit) {
    execute(Jsoup.connect(this).get())
}

const val SCREEN_KEY = "SCREEN_KEY"
inline fun <reified T> SharedPreferences.observeKey(key: String, default: T): Flow<T> {
    val flow = MutableStateFlow(getItem(key, default))

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        if (key == k) {
            flow.value = getItem(key, default)!!
        }
    }
    registerOnSharedPreferenceChangeListener(listener)

    return flow
        .onCompletion { unregisterOnSharedPreferenceChangeListener(listener) }
}

inline fun <reified T> SharedPreferences.getItem(key: String, default: T): T {
    return when (default) {
        is String -> getString(key, default) as T
        is Int -> getInt(key, default) as T
        is Long -> getLong(key, default) as T
        is Boolean -> getBoolean(key, default) as T
        is Float -> getFloat(key, default) as T
        else -> throw IllegalArgumentException("generic type not handle ${T::class.java.name}")
    }
}


//fun Activity.initScreen(): SharedPreferences {
//    return this.getSharedPreferences(SCREEN_KEY, Context.MODE_PRIVATE)
//}

fun <T> Context.setDataPreference(key: String, value: T) {
    this.getSharedPreferences(SCREEN_KEY, Context.MODE_PRIVATE).apply {
        when (value) {
            is String -> edit().putString(key, value).apply()
            is Int -> edit().putInt(key, value).apply()
            is Boolean -> edit().putBoolean(key, value).apply()
            else -> throw IllegalArgumentException("Unsupported type: ${value!!::class.java.name}")
        }
    }
}


//fun main() {
//    runBlocking {
//        val webAddress = "https://loadedfiles.org/4a6fd6942df13308"
//        val httpClient = HttpClient(CIO)
//        httpClient.use { client ->
//            val response = client.get(webAddress)
//            val doc = Jsoup.parse(response.bodyAsText())
//            println(doc)
//        }
//    }
//}