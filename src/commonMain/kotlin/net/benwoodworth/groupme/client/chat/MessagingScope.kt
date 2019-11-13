package net.benwoodworth.groupme.client.chat

import kotlinx.coroutines.flow.Flow

interface MessagingScope {
    suspend fun sendMessage(message: Message): SentMessage

    suspend fun Message.send(): SentMessage {
        return sendMessage(this)
    }

    fun getMessages(): Flow<SentMessage>

    fun getMessagesBefore(before: SentMessage): Flow<SentMessage>

    fun getMessagesSince(since: SentMessage): Flow<SentMessage>

    fun getMessagesAfter(after: SentMessage): Flow<SentMessage>
}
