package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Attachment
import net.benwoodworth.groupme.client.chat.ChatInfo
import net.benwoodworth.groupme.client.chat.SentMessage
import net.benwoodworth.groupme.client.chat.toAttachmentList
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

interface GroupChatInfo : GroupChat, ChatInfo {
    val description: String
    val creator: User
    override val lastMessage: LastMessage

    class LastMessage internal constructor(
        val json: JsonObject,
        messageId: String,
        val text: String? = json.getPrimitive("text").contentOrNull,
        val image: GroupMeImage? = json.getPrimitive("image_url").toGroupMeImage(),
        val attachments: List<Attachment> = json.getArray("attachments").toAttachmentList()
    ) : SentMessage by SentMessage(
        messageId = messageId
    )
}

private class GroupChatInfoImpl(
    override val json: JsonObject,
    chatId: String,
    override val description: String,
    override val creator: User,
    override val lastMessage: GroupChatInfo.LastMessage,
    override val messageCount: Int,
    override val image: GroupMeImage?,
    override val name: String
) : GroupChatInfo, GroupChat by GroupChat(
    chatId = chatId
) {
    override fun toString(): String {
        return "GroupChat($name)"
    }
}

internal fun GroupChatInfo(
    json: JsonObject,
    chatId: String = json.getPrimitive("id").content,
    description: String = json.getPrimitive("description").content,
    creator: User = User(
        userId = json.getPrimitive("creator_user_id").content
    ),
    lastMessage: GroupChatInfo.LastMessage = json.getObject("messages").run {
        GroupChatInfo.LastMessage(
            json = getObject("preview"),
            messageId = getPrimitive("last_message_id").content
        )
    },
    messageCount: Int = json.getObject("messages").getPrimitive("count").int,
    image: GroupMeImage? = json.getPrimitive("image_url").toGroupMeImage(),
    name: String = json.getPrimitive("name").content
): GroupChatInfo = GroupChatInfoImpl(
    json = json,
    chatId = chatId,
    description = description,
    creator = creator,
    lastMessage = lastMessage,
    messageCount = messageCount,
    image = image,
    name = name
)
