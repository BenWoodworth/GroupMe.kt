package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import java.util.*

interface SentMessageInfo : SentMessage, Message {
    val sender: UserInfo
    val likes: List<User>

    @Deprecated("May eventually be replaced with a Kotlin date type")
    val created: Date
}

private class SentMessageInfoImpl(
    override val chat: Chat,
    override val json: JsonObject,
    messageId: String,
    override val sender: UserInfo,
    override val text: String?,
    override val attachments: List<Attachment>,
    override val sourceGuid: String,
    override val likes: List<User>,
    override val created: Date
) : SentMessageInfo, SentMessage by SentMessage(
    messageId = messageId,
    chat = chat
)

internal fun SentMessageInfo(
    chat: Chat,
    json: JsonObject,
    messageId: String,
    sender: UserInfo,
    text: String?,
    attachments: List<Attachment>,
    sourceGuid: String,
    likes: List<User>,
    created: Date
): SentMessageInfo = SentMessageInfoImpl(
    chat = chat,
    json = json,
    messageId = messageId,
    sender = sender,
    text = text,
    attachments = attachments,
    sourceGuid = sourceGuid,
    likes = likes,
    created = created
)
