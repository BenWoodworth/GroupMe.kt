package net.benwoodworth.groupme.client.chat

import kotlinx.coroutines.flow.Flow

interface MessagingScope {
    suspend fun sendMessage(message: Message): SentMessageInfo

    suspend fun Message.send(): SentMessageInfo {
        return sendMessage(this)
    }

    fun getMessages(): Flow<SentMessageInfo>

    fun getMessagesBefore(before: SentMessage): Flow<SentMessageInfo>

    fun getMessagesSince(since: SentMessage): Flow<SentMessageInfo>

    fun getMessagesAfter(after: SentMessage): Flow<SentMessageInfo>
}
