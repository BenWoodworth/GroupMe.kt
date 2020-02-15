package net.benwoodworth.groupme.client.bot

import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.api.GroupMeHttpClient

@GroupMeScope
interface CallbackServerContext {
    suspend fun stopServer()
}

internal class CallbackServerContextImpl(
    private val server: CallbackServer,
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : CallbackServerContext {
    override suspend fun stopServer() {
        server.stop()
    }
}
