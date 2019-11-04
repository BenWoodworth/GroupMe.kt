package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

interface UserInfo : User {
    val name: String
    val avatar: GroupMeImage?
}

internal class UserInfoImpl(
    override val userId: String,
    override val name: String,
    override val avatar: GroupMeImage?
) : UserInfo, User by User(userId) {
    override fun toString(): String {
        return "User($name)"
    }
}

internal fun UserInfo(userId: String, name: String, avatar: GroupMeImage): UserInfo {
    return UserInfoImpl(userId, name, avatar)
}
