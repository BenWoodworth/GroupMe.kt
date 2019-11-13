package net.benwoodworth.groupme.client.chat.direct

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.chat.SentMessageInfo
import net.benwoodworth.groupme.client.chat.SentMessageInfoBase

interface DirectSentMessageInfo : SentMessageInfo {
    override val chat: DirectChat
}

internal class DirectSentMessageInfoImpl(
    messageJson: JsonObject,
    override val chat: DirectChat
) : SentMessageInfoBase(messageJson), DirectSentMessageInfo
