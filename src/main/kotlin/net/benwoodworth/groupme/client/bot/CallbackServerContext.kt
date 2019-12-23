package net.benwoodworth.groupme.client.bot

import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.client.chat.Message

@GroupMeScope
interface CallbackServerContext {
    suspend fun stopServer()

    fun getBotContext(bot: Bot): BotContext

    val Bot.context: BotContext
        get() = getBotContext(this)

    suspend operator fun Bot.invoke(block: suspend BotContext.() -> Unit) = context { block() }

    suspend fun Bot(botId: String, block: suspend BotContext.() -> Unit) = (Bot(botId)) { block() }

    suspend fun sendBotMessage(bot: Bot, message: Message) {
        bot { message.send() }
    }

    suspend fun Bot.sendMessage(message: Message) = sendBotMessage(this, message)

    suspend fun Message.send(bot: Bot) = sendBotMessage(bot, this)
}

internal class CallbackServerContextImpl(
    private val server: CallbackServer,
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : CallbackServerContext {
    override suspend fun stopServer() {
        server.stop()
    }

    override fun getBotContext(bot: Bot): BotContext {
        return BotContext(bot, httpClient, json)
    }
}
