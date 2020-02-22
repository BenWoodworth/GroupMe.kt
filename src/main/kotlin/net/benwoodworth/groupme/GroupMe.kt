package net.benwoodworth.groupme

import io.ktor.client.HttpClient
import io.ktor.client.features.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.groupme.client.AuthenticatedUserInfo
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotInfo
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
    private val client: HttpClient
) : GroupMeClient.Authenticated,
    GroupMeClient by GroupMeBot(client) {

    companion object {
        internal const val API_V2 = "https://v2.groupme.com"
        internal const val API_V3 = "https://api.groupme.com/v3"

        internal val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

        suspend fun getClient(apiToken: String): GroupMe {
            val client = HttpClientFactory.create(apiToken)
            return GroupMe(client).apply { init() }
        }
    }

    private suspend fun init() {
        user = User(getAuthenticatedUserInfo().userId)
    }

    //region users
    /**
     * The authenticated user.
     */
    override lateinit var user: User
        private set

    private suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo {
        val response = client.getEnveloped<JsonObject> {
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

    override suspend fun User.getInfo(): NamedUserInfo {
        if (this == user) {
            return getAuthenticatedUserInfo()
        }

        val response = client.getEnveloped<JsonObject> {
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
    override suspend fun User.getInfo(chat: Chat): NamedUserInfo {
        return when (chat) {
            is DirectChat -> getInfo(chat)
            is GroupChat -> getInfo(chat)
            else -> throw UnsupportedOperationException("Only supports DirectChat and GroupChat types")
        }
    }

    override suspend fun User.getInfo(chat: DirectChat): NamedUserInfo {
        return getInfo()
    }

    override suspend fun User.getInfo(chat: GroupChat): NamedUserInfo { // TODO Get only the requested user
        return chat.getMembers()
            .first { it == this }
    }
    //endregion
    //endregion

    //region bots
    override val bots: GroupMeClient.Authenticated.Bots = Bots()

    private inner class Bots : GroupMeClient.Authenticated.Bots {
        override fun getBots(): Flow<BotInfo> = flow {
            val response = client.getEnveloped<List<JsonObject>> {
                url("$API_V3/bots")
            }

            response.response!!.forEach {
                emit(BotInfo(it))
            }
        }

        override suspend fun create(
            name: String,
            group: GroupChat,
            avatar: GroupMeImage?,
            callbackUrl: String?
        ): BotInfo {
            val response = client.postEnveloped<JsonObject> {
                url("$API_V3/bots")
                parameter("name", name)
                parameter("group_id", group.chatId)
                parameter("avatar_url", avatar?.imageUrl)
                parameter("callback_url", callbackUrl)
            }

            return BotInfo(response.response!!)
        }
    }

    override suspend fun Bot.destroy() {
        client.postEnveloped<JsonObject> {
            url("$API_V3/bots/destroy")
            parameter("bot_id", botId)
        }
    }

    override suspend fun Bot.getInfo(): BotInfo {
        return bots.getBots()
            .first { it == this }
    }
    //endregion

    //region chats
    override val chats: GroupMeClient.Authenticated.Chats = Chats()

    private inner class Chats : GroupMeClient.Authenticated.Chats {
        //region getChats()
        override fun getChats(): Flow<ChatInfo> = flow<ChatInfo> {
            getGroupChats().collect { emit(it) }
            getDirectChats().collect { emit(it) }
        }

        override fun getDirectChats(): Flow<DirectChatInfo> = flow {
            var page = 1
            do {
                val response = client.getEnveloped<List<JsonObject>> {
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

        override fun getGroupChats(): Flow<GroupChatInfo> = flow {
            var page = 1
            do {
                val response = client.getEnveloped<List<JsonObject>> {
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

    override fun GroupMe.DirectChat(toUser: User): DirectChat {
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

        val response = client.get<HttpResponse> {
            url("$API_V3/direct_messages")
            parameter("other_user_id", toUser.userId)
            parameter("before_id", beforeId)
            parameter("since_id", sinceId)
            parameter("after_id", afterId)
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.toResponseEnvelope<DirectMessagesResponse>()
                    .response!!.direct_messages
                    .map { DirectSentMessageInfo(this, it) }
            }
            HttpStatusCode.NotModified -> {
                emptyList()
            }
            else -> {
                throw ResponseException(response)
            }
        }
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

        val response = client.get<HttpResponse> {
            url("$API_V3/groups/${chatId}/messages")
            parameter("before_id", beforeId)
            parameter("since_id", sinceId)
            parameter("after_id", afterId)
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.toResponseEnvelope<GroupMessagesResponse>()
                    .response!!.messages
                    .map { GroupSentMessageInfo(it) }
            }
            HttpStatusCode.NotModified -> {
                emptyList()
            }
            else -> {
                throw ResponseException(response)
            }
        }
    }
    //endregion

    //region Chat.sendMessage(message)
    override suspend fun Chat.sendMessage(message: Message): SentMessageInfo {
        return when (this) {
            is DirectChat -> sendMessage(message)
            is GroupChat -> sendMessage(message)
            else -> throw UnsupportedOperationException("Only supports DirectChat and GroupChat types")
        }
    }

    override suspend fun GroupChat.sendMessage(message: Message): GroupSentMessageInfo {
        @Serializable
        class GroupMessagesRequest(val message: JsonObject)

        val response = client.postEnveloped<JsonObject> {
            url("$API_V3/groups/${chatId}/messages")
            contentType(ContentType.Application.Json)
            body = message.json
        }

        return response.response!!.getObject("message")
            .let { GroupSentMessageInfo(it) }
    }

    override suspend fun DirectChat.sendMessage(message: Message): DirectSentMessageInfo {
        @Serializable
        class DirectMessagesRequest(val direct_message: JsonObject)

        val newEntries = mapOf("recipient_id" to JsonPrimitive(toUser.userId))
        val appendedjson = JsonObject(message.json + newEntries)

        val response = client.postEnveloped<JsonObject> {
            url("$API_V3/direct_messages")
            contentType(ContentType.Application.Json)
            body = DirectMessagesRequest(appendedjson)
        }

        return response.response!!.getObject("direct_message")
            .let { DirectSentMessageInfo(this, it) }
    }
    //endregion

    override suspend fun User.sendMessage(message: Message): DirectSentMessageInfo {
        return DirectChat(this).sendMessage(message)
    }

    //region Chat.getMessages()
    override fun Chat.getMessages(): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessages()
            is GroupChat -> getMessages()
            else -> throw UnsupportedOperationException("Only supports DirectChat and GroupChat types")
        }
    }

    override fun DirectChat.getMessages(): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages()
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun GroupChat.getMessages(): Flow<GroupSentMessageInfo> = flow {
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
    override fun Chat.getMessagesBefore(before: SentMessage): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessagesBefore(before)
            is GroupChat -> getMessagesBefore(before)
            else -> throw UnsupportedOperationException("Only supports DirectChat and GroupChat types")
        }
    }

    override fun DirectChat.getMessagesBefore(before: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(beforeId = before.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun GroupChat.getMessagesBefore(before: SentMessage): Flow<GroupSentMessageInfo> = flow {
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
    override fun Chat.getMessagesSince(since: SentMessage): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessagesSince(since)
            is GroupChat -> getMessagesSince(since)
            else -> throw UnsupportedOperationException("Only supports DirectChat and GroupChat types")
        }
    }

    override fun DirectChat.getMessagesSince(since: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(sinceId = since.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(sinceId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun GroupChat.getMessagesSince(since: SentMessage): Flow<GroupSentMessageInfo> = flow {
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
    override fun Chat.getMessagesAfter(after: SentMessage): Flow<SentMessageInfo> {
        return when (this) {
            is DirectChat -> getMessagesAfter(after)
            is GroupChat -> getMessagesAfter(after)
            else -> throw UnsupportedOperationException("Only supports DirectChat and GroupChat types")
        }
    }

    override fun DirectChat.getMessagesAfter(after: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(afterId = after.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(afterId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun GroupChat.getMessagesAfter(after: SentMessage): Flow<GroupSentMessageInfo> = flow {
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
    override suspend fun Chat.getMembers(): Flow<NamedUserInfo> {
        return when (this) {
            is DirectChat -> getMembers()
            is GroupChat -> getMembers()
            else -> throw UnsupportedOperationException("Only supports DirectChat and GroupChat types")
        }
    }

    override suspend fun DirectChat.getMembers(): Flow<NamedUserInfo> = flow {
        emit(fromUser.getInfo(this@getMembers))
        emit(toUser.getInfo(this@getMembers))
    }

    override suspend fun GroupChat.getMembers(): Flow<NamedUserInfo> {
        val response = client.getEnveloped<JsonObject> {
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
    override suspend fun SentMessage.like() {
        client.post<Unit> {
            url("$API_V3/messages/${chat.chatId}/${messageId}/like")
        }
    }

    override suspend fun SentMessage.unlike() {
        client.post<Unit> {
            url("$API_V3/messages/${chat.chatId}/${messageId}/unlike")
        }
    }
    //endregion
}
