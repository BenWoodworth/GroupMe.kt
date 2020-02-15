package net.benwoodworth.groupme.client.chat.group

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.GroupMe
import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.chat.ChatContext
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.SentMessage
import net.benwoodworth.groupme.client.media.toGroupMeImage

open class GroupChatContext internal constructor(
    override val chat: GroupChat,
    override val client: GroupMe
) : ChatContext {
    override suspend fun sendMessage(message: Message): GroupSentMessageInfo {
        @Serializable
        class Request(val message: JsonObject)

        val response = client.httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/groups/${chat.chatId}/messages",
            body = client.json.stringify(Request.serializer(), Request(message.json))
        )

        val responseJson = client.json.parse(ResponseEnvelope.serializer(JsonObject.serializer()), response.data)

        return responseJson.response!!.getObject("message")
            .let { GroupSentMessageInfo(it) }
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

        val response = client.httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/groups/${chat.chatId}/messages",
            params = mapOf(
                "before_id" to beforeId,
                "since_id" to sinceId,
                "after_id" to afterId
            )
        )

        if (response.code == 304) {
            return emptyList()
        }

        val responseJson = client.json.parse(
            ResponseEnvelope.serializer(Response.serializer()),
            response.data
        )

        return responseJson.response!!.messages
            .map { GroupSentMessageInfo(it) }
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

    suspend fun getMembers(): List<NamedUserInfo> {
        val response = client.httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/groups/${chat.chatId}"
        )

        val responseData = client.json.parse(
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
}
