package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

abstract class SentMessageInfo internal constructor(
    val messageJson: JsonObject
) : SentMessage by SentMessage(
    messageId = messageJson.getPrimitive("id").content
) {
    abstract val chat: Chat

    val sender: UserInfo
        get() = UserInfo(
            userId = messageJson.getPrimitive("sender_id").content,
            name = messageJson.getPrimitive("name").content,
            avatar = messageJson.getPrimitive("avatar_url").contentOrNull?.let { GroupMeImage(it) }
        )

    val text: String?
        get() = messageJson.getPrimitive("text").contentOrNull

    val attachments: List<Attachment>
        get() = messageJson.getArray("attachments").map {
            Attachment(it.jsonObject)
        }

    val sourceGuid: String
        get() = messageJson.getPrimitive("source_guid").content

    val likes: List<User>
        get() = messageJson.getArray("favorited_by").map {
            User(it.primitive.content)
        }

    val created: Long
        get() = messageJson.getPrimitive("created_at").long
}
