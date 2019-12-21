package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.client.chat.SentMessage

interface GroupMeClient_Messages {
    suspend fun likeMessage(message: SentMessage)
    suspend fun unlikeMessage(message: SentMessage)

    suspend fun SentMessage.like() = likeMessage(this)
    suspend fun SentMessage.unlike() = unlikeMessage(this)
}
