package net.benwoodworth.groupme.client.media

class GroupMeImage internal constructor(
    val imageUrl: String
) {
    override fun equals(other: Any?): Boolean {
        return other is GroupMeImage && imageUrl == other.imageUrl
    }

    override fun hashCode(): Int {
        return imageUrl.hashCode()
    }

    override fun toString(): String {
        return "GroupMeImage($imageUrl)"
    }
}

internal fun String.toGroupMeImage() = GroupMeImage(this)
