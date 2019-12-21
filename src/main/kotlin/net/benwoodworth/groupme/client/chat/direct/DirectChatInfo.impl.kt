package net.benwoodworth.groupme.client.chat.direct

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

internal fun DirectChatInfo(
    json: JsonObject,
    fromUser: User,
    toUser: UserInfo = json.getObject("other_user").run {
        UserInfo(
            userId = getPrimitive("id").content,
            name = getPrimitive("name").content,
            avatar = getPrimitive("avatar_url").contentOrNull?.let { GroupMeImage(it) }
        )
    },
    lastMessage: DirectSentMessageInfo = json.getObject("last_message").let { message ->
        DirectSentMessageInfo(
            chat = DirectChat(fromUser, toUser),
            json = message
        )
    },
    messageCount: Int = json.getPrimitive("messages_count").int
): DirectChatInfo = DirectChatInfoImpl(
    json = json,
    fromUser = fromUser,
    toUser = toUser,
    lastMessage = lastMessage,
    messageCount = messageCount
)

private class DirectChatInfoImpl(
    override val json: JsonObject,
    fromUser: User,
    override val toUser: UserInfo,
    override val lastMessage: DirectSentMessageInfo,
    override val messageCount: Int
) : DirectChatInfo, DirectChat by DirectChat(
    fromUser = fromUser,
    toUser = toUser
) {
    override val image: GroupMeImage
        get() = toUser.avatar

    override val name: String
        get() = toUser.name
}
