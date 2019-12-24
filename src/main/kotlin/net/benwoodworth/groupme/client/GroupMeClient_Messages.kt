package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.client.chat.SentMessageInfo

interface GroupMeClient_Messages {
    suspend fun likeMessage(message: SentMessageInfo)
    suspend fun unlikeMessage(message: SentMessageInfo)

    suspend fun SentMessageInfo.like() = likeMessage(this)
    suspend fun SentMessageInfo.unlike() = unlikeMessage(this)
}
