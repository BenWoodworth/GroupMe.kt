package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.list
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotContext
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

internal class GroupMeClient_BotsImpl : GroupMeClient_Bots {
    lateinit var client: GroupMeClient

    override suspend fun createBot(
        name: String?,
        avatar: GroupMeImage?,
        callbackUrl: String?
    ): BotInfo {
        TODO()
    }

    override suspend fun deleteBot(bot: Bot) {
        TODO()
    }

    override fun getBots(): Flow<BotInfo> = flow {
        @Serializable
        class ResponseBot(
            val bot_id: String,
            val name: String,
            val group_id: String,
            val avatar_url: String?,
            val callback_url: String?
        )

        val response = client.httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/bots"
        )

        val responseData = client.json.parse(
            deserializer = ResponseEnvelope.serializer(ResponseBot.serializer().list),
            string = response.data
        )

        responseData.response!!.forEach {
            emit(
                BotInfo(
                    botId = it.bot_id,
                    name = it.name,
                    group = GroupChat(it.group_id),
                    avatar = it.avatar_url?.toGroupMeImage(),
                    callbackUrl = it.callback_url
                )
            )
        }
    }

    override suspend fun getBotInfo(bot: Bot): BotInfo {
        return getBots().first { it == bot }
    }

    override suspend fun setBotInfo(
        bot: Bot,
        name: String?,
        avatar: GroupMeImage?,
        callbackUrl: String?
    ): BotInfo {
        TODO()
    }

    override fun getBotContext(bot: Bot): BotContext {
        return BotContext(bot, client.httpClient, client.json)
    }
}
