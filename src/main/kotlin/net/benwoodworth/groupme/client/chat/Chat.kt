package net.benwoodworth.groupme.client.chat

abstract class Chat internal constructor() {
    abstract val chatId: String

    /**
     * Compares [Chat]s by [chatId].
     */
    final override fun equals(other: Any?): Boolean {
        return other is Chat && chatId == other.chatId
    }

    final override fun hashCode(): Int {
        return chatId.hashCode()
    }

    abstract override fun toString(): String
}
