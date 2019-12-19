package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.chat.Attachment
import net.benwoodworth.groupme.client.chat.SentMessageInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

interface GroupSentMessageInfo : SentMessageInfo {
    override val chat: GroupChat
}

internal fun GroupSentMessageInfo(
    json: JsonObject,
    messageId: String,
    chat: GroupChat,
    sender: UserInfo,
    text: String?,
    attachments: List<Attachment>,
    sourceGuid: String,
    likes: List<User>,
    created: Long
): GroupSentMessageInfo = object : GroupSentMessageInfo, SentMessageInfo by SentMessageInfo(
    json = json,
    messageId = messageId,
    chat = chat,
    sender = sender,
    text = text,
    attachments = attachments,
    sourceGuid = sourceGuid,
    likes = likes,
    created = created
) {
    override val chat: GroupChat get() = chat
}

internal fun JsonObject.toGroupSentMessageInfo(chat: GroupChat) = GroupSentMessageInfo(
    json = this,
    messageId = getPrimitive("id").content,
    chat = chat,
    text = getPrimitive("text").contentOrNull,
    sender = UserInfo(
        userId = getPrimitive("sender_id").content,
        name = getPrimitive("name").content,
        avatar = getPrimitive("avatar_url").contentOrNull?.let { GroupMeImage(it) }
    ),
    attachments = getArray("attachments").map {
        Attachment(it.jsonObject)
    },
    sourceGuid = getPrimitive("source_guid").content,
    likes = getArray("favorited_by").map {
        User(it.primitive.content)
    },
    created = getPrimitive("created_at").long
)
