package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.GroupMeScope

@GroupMeScope
interface MessageLikingScope {
    suspend fun Message.like()

    suspend fun Message.unlike()
}
