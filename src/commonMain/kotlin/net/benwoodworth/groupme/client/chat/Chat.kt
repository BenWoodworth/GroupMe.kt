package net.benwoodworth.groupme.client.chat

interface Chat {
    val chatId: String

    /**
     * Compares [Chat]s by [chatId].
     */
    override fun equals(other: Any?): Boolean
}

internal class ChatImpl(
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

fun Chat(chatId: String): Chat {
    return ChatImpl(chatId)
}