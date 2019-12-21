package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

internal fun NamedUserInfo(
    userId: String,
    name: String,
    nickname: String = name,
    avatar: GroupMeImage
): NamedUserInfo = NamedUserInfoImpl(
    userId = userId,
    name = name,
    nickname = nickname,
    avatar = avatar
)

private class NamedUserInfoImpl(
    userId: String,
    override val name: String,
    nickname: String,
    avatar: GroupMeImage
) : NamedUserInfo, UserInfo by UserInfo(
    userId = userId,
    nickname = nickname,
    avatar = avatar
)
