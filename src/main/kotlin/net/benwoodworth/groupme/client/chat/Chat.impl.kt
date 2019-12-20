package net.benwoodworth.groupme.client.chat

internal fun Chat(
    chatId: String
): Chat = ChatImpl(
    chatId = chatId
)

private class ChatImpl(
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
