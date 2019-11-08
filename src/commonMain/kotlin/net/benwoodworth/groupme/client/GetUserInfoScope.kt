package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

interface GetUserInfoScope {
    suspend fun getUserInfo(user: User): UserInfo

    suspend fun User.getInfo(): UserInfo

    suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo
}
