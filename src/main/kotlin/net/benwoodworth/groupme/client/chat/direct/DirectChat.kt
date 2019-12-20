package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Chat

interface DirectChat : Chat {
    val fromUser: User
    val toUser: User
}

fun DirectChat(
    fromUser: User,
    toUser: User
): DirectChat = object : DirectChat, Chat by Chat(
    chatId = "${fromUser.userId}+${toUser.userId}"
) {
    override val fromUser: User = fromUser
    override val toUser: User = toUser

    override fun toString(): String {
        return "DirectChat($fromUser, $toUser)"
    }
}
