package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

interface AuthenticatedUserInfo : UserInfo, AuthenticatedUser

internal class AuthenticatedUserInfoImpl(
    userId: String,
    name: String,
    avatar: GroupMeImage
) : AuthenticatedUserInfo, UserInfo by UserInfo(userId, name, avatar)

internal fun AuthenticatedUserInfo(
    userId: String,
    name: String,
    avatar: GroupMeImage
): AuthenticatedUserInfo {
    return AuthenticatedUserInfoImpl(userId, name, avatar)
}
