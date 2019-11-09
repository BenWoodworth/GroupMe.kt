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
