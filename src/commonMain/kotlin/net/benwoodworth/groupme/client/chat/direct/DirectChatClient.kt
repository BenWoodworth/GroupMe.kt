package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.group.GroupChatClient

@GroupMeScope
interface DirectChatClient : ChatClient {
    override val chat: DirectChat

    @Deprecated(message = "Use invoke(DirectChatClient.() -> Unit) instead.", level = DeprecationLevel.HIDDEN)
    override suspend fun invoke(block: suspend ChatClient.() -> Unit): ChatClient

    suspend operator fun invoke(block: suspend GroupChatClient.() -> Unit): GroupChatClient
}
