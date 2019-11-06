package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.ChatClient

@GroupMeScope
interface GroupChatClient : ChatClient {
    override val chat: GroupChat

    @Deprecated(message = "Use invoke(GroupChatClient.() -> Unit) instead.", level = DeprecationLevel.HIDDEN)
    override suspend fun invoke(block: suspend ChatClient.() -> Unit): GroupChatClient

    suspend operator fun invoke(block: suspend GroupChatClient.() -> Unit): GroupChatClient
}
