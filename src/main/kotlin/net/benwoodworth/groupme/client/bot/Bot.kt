package net.benwoodworth.groupme.client.bot

interface Bot {
    val botId: String

    /**
     * Compares [Bot]s by [botId].
     */
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}

private class BotImpl(
    override val botId: String
) : Bot {
    override fun equals(other: Any?): Boolean {
        return other is Bot && botId == other.botId
    }

    override fun hashCode(): Int {
        return botId.hashCode()
    }

    override fun toString(): String {
        return "Bot($botId)"
    }
}

fun Bot(
    botId: String
): Bot = BotImpl(
    botId = botId
)