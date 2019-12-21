package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.User

interface GroupMeClient_Users {
    suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo

    suspend fun getUserInfo(user: User): NamedUserInfo

    suspend fun User.getInfo() = getUserInfo(this)
}
