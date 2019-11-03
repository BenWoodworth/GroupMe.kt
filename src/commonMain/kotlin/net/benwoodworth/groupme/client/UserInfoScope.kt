package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

interface UserInfoScope {
    suspend fun getUserInfo(user: User): UserInfo

    suspend fun User.getInfo(): UserInfo

    suspend fun getUserInfo(user: AuthenticatedUser): AuthenticatedUserInfo

    suspend fun AuthenticatedUser.getInfo(): AuthenticatedUserInfo
}
