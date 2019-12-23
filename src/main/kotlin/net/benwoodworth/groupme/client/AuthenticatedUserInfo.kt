package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

interface AuthenticatedUserInfo : NamedUserInfo {
    val json: JsonObject
}

private class AuthenticatedUserInfoImpl(
    override val json: JsonObject,
    userId: String,
    name: String,
    avatar: GroupMeImage?
) : AuthenticatedUserInfo, NamedUserInfo by NamedUserInfo(
    userId = userId,
    name = name,
    avatar = avatar
)

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
