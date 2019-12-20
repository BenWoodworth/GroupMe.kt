package net.benwoodworth.groupme.client.bot

open class Bot(
    val botId: String
) {
    /**
     * Compares [Bot]s by [botId].
     */
    final override fun equals(other: Any?): Boolean {
        return other is Bot && botId == other.botId
    }

    final override fun hashCode(): Int {
        return botId.hashCode()
    }

    override fun toString(): String {
        return "Bot($botId)"
    }
}
