package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.MessageLikingScope

@GroupMeScope
interface GroupMeClient : UserScope, GetChatScope, GetChatClientScope, MessageLikingScope

internal class GroupMeClientImpl(
    getUserInfoScope: UserScope,
    getChatScope: GetChatScope,
    getChatClientScope: GetChatClientScope,
    messageLikingScope: MessageLikingScope
) : GroupMeClient,
    UserScope by getUserInfoScope,
    GetChatScope by getChatScope,
    GetChatClientScope by getChatClientScope,
    MessageLikingScope by messageLikingScope
