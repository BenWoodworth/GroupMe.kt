package net.benwoodworth.groupme.client.chat

fun SentMessage(
    messageId: String
): SentMessage = SentMessageImpl(
    messageId = messageId
)

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
