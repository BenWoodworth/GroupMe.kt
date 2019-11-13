package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.MessageLikingScope

@GroupMeScope
interface GroupMeClient : UserScope, GetChatScope, GetChatClientScope, MessageLikingScope

internal class GroupMeClientImpl(
    userScope: UserScope,
    getChatScope: GetChatScope,
    getChatClientScope: GetChatClientScope,
    messageLikingScope: MessageLikingScope
) : GroupMeClient,
    UserScope by userScope,
    GetChatScope by getChatScope,
    GetChatClientScope by getChatClientScope,
    MessageLikingScope by messageLikingScope
