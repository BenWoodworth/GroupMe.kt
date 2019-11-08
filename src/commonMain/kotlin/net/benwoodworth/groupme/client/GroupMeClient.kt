package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.MessageLikingScope

@GroupMeScope
interface GroupMeClient : GetUserInfoScope, GetChatScope, GetChatClientScope, MessageLikingScope {
    val authenticatedUser: AuthenticatedUser

    suspend operator fun invoke(block: suspend GroupMeClient.() -> Unit): GroupMeClient
}
