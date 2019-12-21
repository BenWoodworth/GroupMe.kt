package net.benwoodworth.groupme.client.bot

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.Message

@GroupMeScope
interface BotContext {
    val bot: Bot

    suspend operator fun invoke(block: suspend BotContext.() -> Unit) = block()

    suspend fun sendMessage(message: Message)

    suspend fun Message.send() = sendMessage(this)
}
