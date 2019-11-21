package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatClient
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatClient
import net.benwoodworth.groupme.client.media.GroupMeImage

@GroupMeScope
class GroupMeClient internal constructor(
    val authenticatedUser: User,
    internal val httpClient: GroupMeHttpClient,
    internal val json: Json
) {
    suspend fun getUserInfo(user: User): UserInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    suspend fun User.getInfo() = getUserInfo(this)

    suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo {
        @Serializable
        class Response(
            val id: String,
            val image_url: String,
            val name: String
        )

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/users/me"
        )

        val responseData = json.parse(
            deserializer = ResponseEnvelope.serializer(Response.serializer()),
            string = response.data
        )

        return responseData.response!!.run {
            AuthenticatedUserInfo(
                userId = id,
                name = name,
                avatar = GroupMeImage(image_url)
            )
        }
    }


    fun getChats(): Flow<Chat> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getDirectChats(): Flow<DirectChat> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getGroupChats(): Flow<GroupChat> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getChatClient(chat: Chat): ChatClient {
        TODO()
    }

    val Chat.client
        get() = getChatClient(this)

    fun getChatClient(chat: DirectChat): DirectChatClient {
        TODO()
    }

    val DirectChat.client
        get() = getChatClient(this)

    fun getChatClient(chat: GroupChat): GroupChatClient {
        TODO()
    }

    val GroupChat.client
        get() = getChatClient(this)


    suspend fun likeMessage() {
        TODO()
    }

    suspend fun Message.like() = likeMessage()

    suspend fun unlikeMessage() {
        TODO()
    }

    suspend fun Message.unlike() = unlikeMessage()


    suspend fun createBot(
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
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
                    group = GroupChat(it.group_id),
                    avatar = it.avatar_url?.let { url -> GroupMeImage(url) },
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
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
    ): BotInfo {
        TODO()
    }

    suspend fun Bot.setInfo(
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
    ) = setBotInfo(this, name, avatar, callbackUrl)
}
