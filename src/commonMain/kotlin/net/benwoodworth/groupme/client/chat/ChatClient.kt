package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.GroupMeScope

@GroupMeScope
interface ChatClient {
    val chat: Chat

    suspend operator fun invoke(block: suspend ChatClient.() -> Unit): ChatClient
}
