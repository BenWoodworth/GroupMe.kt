package net.benwoodworth.groupme.client.chat

interface Chat {
    val chatId: String

    /**
     * Compares [Chat]s by [chatId].
     */
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
