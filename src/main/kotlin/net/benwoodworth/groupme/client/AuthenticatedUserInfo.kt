package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.NamedUserInfo

interface AuthenticatedUserInfo : NamedUserInfo {
    val json: JsonObject
}
