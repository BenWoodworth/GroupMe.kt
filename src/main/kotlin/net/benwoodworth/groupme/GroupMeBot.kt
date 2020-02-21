package net.benwoodworth.groupme

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.CallbackHandler
import net.benwoodworth.groupme.client.bot.CallbackServer
import net.benwoodworth.groupme.client.chat.Message

class GroupMeBot internal constructor(
    private val client: HttpClient
) : GroupMeClient {
    companion object {
        fun getClient(): GroupMeBot {
            val client = HttpClientFactory.create(null)
            return GroupMeBot(client)
        }

        suspend inline fun getClient(block: GroupMeBot.() -> Unit) {
            getClient().run { block() }
        }

        suspend fun startCallbackServer(port: Int, callbackHandler: CallbackHandler) {
            CallbackServer(port, GroupMe.json, callbackHandler).start()
        }
    }

    override suspend fun Bot.sendMessage(message: Message) {
        val newEntries = mapOf("bot_id" to JsonPrimitive(botId))
        val appendedjson = JsonObject(message.json + newEntries)

        client.post<JsonObject>("${GroupMe.API_V3}/bots/post") {
            contentType(ContentType.Application.Json)
            body = appendedjson
        }
    }
}
