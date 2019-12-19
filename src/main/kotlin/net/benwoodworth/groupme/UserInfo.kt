package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

interface UserInfo : User {
    val name: String
    val avatar: GroupMeImage?
}

internal fun UserInfo(
    userId: String,
    name: String,
    avatar: GroupMeImage?
): UserInfo = object : UserInfo, User by User(userId) {
    override val name: String
        get() = name

    override val avatar: GroupMeImage?
        get() = avatar

    override fun toString(): String {
        return "User($name)"
    }
}
