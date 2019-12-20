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
