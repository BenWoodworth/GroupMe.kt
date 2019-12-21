package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

interface GroupMeClient_Users {
    suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo

    suspend fun getUserInfo(user: User): UserInfo

    suspend fun User.getInfo() = getUserInfo(this)
}
