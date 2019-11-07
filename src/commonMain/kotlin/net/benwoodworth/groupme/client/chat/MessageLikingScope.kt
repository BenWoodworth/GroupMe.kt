package net.benwoodworth.groupme.client.chat

interface MessageLikingScope {
    suspend fun Message.like()

    suspend fun Message.unlike()
}
