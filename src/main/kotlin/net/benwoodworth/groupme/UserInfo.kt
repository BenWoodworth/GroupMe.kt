package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

open class UserInfo internal constructor(
    userId: String,
    val name: String,
    val avatar: GroupMeImage?
) : User(userId) {
    override fun toString(): String {
        return "User($name)"
    }
}
