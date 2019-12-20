package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.UserInfo

interface AuthenticatedUserInfo : UserInfo {
    val json: JsonObject
}
