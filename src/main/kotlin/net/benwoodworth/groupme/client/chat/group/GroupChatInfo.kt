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


