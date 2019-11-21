package net.benwoodworth.groupme.client.chat.direct

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.chat.SentMessageInfo

class DirectSentMessageInfo internal constructor(
    messageJson: JsonObject,
    override val chat: DirectChat
) : SentMessageInfo(messageJson)
