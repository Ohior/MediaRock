package ohior.app.mediarock.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


@Entity
data class WebPageItem(
    @Id var id: Long = 0,
    var itemId: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val pageUrl: String = "",
    val isFavorite: Boolean = false
)
