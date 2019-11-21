package net.benwoodworth.groupme.client.chat.direct

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

class DirectChatInfo internal constructor(
    fromUser: User,
    val chatJson: JsonObject
) : DirectChat(
    fromUser = fromUser,
    toUser = UserInfo(chatJson.getObject("other_user"))
)
