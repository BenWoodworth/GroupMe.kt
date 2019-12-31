package net.benwoodworth.groupme.client.bot

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectSerializer
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.client.chat.Message

@GroupMeScope
class BotContext internal constructor(
    val bot: Bot,
    internal val httpClient: GroupMeHttpClient,
    internal val json: Json
) {
    suspend operator fun invoke(block: suspend BotContext.() -> Unit) = block()

    suspend fun sendMessage(message: Message) {
        val newEntries = mapOf("bot_id" to JsonPrimitive(bot.botId))
        val appendedjson = JsonObject(message.json + newEntries)

        httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/bots/post",
            body = json.stringify(JsonObjectSerializer, appendedjson)
        )
    }

    suspend fun Message.send() = sendMessage(this)
}
