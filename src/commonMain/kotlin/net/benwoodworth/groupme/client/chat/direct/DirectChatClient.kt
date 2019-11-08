package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.group.GroupChatClient

@GroupMeScope
interface DirectChatClient : ChatClient {
    override val chat: DirectChat

    suspend operator fun invoke(block: suspend GroupChatClient.() -> Unit): GroupChatClient
}
