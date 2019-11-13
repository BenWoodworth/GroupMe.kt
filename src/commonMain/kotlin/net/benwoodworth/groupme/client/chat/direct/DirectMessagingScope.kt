package net.benwoodworth.groupme.client.chat.direct

import kotlinx.coroutines.flow.Flow
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
