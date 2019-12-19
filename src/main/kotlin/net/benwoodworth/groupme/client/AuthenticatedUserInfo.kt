package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

class AuthenticatedUserInfo internal constructor(
    val json: JsonObject
) : UserInfo by UserInfo(
    userId = json.getPrimitive("id").content,
    name = json.getPrimitive("name").content,
    avatar = json.getPrimitive("avatar").contentOrNull?.let { GroupMeImage(it) }
)
