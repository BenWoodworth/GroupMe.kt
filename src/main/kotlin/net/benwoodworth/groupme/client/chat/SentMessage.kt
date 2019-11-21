package net.benwoodworth.groupme.client.chat

abstract class SentMessage internal constructor() {
    abstract val messageId: String

    /**
     * Compares [SentMessage]s by [messageId].
     */
    final override fun equals(other: Any?): Boolean {
        return other is SentMessage && other.messageId == messageId
    }

    final override fun hashCode(): Int {
        return messageId.hashCode()
    }

    override fun toString(): String {
        return "SentMessage($messageId)"
    }
}
