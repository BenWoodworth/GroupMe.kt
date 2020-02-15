package net.benwoodworth.groupme

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.list
import net.benwoodworth.groupme.api.DefaultHttpClient
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.AuthenticatedUserInfo
import net.benwoodworth.groupme.client.bot.*
import net.benwoodworth.groupme.client.chat.*
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatInfo
import net.benwoodworth.groupme.client.chat.direct.DirectSentMessageInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatInfo
import net.benwoodworth.groupme.client.chat.group.GroupSentMessageInfo
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

class GroupMe private constructor(
    val authenticatedUser: User,
    internal val httpClient: GroupMeHttpClient,
    internal val json: Json
) {
    //region companion object
    companion object {
        private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

        private fun createHttpClient(apiToken: String? = null) = GroupMeHttpClient(
            DefaultHttpClient(),
            apiToken,
            "https://api.groupme.com/v3",
            "https://v2.groupme.com"
        )

        suspend fun getClient(apiToken: String): GroupMe {
            val httpClient = createHttpClient(apiToken)

            @Serializable
            class MeResponse(val id: String)

            val meResponse = httpClient.sendApiV3Request(
                method = HttpMethod.Get,
                endpoint = "/users/me"
            )

            val responseJson = json.parse(
                ResponseEnvelope.serializer(MeResponse.serializer()),
                meResponse.data
            )

            val authenticatedUser = User(responseJson.response!!.id)

            return GroupMe(authenticatedUser, httpClient, json)
        }

        suspend inline fun getClient(apiToken: String, block: GroupMe.() -> Unit) {
            getClient(apiToken).run { block() }
        }

        suspend fun startCallbackServer(port: Int, callbackHandler: CallbackHandler) {
            val server = CallbackServer(port, createHttpClient(), json, callbackHandler)
            server.start()
        }
    }
    //endregion

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
                    group = GroupChat(it.group_id),
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
    //region getChats()
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
    //endregion

    //region Chat.fetchMessages(beforeId, sinceId, afterId)
    private suspend fun DirectChat.fetchMessages(
        beforeId: String? = null,
        sinceId: String? = null,
        afterId: String? = null
    ): List<DirectSentMessageInfo> {
        @Serializable
        class DirectMessagesResponse(
            val count: Int,
            val direct_messages: List<JsonObject>
        )

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/direct_messages",
            params = mapOf(
                "other_user_id" to toUser.userId,
                "before_id" to beforeId,
                "since_id" to sinceId,
                "after_id" to afterId
            )
        )

        if (response.code == 304) {
            return emptyList()
        }

        val responseJson = json.parse(
            ResponseEnvelope.serializer(DirectMessagesResponse.serializer()),
            response.data
        )

        return responseJson.response!!.direct_messages
            .map { DirectSentMessageInfo(this, it) }
    }

    private suspend fun GroupChat.fetchMessages(
        beforeId: String? = null,
        sinceId: String? = null,
        afterId: String? = null
    ): List<GroupSentMessageInfo> {
        @Serializable
        class GroupMessagesResponse(
            val count: Int,
            val messages: List<JsonObject>
        )

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/groups/${chatId}/messages",
            params = mapOf(
                "before_id" to beforeId,
                "since_id" to sinceId,
                "after_id" to afterId
            )
        )

        if (response.code == 304) {
            return emptyList()
        }

        val responseJson = json.parse(
            ResponseEnvelope.serializer(GroupMessagesResponse.serializer()),
            response.data
        )

        return responseJson.response!!.messages
            .map { GroupSentMessageInfo(it) }
    }
    //endregion

    //region Chat.sendMessage(message)
    suspend fun Chat.sendMessage(message: Message): SentMessageInfo {
        return when (this) {
            is DirectChat -> sendMessage(message)
            is GroupChat -> sendMessage(message)
            else -> throw IllegalStateException()
        }
    }

    suspend fun GroupChat.sendMessage(message: Message): GroupSentMessageInfo {
        @Serializable
        class GroupMessagesRequest(val message: JsonObject)

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/groups/${chatId}/messages",
            body = json.stringify(GroupMessagesRequest.serializer(), GroupMessagesRequest(message.json))
        )

        val responseJson = json.parse(ResponseEnvelope.serializer(JsonObject.serializer()), response.data)

        return responseJson.response!!.getObject("message")
            .let { GroupSentMessageInfo(it) }
    }

    suspend fun DirectChat.sendMessage(message: Message): DirectSentMessageInfo {
        @Serializable
        class DirectMessagesRequest(val direct_message: JsonObject)

        val newEntries = mapOf("recipient_id" to JsonPrimitive(toUser.userId))
        val appendedjson = JsonObject(message.json + newEntries)

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/direct_messages",
            body = json.stringify(DirectMessagesRequest.serializer(), DirectMessagesRequest(appendedjson))
        )

        return json
            .parse(ResponseEnvelope.serializer(JsonObject.serializer()), response.data)
            .response!!.getObject("direct_message")
            .let { DirectSentMessageInfo(this, it) }
    }
    //endregion

    //region Chat.getMessages()
    fun Chat.getMessages(): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessages()
            is GroupChat -> getMessages()
            else -> throw IllegalStateException()
        }
    }

    fun DirectChat.getMessages(): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages()
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    fun GroupChat.getMessages(): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages()
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }
    //endregion

    //region Chat.getMessagesBefore(before)
    fun Chat.getMessagesBefore(before: SentMessage): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessagesBefore(before)
            is GroupChat -> getMessagesBefore(before)
            else -> throw IllegalStateException()
        }
    }

    fun DirectChat.getMessagesBefore(before: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(beforeId = before.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    fun GroupChat.getMessagesBefore(before: SentMessage): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages(beforeId = before.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }
    //endregion

    //region Chat.getMessagesSince(since)
    fun Chat.getMessagesSince(since: SentMessage): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessagesSince(since)
            is GroupChat -> getMessagesSince(since)
            else -> throw IllegalStateException()
        }
    }

    fun DirectChat.getMessagesSince(since: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(sinceId = since.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(sinceId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    fun GroupChat.getMessagesSince(since: SentMessage): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages(sinceId = since.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(sinceId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }
    //endregion

    //region Chat.getMessagesAfter(after)
    fun Chat.getMessagesAfter(after: SentMessage): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessagesAfter(after)
            is GroupChat -> getMessagesAfter(after)
            else -> throw IllegalStateException()
        }
    }

    fun DirectChat.getMessagesAfter(after: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(afterId = after.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(afterId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    fun GroupChat.getMessagesAfter(after: SentMessage): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages(afterId = after.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(afterId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }
    //endregion

    suspend fun GroupChat.getMembers(): List<NamedUserInfo> {
        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/groups/${chatId}"
        )

        val responseData = json.parse(
            deserializer = ResponseEnvelope.serializer(JsonObject.serializer()),
            string = response.data
        )

        val members = responseData.response!!.getArray("members")

        return members.map {
            NamedUserInfo(
                userId = it.jsonObject.getPrimitive("user_id").content,
                name = it.jsonObject.getPrimitive("name").content,
                nickname = it.jsonObject.getPrimitive("nickname").content,
                avatar = it.jsonObject.getPrimitive("image_url").toGroupMeImage()
            )
        }
    }
    //endregion

    //region messages
    suspend fun SentMessageInfo.like() {
        httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/messages/${chat.chatId}/${messageId}/like"
        )
    }

    suspend fun SentMessageInfo.unlike() {
        httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/messages/${chat.chatId}/${messageId}/unlike"
        )
    }
    //endregion

    //region users
    suspend fun getUserInfo(user: User): NamedUserInfo {
        val response = httpClient.sendApiV2Request(
            method = HttpMethod.Get,
            endpoint = "/users/${user.userId}"
        )

        val responseData = json.parse(
            deserializer = ResponseEnvelope.serializer(JsonObject.serializer()),
            string = response.data
        )

        val userData = responseData.response!!.getObject("user")

        return NamedUserInfo(
            userId = userData.getPrimitive("user_id").content,
            name = userData.getPrimitive("name").content,
            avatar = userData.getPrimitive("avatar_url").toGroupMeImage()
        )
    }

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
            avatar = userJson.getPrimitive("avatar_url").toGroupMeImage()
        )
    }

    suspend fun User.getInfo() = getUserInfo(this)
    //endregion
}
