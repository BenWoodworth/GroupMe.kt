package net.benwoodworth.groupme.client.chat

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.GroupMeScope

@GroupMeScope
interface MessagingScope : MessageLikingScope {
    suspend fun sendMessage(message: Message): SentMessage

    suspend fun Message.send(): SentMessage {
        return sendMessage(this)
    }

    fun getMessages(): Flow<SentMessage>

    fun getMessagesBefore(before: SentMessage): Flow<SentMessage>

    fun getMessagesSince(since: SentMessage): Flow<SentMessage>

    fun getMessagesAfter(after: SentMessage): Flow<SentMessage>
}
