package net.benwoodworth.groupme.client.chat.direct

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.MessagingScope
import net.benwoodworth.groupme.client.chat.SentMessage

interface DirectMessagingScope : MessagingScope {
    override suspend fun sendMessage(message: Message): DirectSentMessageInfo

    override suspend fun Message.send(): DirectSentMessageInfo

    override fun getMessages(): Flow<DirectSentMessageInfo>

    override fun getMessagesBefore(before: SentMessage): Flow<DirectSentMessageInfo>

    override fun getMessagesSince(since: SentMessage): Flow<DirectSentMessageInfo>

    override fun getMessagesAfter(after: SentMessage): Flow<DirectSentMessageInfo>
}

internal class DirectMessagingScopeImpl(
    private val chat: DirectChat,
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : DirectMessagingScope {
    private fun HttpClient.Response.toSentMessage(): DirectSentMessageInfo {
        val responseJson = json.parse(ResponseEnvelope.serializer(JsonObject.serializer()), data)
        return DirectSentMessageInfo(responseJson.response!!.getObject("direct_message"), chat)
    }

    override suspend fun sendMessage(message: Message): DirectSentMessageInfo {
        @Serializable
        class Request(val direct_message: JsonObject)

        val newEntries = mapOf("recipient_id" to JsonPrimitive(chat.toUser.userId))
        val appendedMessageJson = JsonObject(message.messageJson + newEntries)

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/direct_messages",
            body = json.stringify(Request.serializer(), Request(appendedMessageJson))
        )

        return response.toSentMessage()
    }

    override suspend fun Message.send(): DirectSentMessageInfo {
        return sendMessage(this)
    }

    private suspend fun fetchMessages(
        beforeId: String? = null,
        sinceId: String? = null,
        afterId: String? = null
    ): List<DirectSentMessageInfo> {
        @Serializable
        class Response(
            val count: Int,
            val messages: List<JsonObject>
        )

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/direct_messages",
            params = mapOf(
                "other_user_id" to chat.toUser.userId,
                "before_id" to beforeId,
                "since_id" to sinceId,
                "after_id" to afterId
            )
        )

        if (response.code == 304) {
            return emptyList()
        }

        val responseJson = json.parse(
            ResponseEnvelope.serializer(Response.serializer()),
            response.data
        )

        return responseJson.response!!.messages
            .map { DirectSentMessageInfo(it, chat) }
    }

    override fun getMessages(): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages()
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun getMessagesBefore(before: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(beforeId = before.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun getMessagesSince(since: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(sinceId = since.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(sinceId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun getMessagesAfter(after: SentMessage): Flow<DirectSentMessageInfo> = flow {
        var messages = fetchMessages(afterId = after.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(afterId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }
}
