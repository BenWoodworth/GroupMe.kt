package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.MessageLikingScope

@GroupMeScope
interface GroupMeClient : GetUserInfoScope, GetChatScope, GetChatClientScope, MessageLikingScope {
    val authenticatedUser: User
}

internal class GroupMeClientImpl(
    override val authenticatedUser: User,
    getUserInfoScope: GetUserInfoScope,
    getChatScope: GetChatScope,
    getChatClientScope: GetChatClientScope,
    messageLikingScope: MessageLikingScope
) : GroupMeClient,
    GetUserInfoScope by getUserInfoScope,
    GetChatScope by getChatScope,
    GetChatClientScope by getChatClientScope,
    MessageLikingScope by messageLikingScope
