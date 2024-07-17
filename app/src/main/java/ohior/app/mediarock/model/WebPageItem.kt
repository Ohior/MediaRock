package ohior.app.mediarock.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


@Entity
data class WebPageItem(
    @Id var id: Long = 0,
    var itemId: String?,
    val key: String?,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val pageUrl: String,
) {
    override fun toString(): String {
        return """
            Web page => {
            Id : $id
            itemId : $itemId
            Image : $imageUrl
            Page Url : $pageUrl
            Title : $title
            Description : $description
        }""".trimIndent()
    }
}
