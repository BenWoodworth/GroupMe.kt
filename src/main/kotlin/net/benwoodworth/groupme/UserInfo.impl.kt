package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

internal fun UserInfo(
    userId: String,
    nickname: String,
    avatar: GroupMeImage?
): UserInfo = UserInfoImpl(
    userId = userId,
    nickname = nickname,
    avatar = avatar
)

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
