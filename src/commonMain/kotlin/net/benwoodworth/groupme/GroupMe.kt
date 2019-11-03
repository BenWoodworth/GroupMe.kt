package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.GroupMeClient

object GroupMe {
    suspend fun getClient(apiToken: String, @GroupMeDsl block: suspend GroupMeClient.() -> Unit = {}): GroupMeClient {
        TODO()
    }
}
