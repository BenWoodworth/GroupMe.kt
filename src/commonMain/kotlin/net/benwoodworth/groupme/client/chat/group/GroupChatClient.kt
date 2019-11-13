package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.ChatClient

@GroupMeScope
interface GroupChatClient : ChatClient, GroupMessagingScope {
    override val chat: GroupChat
}

internal class GroupChatClientImpl(
    override val chat: GroupChat,
    groupMessagingScope: GroupMessagingScope
) : ChatClient,
    GroupMessagingScope by groupMessagingScope
