package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.list
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotContext
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatContext
import net.benwoodworth.groupme.client.chat.ChatInfo
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatContext
import net.benwoodworth.groupme.client.chat.direct.DirectChatInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatContext
import net.benwoodworth.groupme.client.chat.group.GroupChatInfo
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

@GroupMeScope
class GroupMeClient internal constructor(
    val authenticatedUser: User,
    internal val httpClient: GroupMeHttpClient,
    internal val json: Json,

    private val messages: GroupMeClient_MessagesImpl = GroupMeClient_MessagesImpl(),
    private val users: GroupMeClient_UsersImpl = GroupMeClient_UsersImpl()
) : GroupMeClient_Messages by messages,
    GroupMeClient_Users by users {

    init {
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

    //region chats

    fun getChats(): Flow<ChatInfo> = flow<ChatInfo> {
        getGroupChats().collect { emit(it) }
        getDirectChats().collect { emit(it) }
    }

    fun getDirectChats(): Flow<DirectChatInfo> = flow {
        var page = 1
        do {
            val response = httpClient.sendApiV3Request(
                method = HttpMethod.Get,
                endpoint = "/chats",
                params = mapOf(
                    "page" to page.toString(),
                    "per_page" to "100"
                )
            )

            val responseData = json.parse(
                deserializer = ResponseEnvelope.serializer(JsonObject.serializer().list),
                string = response.data
            )

            responseData.response!!.forEach {
                val otherUser = it.getObject("other_user").run {
                    NamedUserInfo(
                        userId = getPrimitive("id").content,
                        name = getPrimitive("name").content,
                        avatar = getPrimitive("avatar_url").toGroupMeImage()
                    )
                }

                emit(DirectChatInfo(it, authenticatedUser, otherUser))
            }

            page++
        } while (responseData.response!!.any())
    }

    fun getGroupChats(): Flow<GroupChatInfo> = flow {
        var page = 1
        do {
            val response = httpClient.sendApiV3Request(
                method = HttpMethod.Get,
                endpoint = "/groups",
                params = mapOf(
                    "page" to page.toString(),
                    "per_page" to "100",
                    "omit" to "memberships"
                )
            )

            val responseData = json.parse(
                deserializer = ResponseEnvelope.serializer(JsonObject.serializer().list),
                string = response.data
            )

            responseData.response!!.forEach {
                emit(GroupChatInfo(it))
            }

            page++
        } while (responseData.response!!.any())
    }

    fun getChatContext(chat: Chat): ChatContext = when (chat) {
        is DirectChat -> getChatContext(chat)
        is GroupChat -> getChatContext(chat)
        else -> throw IllegalStateException("Unable to create client for $chat")
    }


    fun getChatContext(chat: DirectChat): DirectChatContext {
        return DirectChatContext(chat, this)
    }


    fun getChatContext(chat: GroupChat): GroupChatContext {
        return GroupChatContext(chat, this)
    }

    val Chat.context: ChatContext
        get() = getChatContext(this)

    val DirectChat.context: DirectChatContext
        get() = getChatContext(this)

    val GroupChat.context: GroupChatContext
        get() = getChatContext(this)

    suspend operator fun <T : ChatContext> T.invoke(block: suspend T.() -> Unit) = block()

    suspend operator fun DirectChat.invoke(block: suspend DirectChatContext.() -> Unit) = context { block() }
    suspend operator fun GroupChat.invoke(block: suspend GroupChatContext.() -> Unit) = context { block() }

    suspend fun DirectChat(
        fromUser: User,
        toUser: User,
        block: suspend DirectChatContext.() -> Unit
    ) = (DirectChat(fromUser, toUser)) { block() }

    suspend fun GroupChat(
        chatId: String,
        block: suspend GroupChatContext.() -> Unit
    ) = (GroupChat(chatId)) { block() }

    //endregion
}
