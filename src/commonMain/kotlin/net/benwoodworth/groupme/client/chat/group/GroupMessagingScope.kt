package net.benwoodworth.groupme.client.chat.group

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.MessagingScope
import net.benwoodworth.groupme.client.chat.SentMessage

interface GroupMessagingScope : MessagingScope {
    override suspend fun sendMessage(message: Message): GroupSentMessageInfo

    override suspend fun Message.send(): GroupSentMessageInfo

    override fun getMessages(): Flow<GroupSentMessageInfo>

    override fun getMessagesBefore(before: SentMessage): Flow<GroupSentMessageInfo>

    override fun getMessagesSince(since: SentMessage): Flow<GroupSentMessageInfo>

    override fun getMessagesAfter(after: SentMessage): Flow<GroupSentMessageInfo>
}

internal class GroupMessagingScopeImpl(
    private val group: GroupChat,
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : GroupMessagingScope {
    private fun HttpClient.Response.toSentMessage(): GroupSentMessageInfo {
        val responseJson = json.parse(ResponseEnvelope.serializer(JsonObject.serializer()), data)
        return GroupSentMessageInfoImpl(responseJson.response!!.getObject("message"))
    }

    override suspend fun sendMessage(message: Message): GroupSentMessageInfo {
        @Serializable
        class Request(val message: JsonObject)

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/groups/${group.chatId}/messages",
            body = json.stringify(Request.serializer(), Request(message.messageJson))
        )

        return response.toSentMessage()
    }

    override suspend fun Message.send(): GroupSentMessageInfo {
        return sendMessage(this)
    }

    private suspend fun fetchMessages(
        beforeId: String? = null,
        sinceId: String? = null,
        afterId: String? = null
    ): List<GroupSentMessageInfo> {
        @Serializable
        class Response(
            val count: Int,
            val messages: List<JsonObject>
        )

        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/groups/${group.chatId}/messages",
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
            ResponseEnvelope.serializer(Response.serializer()),
            response.data
        )

        return responseJson.response!!.messages
            .map { GroupSentMessageInfoImpl(it) }
    }

    override fun getMessages(): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages()
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun getMessagesBefore(before: SentMessage): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages(beforeId = before.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(beforeId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun getMessagesSince(since: SentMessage): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages(sinceId = since.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(sinceId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }

    override fun getMessagesAfter(after: SentMessage): Flow<GroupSentMessageInfo> = flow {
        var messages = fetchMessages(afterId = after.messageId)
        var lastMessage = messages.lastOrNull()

        while (lastMessage != null) {
            messages.forEach { emit(it) }

            messages = fetchMessages(afterId = lastMessage.messageId)
            lastMessage = messages.lastOrNull()
        }
    }
}
