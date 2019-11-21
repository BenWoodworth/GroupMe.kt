package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

class AuthenticatedUserInfo internal constructor(
    val userJson: JsonObject
) : UserInfo(
    userId = userJson.getPrimitive("id").content,
    name = userJson.getPrimitive("name").content,
    avatar = userJson.getPrimitive("avatar").contentOrNull?.let { GroupMeImage(it) }
)
