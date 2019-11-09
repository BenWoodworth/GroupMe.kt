package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.api.GroupMeHttpClient

@GroupMeScope
interface MessageLikingScope {
    suspend fun Message.like()

    suspend fun Message.unlike()
}

internal class MessageLikingScopeImpl(
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : MessageLikingScope {
    override suspend fun Message.like() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun Message.unlike() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
