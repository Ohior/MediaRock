package ohior.app.mediarock.model

import io.objectbox.annotation.ConflictStrategy
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique


@Entity
data class WebPageItem(
    @Id var id: Long = 0,
    @Unique(onConflict = ConflictStrategy.REPLACE)
    val itemId: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val pageUrl: String = "",
    val isFavorite: Boolean = false
)
