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
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatContext
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatContext
import net.benwoodworth.groupme.client.chat.direct.DirectChatInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatContext
import net.benwoodworth.groupme.client.chat.group.GroupChatInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

@GroupMeScope
class GroupMeClient internal constructor(
    val authenticatedUser: User,
    internal val httpClient: GroupMeHttpClient,
    internal val json: Json
) {
    suspend fun getUserInfo(user: User): UserInfo {
        val response = httpClient.sendApiV2Request(
            method = HttpMethod.Get,
            endpoint = "/users/${user.userId}"
        )

        val responseData = json.parse(
            deserializer = ResponseEnvelope.serializer(JsonObject.serializer()),
            string = response.data
        )

        val userData = responseData.response!!.getObject("user")

        return UserInfo(
            userId = userData["user_id"]!!.primitive.content,
            name = userData["name"]!!.primitive.content,
            avatar = userData["avatar_url"]!!.primitive.content?.let { GroupMeImage(it) }
        )
    }

    suspend fun User.getInfo() = getUserInfo(this)

    suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo {
        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/users/me"
        )

        val responseData = json.parse(
            deserializer = ResponseEnvelope.serializer(JsonObject.serializer()),
            string = response.data
        )

        val userJson = responseData.response!!.jsonObject

        return AuthenticatedUserInfo(
            json = userJson,
            userId = userJson.getPrimitive("user_id").content,
            name = userJson.getPrimitive("name").content,
            avatar = userJson.getPrimitive("avatar_url").contentOrNull?.let { GroupMeImage(it) }
        )
    }


    fun getChats(): Flow<Chat> = flow {
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
                    UserInfo(
                        userId = getPrimitive("id").content,
                        name = getPrimitive("name").content,
                        avatar = getPrimitive("avatar_url").contentOrNull?.let { url -> GroupMeImage(url) }
                    )
                }

                emit(DirectChatInfo(authenticatedUser, otherUser))
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

    fun getChatClient(chat: Chat): ChatContext {
        throw IllegalStateException("Unable to create client for $chat")
    }

    val Chat.client
        get() = getChatClient(this)

    fun getChatClient(chat: DirectChat): DirectChatContext {
        return DirectChatContext(chat, this)
    }

    val DirectChat.client
        get() = getChatClient(this)

    fun getChatClient(chat: GroupChat): GroupChatContext {
        return GroupChatContext(chat, this)
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
