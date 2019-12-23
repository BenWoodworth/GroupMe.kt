package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Chat

interface DirectChat : Chat {
    val fromUser: User
    val toUser: User
}

private class DirectChatImpl(
    override val fromUser: User,
    override val toUser: User
) : DirectChat, Chat by Chat(
    chatId = "${fromUser.userId}+${toUser.userId}"
) {
    override fun toString(): String {
        return "DirectChat($fromUser, $toUser)"
    }
}

fun DirectChat(
    fromUser: User,
    toUser: User
): DirectChat = DirectChat(
    fromUser = fromUser,
    toUser = toUser
)
