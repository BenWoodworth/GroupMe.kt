package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Attachment
import net.benwoodworth.groupme.client.chat.SentMessageInfo

interface GroupSentMessageInfo : SentMessageInfo {
    override val chat: GroupChat
}

class GroupSentMessageInfoImpl(
    override val messageJson: JsonObject
) : GroupSentMessageInfo {
    override val messageId: String = messageJson.getPrimitive("id").content

    override val sender: User = User(messageJson.getPrimitive("user_id").content)

    override val chat: GroupChat = GroupChat(messageJson.getPrimitive("group_id").content)

    override val text: String? = messageJson.getPrimitive("text").contentOrNull

    override val attachments: List<Attachment> = messageJson.getArray("attachments").map {
        Attachment(it.jsonObject)
    }

    override val sourceGuid: String = messageJson.getPrimitive("source_guid").content
}
