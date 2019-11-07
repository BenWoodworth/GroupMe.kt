package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.GroupMeScope

@GroupMeScope
interface MessagingScope : MessageLikingScope {
    suspend fun sendMessage(message: Message): SentMessage

    suspend fun Message.send(): SentMessage {
        return sendMessage(this)
    }
}
