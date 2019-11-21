package net.benwoodworth.groupme.client.bot

open class Bot(
    val botId: String
) {
    final override fun equals(other: Any?): Boolean {
        return other is Bot && other.botId == botId
    }

    final override fun hashCode(): Int {
        return botId.hashCode()
    }

    override fun toString(): String {
        return "Bot($botId)"
    }
}
