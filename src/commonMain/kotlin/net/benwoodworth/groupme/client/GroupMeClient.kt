package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeDsl

interface GroupMeClient : GetUserInfoScope, GetChatClientScope {
    val authenticatedUser: AuthenticatedUser

    suspend operator fun invoke(@GroupMeDsl block: suspend GroupMeClient.() -> Unit): GroupMeClient
}
