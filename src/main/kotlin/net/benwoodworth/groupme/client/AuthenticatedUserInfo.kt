package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

interface AuthenticatedUserInfo : UserInfo {
    val json: JsonObject
}

internal fun AuthenticatedUserInfo(
    json: JsonObject,
    userId: String,
    name: String,
    avatar: GroupMeImage?
): AuthenticatedUserInfo = object : AuthenticatedUserInfo, UserInfo by UserInfo(
    userId = userId,
    name = name,
    avatar = avatar
) {
    override val json: JsonObject = json
}
