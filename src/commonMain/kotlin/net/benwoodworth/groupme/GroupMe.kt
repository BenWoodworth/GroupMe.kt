package net.benwoodworth.groupme

import net.benwoodworth.groupme.api.DefaultHttpClient
import net.benwoodworth.groupme.api.HttpClient
import net.benwoodworth.groupme.client.GroupMeClient

object GroupMe {
    suspend fun getClient(
        apiToken: String,
        httpClient: HttpClient = DefaultHttpClient()
    ): GroupMeClient {
        TODO()
    }
}
