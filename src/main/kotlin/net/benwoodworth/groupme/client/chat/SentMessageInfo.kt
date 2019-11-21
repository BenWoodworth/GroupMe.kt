package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User

abstract class SentMessageInfo internal constructor(
    val messageJson: JsonObject
) : SentMessage() {
    abstract val chat: Chat

    override val messageId: String
        get() = messageJson.getPrimitive("id").content

    val sender: User
        get() = User(messageJson.getPrimitive("user_id").content)

    val text: String?
        get() = messageJson.getPrimitive("text").contentOrNull

    val attachments: List<Attachment>
        get() = messageJson.getArray("attachments").map {
            Attachment(it.jsonObject)
        }

    val sourceGuid: String
        get() = messageJson.getPrimitive("source_guid").content
}
