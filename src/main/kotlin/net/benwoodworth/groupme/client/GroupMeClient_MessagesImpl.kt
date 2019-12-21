package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.client.chat.SentMessage

internal class GroupMeClient_MessagesImpl : GroupMeClient_Messages {
    lateinit var client: GroupMeClient

    override suspend fun likeMessage(message: SentMessage) {
        TODO()
    }

    override suspend fun unlikeMessage(message: SentMessage) {
        TODO()
    }
}
