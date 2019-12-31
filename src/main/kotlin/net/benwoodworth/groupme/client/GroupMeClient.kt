package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotContext
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

@GroupMeScope
class GroupMeClient internal constructor(
    val authenticatedUser: User,
    internal val httpClient: GroupMeHttpClient,
    internal val json: Json,

    private val chats: GroupMeClient_ChatsImpl = GroupMeClient_ChatsImpl(),
    private val messages: GroupMeClient_MessagesImpl = GroupMeClient_MessagesImpl(),
    private val users: GroupMeClient_UsersImpl = GroupMeClient_UsersImpl()
) : GroupMeClient_Chats by chats,
    GroupMeClient_Messages by messages,
    GroupMeClient_Users by users {

    init {
        chats.client = this
        messages.client = this
        users.client = this
    }

    inline operator fun invoke(block: GroupMeClient.() -> Unit) = block()

    //region bots

    suspend fun createBot(
        name: String?,
        avatar: GroupMeImage?,
        callbackUrl: String?
    ): BotInfo {
        TODO()
    }

    suspend fun deleteBot(bot: Bot) {
        TODO()
    }

    suspend fun Bot.delete() = deleteBot(this)

    fun getBots(): Flow<BotInfo> = flow {
        @Serializable
        class ResponseBot(
            val bot_id: String,
            val name: String,
            val group_id: String,
            val avatar_url: String?,
            val callback_url: String?
        )

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/bots"
        )

        val responseData = json.parse(
            deserializer = ResponseEnvelope.serializer(ResponseBot.serializer().list),
            string = response.data
        )

        responseData.response!!.forEach {
            emit(
                BotInfo(
                    botId = it.bot_id,
                    name = it.name,
                    group = net.benwoodworth.groupme.client.chat.group.GroupChat(it.group_id),
                    avatar = it.avatar_url?.toGroupMeImage(),
                    callbackUrl = it.callback_url
                )
            )
        }
    }

    suspend fun getBotInfo(bot: Bot): BotInfo {
        return getBots().first { it == bot }
    }

    suspend fun Bot.getInfo() = getBotInfo(this)

    suspend fun setBotInfo(
        bot: Bot,
        name: String?,
        avatar: GroupMeImage?,
        callbackUrl: String?
    ): BotInfo {
        TODO()
    }

    suspend fun Bot.setInfo(
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
    ) = setBotInfo(this, name, avatar, callbackUrl)

    fun getBotContext(bot: Bot): BotContext {
        return BotContext(bot, httpClient, json)
    }

    val Bot.context: BotContext
        get() = getBotContext(this)

    suspend operator fun Bot.invoke(block: suspend BotContext.() -> Unit) = context { block() }

    suspend fun Bot(botId: String, block: suspend BotContext.() -> Unit) = (Bot(botId)) { block() }

    //endregion
}
