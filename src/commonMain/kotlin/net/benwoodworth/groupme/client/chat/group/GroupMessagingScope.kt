package net.benwoodworth.groupme.client.chat.group

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.MessagingScope
import net.benwoodworth.groupme.client.chat.SentMessage

interface GroupMessagingScope : MessagingScope {
    override suspend fun sendMessage(message: Message): GroupSentMessage

    override suspend fun Message.send(): GroupSentMessage

    override fun getMessages(): Flow<GroupSentMessage>

    override fun getMessagesBefore(before: SentMessage): Flow<GroupSentMessage>

    override fun getMessagesSince(since: SentMessage): Flow<GroupSentMessage>

    override fun getMessagesAfter(after: SentMessage): Flow<GroupSentMessage>
}
