package net.benwoodworth.groupme.client.chat.group

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.MessagingScope
import net.benwoodworth.groupme.client.chat.SentMessage

interface GroupMessagingScope : MessagingScope {
    override suspend fun sendMessage(message: Message): GroupSentMessageInfo

    override suspend fun Message.send(): GroupSentMessageInfo

    override fun getMessages(): Flow<GroupSentMessageInfo>

    override fun getMessagesBefore(before: SentMessage): Flow<GroupSentMessageInfo>

    override fun getMessagesSince(since: SentMessage): Flow<GroupSentMessageInfo>

    override fun getMessagesAfter(after: SentMessage): Flow<GroupSentMessageInfo>
}
