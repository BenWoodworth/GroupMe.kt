package net.benwoodworth.groupme.client

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.media.toGroupMeImage

internal class GroupMeClient_UsersImpl : GroupMeClient_Users {
    lateinit var client: GroupMeClient

    override suspend fun getUserInfo(user: User): NamedUserInfo {
        val response = client.httpClient.sendApiV2Request(
            method = HttpMethod.Get,
            endpoint = "/users/${user.userId}"
        )

        val responseData = client.json.parse(
            deserializer = ResponseEnvelope.serializer(JsonObject.serializer()),
            string = response.data
        )

        val userData = responseData.response!!.getObject("user")

        return NamedUserInfo(
            userId = userData.getPrimitive("user_id").content,
            name = userData.getPrimitive("name").content,
            avatar = userData.getPrimitive("avatar_url").content.toGroupMeImage()
        )
    }

    override suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo {
        val response = client.httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/users/me"
        )

        val responseData = client.json.parse(
            deserializer = ResponseEnvelope.serializer(JsonObject.serializer()),
            string = response.data
        )

        val userJson = responseData.response!!.jsonObject

        return AuthenticatedUserInfo(
            json = userJson,
            userId = userJson.getPrimitive("user_id").content,
            name = userJson.getPrimitive("name").content,
            avatar = userJson.getPrimitive("avatar_url").content.toGroupMeImage()
        )
    }
}
