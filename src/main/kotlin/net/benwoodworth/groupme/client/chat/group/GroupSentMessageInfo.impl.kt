package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.chat.Attachment
import net.benwoodworth.groupme.client.chat.SentMessageInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

internal fun GroupSentMessageInfo(
    chat: GroupChat,
    json: JsonObject,
    messageId: String = json.getPrimitive("id").content,
    sender: UserInfo = UserInfo(
        userId = json.getPrimitive("sender_id").content,
        name = json.getPrimitive("name").content,
        avatar = json.getPrimitive("avatar_url").contentOrNull?.let { GroupMeImage(it) }
    ),
    text: String? = json.getPrimitive("text").contentOrNull,
    attachments: List<Attachment> = json.getArray("attachments").map {
        Attachment(it.jsonObject)
    },
    sourceGuid: String = json.getPrimitive("source_guid").content,
    likes: List<User> = json.getArray("favorited_by").map {
        User(it.primitive.content)
    },
    created: Long = json.getPrimitive("created_at").long
): GroupSentMessageInfo = GroupSentMessageInfoImpl(
    json = json,
    messageId = messageId,
    chat = chat,
    sender = sender,
    text = text,
    attachments = attachments,
    sourceGuid = sourceGuid,
    likes = likes,
    created = created
)

private class GroupSentMessageInfoImpl(
    override val chat: GroupChat,
    json: JsonObject,
    messageId: String,
    sender: UserInfo,
    text: String?,
    attachments: List<Attachment>,
    sourceGuid: String,
    likes: List<User>,
    created: Long
) : GroupSentMessageInfo, SentMessageInfo by SentMessageInfo(
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
