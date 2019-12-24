package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.client.chat.SentMessageInfo

internal class GroupMeClient_MessagesImpl : GroupMeClient_Messages {
    lateinit var client: GroupMeClient

    override suspend fun likeMessage(message: SentMessageInfo) {
        client.httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/messages/${message.chat.chatId}/${message.messageId}/like"
        )
    }

    override suspend fun unlikeMessage(message: SentMessageInfo) {
        client.httpClient.sendApiV3Request(
            method = HttpMethod.Post,
            endpoint = "/messages/${message.chat.chatId}/${message.messageId}/unlike"
        )
    }
}
