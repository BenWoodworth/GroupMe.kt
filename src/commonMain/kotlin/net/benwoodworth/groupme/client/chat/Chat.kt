package net.benwoodworth.groupme.client.chat

interface Chat {
    val chatId: String

    /**
     * Compares [Chat]s by [chatId].
     */
    override fun equals(other: Any?): Boolean
}

fun Chat(chatId: String): Chat {
    return object : Chat {
        override val chatId: String = chatId

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
}
