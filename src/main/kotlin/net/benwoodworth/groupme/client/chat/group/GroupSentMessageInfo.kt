package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.chat.Attachment
import net.benwoodworth.groupme.client.chat.SentMessageInfo
import net.benwoodworth.groupme.client.media.toGroupMeImage
import java.util.*

interface GroupSentMessageInfo : SentMessageInfo {
    override val chat: GroupChat
}

private class GroupSentMessageInfoImpl(
    override val chat: GroupChat,
    json: JsonObject,
    messageId: String,
    sender: UserInfo,
    text: String?,
    attachments: List<Attachment>,
    sourceGuid: String,
    likes: List<User>,
    created: Date
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

internal fun GroupSentMessageInfo(
    json: JsonObject,
    chat: GroupChat = GroupChat(json.getPrimitive("group_id").content),
    messageId: String = json.getPrimitive("id").content,
    sender: UserInfo = UserInfo(
        userId = json.getPrimitive("sender_id").content,
        nickname = json.getPrimitive("name").content,
        avatar = json.getPrimitive("avatar_url").toGroupMeImage()
    ),
    text: String? = json.getPrimitive("text").contentOrNull,
    attachments: List<Attachment> = json.getArray("attachments").map {
        Attachment(it.jsonObject)
    },
    sourceGuid: String = json.getPrimitive("source_guid").content,
    likes: List<User> = json.getArray("favorited_by").map {
        User(it.primitive.content)
    },
    created: Date = Date(json.getPrimitive("created_at").long)
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
