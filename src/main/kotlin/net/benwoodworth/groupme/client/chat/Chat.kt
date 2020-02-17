package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.group.GroupChat

interface Chat {
    val chatId: String

    /**
     * Compares [Chat]s by [chatId].
     */
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}

internal open class ChatImpl(
    override val chatId: String
) : Chat {
    override fun equals(other: Any?): Boolean {
        return other is Chat && chatId == other.chatId
    }

    override fun hashCode(): Int {
        return chatId.hashCode()
    }

    override fun toString(): String {
        return "Chat($chatId)"
    }
}

internal fun Chat(
    chatId: String
): Chat {
    if (chatId.count { it == '+' } == 1) {
        val userIds = chatId.split('+')
        return DirectChat(User(userIds[0]), User(userIds[1]))
    }

    return GroupChat(chatId)
}
