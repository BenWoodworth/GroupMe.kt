package net.benwoodworth.groupme.client.bot

interface CallbackServerContext {
    suspend fun stop()
}

internal class CallbackServerContextImpl(
    private val server: CallbackServer
) : CallbackServerContext {
    override suspend fun stop() {
        server.stop()
    }
}
