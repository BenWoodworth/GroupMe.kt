package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.chat.SentMessageInfo
import net.benwoodworth.groupme.client.chat.SentMessageInfoBase

interface GroupSentMessageInfo : SentMessageInfo {
    override val chat: GroupChat
}

internal class GroupSentMessageInfoImpl(
    messageJson: JsonObject,
    override val chat: GroupChat
) : SentMessageInfoBase(messageJson), GroupSentMessageInfo
