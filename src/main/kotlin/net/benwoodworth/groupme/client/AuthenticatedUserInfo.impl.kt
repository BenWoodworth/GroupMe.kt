package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

internal fun AuthenticatedUserInfo(
    json: JsonObject,
    userId: String,
    name: String,
    avatar: GroupMeImage?
): AuthenticatedUserInfo = AuthenticatedUserInfoImpl(
    json = json,
    userId = userId,
    name = name,
    avatar = avatar
)

private class AuthenticatedUserInfoImpl(
    override val json: JsonObject,
    userId: String,
    name: String,
    avatar: GroupMeImage?
) : AuthenticatedUserInfo, UserInfo by UserInfo(
    userId = userId,
    name = name,
    avatar = avatar
)
