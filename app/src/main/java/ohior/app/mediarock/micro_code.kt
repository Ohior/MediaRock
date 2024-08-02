package ohior.app.mediarock

import android.icu.text.SimpleDateFormat
import android.util.Log
import java.util.Date
import java.util.Locale

inline fun <T> T?.whenNull(block: () -> Unit): T? {
    if (this == null) block()
    return this@whenNull
}

inline fun <T> T?.whenNotNull(block: (T) -> Unit): T? {
    this?.let(block)
    return this@whenNotNull
}

fun debugPrint(message: String, tag: String = "DEBUG") {
    Log.e(tag, message)
}

inline fun <T, R> T?.getWhenNotNull(defaultValue: R, block: (T) -> R): R {
    return this?.let(block) ?: defaultValue
}

fun convertLongToTime(time: Long, isTime: Boolean = false): String {
    val pattern = if (isTime) "yyyy.MM.dd HH:mm:ss" else "HH:mm:ss"
    val date = Date(time)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(date)
}

fun formatFileSize(sizeInBytes: Long): String {
    val kb = 1024L
    val mb = kb * 1024
    val gb = mb * 1024
    return when {
        sizeInBytes >= gb -> String.format(
            Locale.getDefault(),
            "%.2f GB",
            sizeInBytes / gb.toDouble()
        )

        sizeInBytes >= mb -> String.format(
            Locale.getDefault(),
            "%.2f MB",
            sizeInBytes / mb.toDouble()
        )

        sizeInBytes >= kb -> String.format(
            Locale.getDefault(),
            "%.2f KB",
            sizeInBytes / kb.toDouble()
        )

        else -> String.format(Locale.getDefault(), "%d bytes", sizeInBytes)
    }
}


fun generateID(): Long {
    val uuid = System.nanoTime()
    return uuid
}