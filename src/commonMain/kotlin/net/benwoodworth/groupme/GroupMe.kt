package net.benwoodworth.groupme

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.benwoodworth.groupme.api.*
import net.benwoodworth.groupme.client.*
import net.benwoodworth.groupme.client.chat.MessageLikingScopeImpl

object GroupMe {
    suspend fun getClient(
        apiToken: String,
        httpClient: HttpClient = DefaultHttpClient()
    ): GroupMeClient {
        val groupMeHttpClient = GroupMeHttpClient(httpClient, apiToken, "https://api.groupme.com/v3")
        val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

        @Serializable
        class MeResponse(val id: String)

        val meResponse = groupMeHttpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/users/me"
        )

        val responseJson = json.parse(
            ResponseEnvelope.serializer(MeResponse.serializer()),
            meResponse.data
        )

        val authenticatedUser = User(responseJson.response!!.id)

        return GroupMeClientImpl(
            userScope = UserScopeImpl(authenticatedUser, groupMeHttpClient, json),
            getChatScope = GetChatScopeImpl(groupMeHttpClient, json),
            getChatClientScope = GetChatClientScopeImpl(groupMeHttpClient, json),
            messageLikingScope = MessageLikingScopeImpl(groupMeHttpClient, json)
        )
    }
}
