package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.ChatClient

@GroupMeScope
interface GroupChatClient : ChatClient {
    override val chat: GroupChat

    suspend operator fun invoke(block: suspend GroupChatClient.() -> Unit): GroupChatClient
}
