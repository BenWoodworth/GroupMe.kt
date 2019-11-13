package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User

interface SentMessageInfo : SentMessage {
    val sender: User
    val chat: Chat
    val messageJson: JsonObject
    val text: String?
    val attachments: List<Attachment>
    val sourceGuid: String
}

internal abstract class SentMessageInfoBase(
    final override val messageJson: JsonObject
) : SentMessageInfo {
    override val messageId: String = messageJson.getPrimitive("id").content

    override val sender: User = User(messageJson.getPrimitive("user_id").content)

    override val text: String? = messageJson.getPrimitive("text").contentOrNull

    override val attachments: List<Attachment> = messageJson.getArray("attachments").map {
        Attachment(it.jsonObject)
    }

    override val sourceGuid: String = messageJson.getPrimitive("source_guid").content
}
