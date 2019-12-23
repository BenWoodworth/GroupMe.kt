package net.benwoodworth.groupme.client.chat.direct

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Attachment
import net.benwoodworth.groupme.client.chat.SentMessageInfo
import net.benwoodworth.groupme.client.media.toGroupMeImage

interface DirectSentMessageInfo : SentMessageInfo {
    override val chat: DirectChat
    override val sender: NamedUserInfo
}

private class DirectSentMessageInfoImpl(
    override val chat: DirectChat,
    override val json: JsonObject,
    override val messageId: String,
    override val sender: NamedUserInfo,
    override val text: String?,
    override val attachments: List<Attachment>,
    override val sourceGuid: String,
    override val likes: List<User>,
    override val created: Long
) : DirectSentMessageInfo, SentMessageInfo by SentMessageInfo(
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

internal fun DirectSentMessageInfo(
    chat: DirectChat,
    json: JsonObject,
    messageId: String = json.getPrimitive("id").content,
    text: String? = json.getPrimitive("text").contentOrNull,
    sender: NamedUserInfo = NamedUserInfo(
        userId = json.getPrimitive("sender_id").content,
        name = json.getPrimitive("name").content,
        avatar = json.getPrimitive("avatar_url").toGroupMeImage()
    ),
    attachments: List<Attachment> = json.getArray("attachments").map {
        Attachment(it.jsonObject)
    },
    sourceGuid: String = json.getPrimitive("source_guid").content,
    likes: List<User> = json.getArray("favorited_by").map {
        User(it.primitive.content)
    },
    created: Long = json.getPrimitive("created_at").long
): DirectSentMessageInfo = DirectSentMessageInfoImpl(
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
