package net.benwoodworth.groupme.client.chat

interface SentMessage {
    val messageId: String

    /**
     * Compares [SentMessage]s by [messageId].
     */
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}

fun SentMessage(messageId: String): SentMessage = object : SentMessage {
    override val messageId: String = messageId

    override fun equals(other: Any?): Boolean {
        return other is SentMessage && other.messageId == messageId
    }

    override fun hashCode(): Int {
        return messageId.hashCode()
    }

    override fun toString(): String {
        return "SentMessage($messageId)"
    }
}
