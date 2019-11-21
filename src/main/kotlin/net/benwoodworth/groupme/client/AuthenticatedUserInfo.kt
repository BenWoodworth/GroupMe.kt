package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.UserInfo

class AuthenticatedUserInfo internal constructor(
    userJson: JsonObject
) : UserInfo(userJson)
