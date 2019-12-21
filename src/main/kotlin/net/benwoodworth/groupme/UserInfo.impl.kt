package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

internal fun UserInfo(
    userId: String,
    name: String,
    avatar: GroupMeImage
): UserInfo = UserInfoImpl(
    userId,
    name,
    avatar
)

private class UserInfoImpl(
    userId: String,
    override val name: String,
    override val avatar: GroupMeImage
) : UserInfo, User by User(
    userId = userId
) {
    override fun toString(): String {
        return "User($name)"
    }
}
