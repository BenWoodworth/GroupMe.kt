package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.chat.SentMessageInfo

class GroupSentMessageInfo(
    messageJson: JsonObject,
    override val chat: GroupChat
) : SentMessageInfo(messageJson)
