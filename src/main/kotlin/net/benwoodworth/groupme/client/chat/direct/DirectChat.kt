package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatImpl

interface DirectChat : Chat {
    val fromUser: User
    val toUser: User
}

private class DirectChatImpl(
    override val fromUser: User,
    override val toUser: User
) : ChatImpl(
    chatId = "${fromUser.userId}+${toUser.userId}"
), DirectChat {
    override fun toString(): String {
        return "DirectChat($fromUser, $toUser)"
    }
}

fun DirectChat(
    fromUser: User,
    toUser: User
): DirectChat = DirectChatImpl(
    fromUser = fromUser,
    toUser = toUser
)
