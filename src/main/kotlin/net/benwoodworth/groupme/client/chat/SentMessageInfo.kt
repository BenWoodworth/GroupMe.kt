package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

interface SentMessageInfo : SentMessage {
    val messageJson: JsonObject
    val chat: Chat
    val sender: UserInfo
    val text: String?
    val attachments: List<Attachment>
    val sourceGuid: String
    val likes: List<User>
    val created: Long
}

internal fun SentMessageInfo(
    messageJson: JsonObject,
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
    override val messageJson: JsonObject get() = messageJson
    override val chat: Chat get() = chat
    override val sender: UserInfo get() = sender
    override val text: String? get() = text
    override val attachments: List<Attachment> get() = attachments
    override val sourceGuid: String get() = sourceGuid
    override val likes: List<User> get() = likes
    override val created: Long get() = created
    override val messageId: String get() = messageId
}
