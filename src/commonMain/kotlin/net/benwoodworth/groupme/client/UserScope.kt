package net.benwoodworth.groupme.client

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.media.GroupMeImage

interface UserScope {
    val authenticatedUser: User

    suspend fun getUserInfo(user: User): UserInfo

    suspend fun User.getInfo(): UserInfo {
        return getUserInfo(this)
    }

    suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo
}

internal class UserScopeImpl(
    override val authenticatedUser: User,
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : UserScope {
    override suspend fun getUserInfo(user: User): UserInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo {
        val response = httpClient.sendApiV3Request(
            method = HttpMethod.Get,
            endpoint = "/users/me"
        )

        val responseData = json.parse(
            deserializer = ResponseEnvelope.serializer(AuthenticatedUserInfoResponse.serializer()),
            string = response.data
        )

        return responseData.response!!.run {
            AuthenticatedUserInfo(
                userId = id,
                name = name,
                avatar = GroupMeImage(image_url)
            )
        }
    }

    @Serializable
    private class AuthenticatedUserInfoResponse(
        val id: String,
        val phone_number: String,
        val image_url: String,
        val name: String,
        val created_at: Long,
        val updated_at: Long,
        val email: String,
        val sms: Boolean
    )
}
