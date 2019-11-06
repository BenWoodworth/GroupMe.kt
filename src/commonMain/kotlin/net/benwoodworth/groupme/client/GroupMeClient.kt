package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeScope

@GroupMeScope
interface GroupMeClient : GetUserInfoScope, GetChatClientScope {
    val authenticatedUser: AuthenticatedUser

    suspend operator fun invoke(block: suspend GroupMeClient.() -> Unit): GroupMeClient
}
