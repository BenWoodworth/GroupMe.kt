package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeDsl
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatClient

interface GroupMeClient {
    suspend operator fun invoke(@GroupMeDsl block: suspend GroupMeClient.() -> Unit): GroupMeClient


    fun getChatClient(chat: Chat, @GroupMeDsl block: suspend ChatClient.() -> Unit = {}): ChatClient

    fun Chat.getClient(@GroupMeDsl block: suspend ChatClient.() -> Unit = {}): ChatClient {
        return getChatClient(this, block)
    }

    suspend operator fun Chat.invoke(@GroupMeDsl block: suspend ChatClient.() -> Unit): ChatClient {
        return getChatClient(this, block)
    }
}
