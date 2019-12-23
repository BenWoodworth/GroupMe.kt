package net.benwoodworth.groupme.client.chat.direct

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.ChatInfo
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

interface DirectChatInfo : DirectChat, ChatInfo {
    override val toUser: NamedUserInfo
    override val lastMessage: DirectSentMessageInfo
}

private class DirectChatInfoImpl(
    override val json: JsonObject,
    fromUser: User,
    override val toUser: NamedUserInfo,
    override val lastMessage: DirectSentMessageInfo,
    override val messageCount: Int
) : DirectChatInfo, DirectChat by DirectChat(
    fromUser = fromUser,
    toUser = toUser
) {
    override val image: GroupMeImage?
        get() = toUser.avatar

    override val name: String
        get() = toUser.name
}

internal fun DirectChatInfo(
    json: JsonObject,
    fromUser: User,
    toUser: NamedUserInfo = json.getObject("other_user").run {
        NamedUserInfo(
            userId = getPrimitive("id").content,
            name = getPrimitive("name").content,
            avatar = getPrimitive("avatar_url").toGroupMeImage()
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
