package net.benwoodworth.groupme.client.chat.direct

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.MessagingScope
import net.benwoodworth.groupme.client.chat.SentMessage

interface DirectMessagingScope : MessagingScope {
    override suspend fun sendMessage(message: Message): DirectSentMessage

    override suspend fun Message.send(): DirectSentMessage

    override fun getMessages(): Flow<DirectSentMessage>

    override fun getMessagesBefore(before: SentMessage): Flow<DirectSentMessage>

    override fun getMessagesSince(since: SentMessage): Flow<DirectSentMessage>

    override fun getMessagesAfter(after: SentMessage): Flow<DirectSentMessage>
}
