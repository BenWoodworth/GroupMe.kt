package net.benwoodworth.groupme

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.flow.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.list
import net.benwoodworth.groupme.client.AuthenticatedUserInfo
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.bot.CallbackHandler
import net.benwoodworth.groupme.client.bot.CallbackServer
import net.benwoodworth.groupme.client.chat.*
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatInfo
import net.benwoodworth.groupme.client.chat.direct.DirectSentMessageInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatInfo
import net.benwoodworth.groupme.client.chat.group.GroupSentMessageInfo
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

@Suppress("unused", "MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
class GroupMe private constructor(
    private val apiToken: String
) {
    companion object {
        internal const val API_V2 = "https://v2.groupme.com"
        internal const val API_V3 = "https://api.groupme.com/v3"

        internal val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

        suspend fun getClient(apiToken: String): GroupMe {
            return GroupMe(apiToken).apply { init() }
        }

        suspend inline fun getClient(apiToken: String, block: GroupMe.() -> Unit) {
            getClient(apiToken).run { block() }
        }

        suspend fun startCallbackServer(port: Int, callbackHandler: CallbackHandler) {
            CallbackServer(port, json, callbackHandler).start()
        }
    }

    private val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }

        defaultRequest {
            header("X-Access-Token", apiToken)
        }
    }

    private suspend fun init() {
        user = User(getAuthenticatedUserInfo().userId)
    }

    //region ktor extensions
    private suspend fun <T> HttpClient.get(
        serializer: KSerializer<T>,
        block: HttpRequestBuilder.() -> Unit
    ): T {
        val response = this.get<HttpResponse> { block() }
        return json.parse(serializer, response.readText())
    }

    private suspend fun <T> HttpClient.post(
        serializer: KSerializer<T>,
        block: HttpRequestBuilder.() -> Unit
    ): T {
        val response = this.post<HttpResponse> { block() }
        return json.parse(serializer, response.readText())
    }
    //endregion

    //region users
    /**
     * The authenticated user.
     */
    lateinit var user: User
        private set

    private suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo {
        val response = client.get(ResponseEnvelope.serializer(JsonObject.serializer())) {
            url("$API_V3/users/me")
        }
        val userJson = response.response!!.jsonObject

        return AuthenticatedUserInfo(
            json = userJson,
            userId = userJson.getPrimitive("user_id").content,
            name = userJson.getPrimitive("name").content,
            avatar = userJson.getPrimitive("image_url").toGroupMeImage()
        )
    }

    suspend fun User.getInfo(): NamedUserInfo {
        if (this == user) {
            return getAuthenticatedUserInfo()
        }

        val response = client.get(ResponseEnvelope.serializer(JsonObject.serializer())) {
            url("$API_V2/users/$userId")
        }

        val userData = response.response!!.getObject("user")

        return NamedUserInfo(
            userId = userData.getPrimitive("id").content,
            name = userData.getPrimitive("name").content,
            avatar = userData.getPrimitive("avatar_url").toGroupMeImage()
        )
    }

    //region User.getInfo(chat)
    suspend fun User.getInfo(chat: Chat): NamedUserInfo {
        return when (chat) {
            is DirectChat -> getInfo(chat)
            is GroupChat -> getInfo(chat)
            else -> throw IllegalStateException()
        }
    }

    suspend fun User.getInfo(chat: DirectChat): NamedUserInfo {
        return getInfo()
    }

    suspend fun User.getInfo(chat: GroupChat): NamedUserInfo { // TODO Get only the requested user
        return chat.getMembers()
            .first { it == this }
    }
    //endregion
    //endregion

    //region bots
    val bots: Bots = Bots()

    inner class Bots internal constructor() {
        fun getBots(): Flow<BotInfo> = flow {
            val response = client.get(ResponseEnvelope.serializer(JsonObject.serializer().list)) {
                url("$API_V3/bots")
            }

            response.response!!.forEach {
                emit(BotInfo(it))
            }
        }

        suspend fun create(
            name: String?,
            avatar: GroupMeImage?,
            callbackUrl: String?
        ): BotInfo {
            TODO()
        }

        suspend fun delete(bot: Bot) {
            TODO()
        }
    }

    suspend fun Bot.getInfo(): BotInfo {
        return bots.getBots()
            .first { it == this }
    }

    suspend fun Bot.setInfo(
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
    ): BotInfo {
        TODO()
    }

    suspend fun Bot.sendMessage(message: Message) {
        val newEntries = mapOf("bot_id" to JsonPrimitive(botId))
        val appendedjson = JsonObject(message.json + newEntries)

        client.post<JsonObject>("$API_V3/bots/post") {
            body = appendedjson
        }
    }
    //endregion

    //region chats
    val chats: Chats = Chats()

    inner class Chats internal constructor() {
        //region getChats()
        fun getChats(): Flow<ChatInfo> = flow<ChatInfo> {
            getGroupChats().collect { emit(it) }
            getDirectChats().collect { emit(it) }
        }

        fun getDirectChats(): Flow<DirectChatInfo> = flow {
            var page = 1
            do {
                val response = client.get(ResponseEnvelope.serializer(JsonObject.serializer().list)) {
                    url("$API_V3/chats")
                    parameter("page", page)
                    parameter("per_page", 100)
                }

                response.response!!.forEach {
                    val otherUser = it.getObject("other_user").run {
                        NamedUserInfo(
                            userId = getPrimitive("id").content,
                            name = getPrimitive("name").content,
                            avatar = getPrimitive("avatar_url").toGroupMeImage()
                        )
                    }

                    emit(DirectChatInfo(it, user, otherUser))
                }

                page++
            } while (response.response!!.any())
        }

        fun getGroupChats(): Flow<GroupChatInfo> = flow {
            var page = 1
            do {
                val response = client.get(ResponseEnvelope.serializer(JsonObject.serializer().list)) {
                    url("$API_V3/groups")
                    parameter("page", page)
                    parameter("per_page", 100)
                    parameter("omit", "memberships")
                }

                response.response!!.forEach {
                    emit(GroupChatInfo(it))
                }

                page++
            } while (response.response!!.any())
        }
        //endregion
    }

    fun GroupMe.DirectChat(toUser: User): DirectChat {
        return DirectChat(user, toUser)
    }

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

        val response = client.get(ResponseEnvelope.serializer(DirectMessagesResponse.serializer())) {
            url("$API_V3/direct_messages")
            parameter("other_user_id", toUser.userId)
            parameter("before_id", beforeId)
            parameter("since_id", sinceId)
            parameter("after_id", afterId)
        }

        if (response.meta.code == 304) {
            return emptyList()
        }

        return response.response!!.direct_messages
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

        val response = client.get(ResponseEnvelope.serializer(GroupMessagesResponse.serializer())) {
            url("$API_V3/groups/${chatId}/messages")
            parameter("before_id", beforeId)
            parameter("since_id", sinceId)
            parameter("after_id", afterId)
        }

        if (response.meta.code == 304) {
            return emptyList()
        }

        return response.response!!.messages
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

        val response = client.post(ResponseEnvelope.serializer(JsonObject.serializer())) {
            url("$API_V3/groups/${chatId}/messages")
            body = message.json
        }

        return response.response!!.getObject("message")
            .let { GroupSentMessageInfo(it) }
    }

    suspend fun DirectChat.sendMessage(message: Message): DirectSentMessageInfo {
        @Serializable
        class DirectMessagesRequest(val direct_message: JsonObject)

        val newEntries = mapOf("recipient_id" to JsonPrimitive(toUser.userId))
        val appendedjson = JsonObject(message.json + newEntries)

        val response = client.post(ResponseEnvelope.serializer(JsonObject.serializer())) {
            url("$API_V3/direct_messages")
            body = DirectMessagesRequest(appendedjson)
        }

        return response.response!!.getObject("direct_message")
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

    //region Chat.getMembers()
    suspend fun Chat.getMembers(): Flow<NamedUserInfo> {
        return when (this) {
            is DirectChat -> getMembers()
            is GroupChat -> getMembers()
            else -> throw IllegalStateException()
        }
    }

    suspend fun DirectChat.getMembers(): Flow<NamedUserInfo> = flow {
        emit(fromUser.getInfo(this@getMembers))
        emit(toUser.getInfo(this@getMembers))
    }

    suspend fun GroupChat.getMembers(): Flow<NamedUserInfo> {
        val response = client.get(ResponseEnvelope.serializer(JsonObject.serializer())) {
            url("$API_V3/groups/${chatId}")
        }

        val members = response.response!!.getArray("members")

        return members.asFlow().map {
            NamedUserInfo(
                userId = it.jsonObject.getPrimitive("user_id").content,
                name = it.jsonObject.getPrimitive("name").content,
                nickname = it.jsonObject.getPrimitive("nickname").content,
                avatar = it.jsonObject.getPrimitive("image_url").toGroupMeImage()
            )
        }
    }
    //endregion
    //endregion

    //region messages
    suspend fun SentMessage.like() {
        client.post<Unit> {
            url("$API_V3/messages/${chat.chatId}/${messageId}/like")
        }
    }

    suspend fun SentMessage.unlike() {
        client.post<Unit> {
            url("$API_V3/messages/${chat.chatId}/${messageId}/unlike")
        }
    }
    //endregion
}
