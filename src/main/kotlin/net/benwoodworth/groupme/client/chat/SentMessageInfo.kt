package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

interface SentMessageInfo : SentMessage {
    val chat: Chat
    val json: JsonObject
    val sender: UserInfo
    val text: String?
    val attachments: List<Attachment>
    val sourceGuid: String
    val likes: List<User>
    val created: Long
}
