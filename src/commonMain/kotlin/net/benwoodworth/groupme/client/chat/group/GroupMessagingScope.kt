package net.benwoodworth.groupme.client.chat.group

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.client.chat.Message
import net.benwoodworth.groupme.client.chat.MessagingScope
import net.benwoodworth.groupme.client.chat.SentMessage

interface GroupMessagingScope : MessagingScope {
    override suspend fun sendMessage(message: Message): GroupSentMessage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun Message.send(): GroupSentMessage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessages(): Flow<GroupSentMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessagesBefore(before: SentMessage): Flow<GroupSentMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessagesSince(since: SentMessage): Flow<GroupSentMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessagesAfter(after: SentMessage): Flow<GroupSentMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
