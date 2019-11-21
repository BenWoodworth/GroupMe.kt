package net.benwoodworth.groupme.client.media

class GroupMeVideo internal constructor(
    val videoUrl: String,
    val preview: GroupMeImage
) {
    override fun equals(other: Any?): Boolean {
        return other is GroupMeVideo && videoUrl == other.videoUrl
    }

    override fun hashCode(): Int {
        return videoUrl.hashCode()
    }

    override fun toString(): String {
        return "GroupMeVideo($videoUrl)"
    }
}
