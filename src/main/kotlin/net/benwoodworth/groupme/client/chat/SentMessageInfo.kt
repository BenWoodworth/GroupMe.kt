package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

interface SentMessageInfo : SentMessage {
    val json: JsonObject
    val chat: Chat
    val sender: UserInfo
    val text: String?
    val attachments: List<Attachment>
    val sourceGuid: String
    val likes: List<User>
    val created: Long
}

internal fun SentMessageInfo(
    json: JsonObject,
    messageId: String,
    chat: Chat,
    sender: UserInfo,
    text: String?,
    attachments: List<Attachment>,
    sourceGuid: String,
    likes: List<User>,
    created: Long
): SentMessageInfo = object : SentMessageInfo, SentMessage by SentMessage(
    messageId = messageId
) {
    override val json: JsonObject = json
    override val chat: Chat = chat
    override val sender: UserInfo = sender
    override val text: String? = text
    override val attachments: List<Attachment> = attachments
    override val sourceGuid: String = sourceGuid
    override val likes: List<User> = likes
    override val created: Long = created
    override val messageId: String = messageId
}
