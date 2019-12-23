package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

interface UserInfo : User {
    val nickname: String
    val avatar: GroupMeImage?
}

private class UserInfoImpl(
    userId: String,
    override val nickname: String,
    override val avatar: GroupMeImage?
) : UserInfo, User by User(
    userId = userId
) {
    override fun toString(): String {
        return "User($nickname)"
    }
}

internal fun UserInfo(
    userId: String,
    nickname: String,
    avatar: GroupMeImage?
): UserInfo = UserInfoImpl(
    userId = userId,
    nickname = nickname,
    avatar = avatar
)
