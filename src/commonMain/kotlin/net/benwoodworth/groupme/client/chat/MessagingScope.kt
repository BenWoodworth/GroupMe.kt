package net.benwoodworth.groupme.client.chat

interface MessagingScope : MessageLikingScope {
    suspend fun sendMessage(message: Message): SentMessage

    suspend fun Message.send(): SentMessage {
        return sendMessage(this)
    }
}
