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

private class SentMessageImpl(
    override val messageId: String
) : SentMessage {
    override fun equals(other: Any?): Boolean {
        return other is SentMessage && messageId == other.messageId
    }

    override fun hashCode(): Int {
        return messageId.hashCode()
    }

    override fun toString(): String {
        return "SentMessage($messageId)"
    }
}

fun SentMessage(
    messageId: String
): SentMessage = SentMessageImpl(
    messageId = messageId
)
