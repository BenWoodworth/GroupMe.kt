package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeDsl
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatClient
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatClient

interface GroupMeClient {
    suspend operator fun invoke(@GroupMeDsl block: suspend GroupMeClient.() -> Unit): GroupMeClient


    fun getChatClient(chat: Chat, @GroupMeDsl block: suspend ChatClient.() -> Unit = {}): ChatClient

    fun Chat.getClient(@GroupMeDsl block: suspend ChatClient.() -> Unit = {}): ChatClient {
        return getChatClient(this, block)
    }

    suspend operator fun Chat.invoke(@GroupMeDsl block: suspend ChatClient.() -> Unit): ChatClient {
        return getChatClient(this, block)
    }


    fun getChatClient(chat: DirectChat, @GroupMeDsl block: suspend DirectChatClient.() -> Unit = {}): DirectChatClient

    fun DirectChat.getClient(@GroupMeDsl block: suspend DirectChatClient.() -> Unit = {}): DirectChatClient {
        return getChatClient(this, block)
    }

    suspend operator fun DirectChat.invoke(@GroupMeDsl block: suspend DirectChatClient.() -> Unit): DirectChatClient {
        return getChatClient(this, block)
    }


    fun getChatClient(chat: GroupChat, @GroupMeDsl block: suspend GroupChatClient.() -> Unit = {}): GroupChatClient

    fun GroupChat.getClient(@GroupMeDsl block: suspend GroupChatClient.() -> Unit = {}): GroupChatClient {
        return getChatClient(this, block)
    }

    suspend operator fun GroupChat.invoke(@GroupMeDsl block: suspend GroupChatClient.() -> Unit): GroupChatClient {
        return getChatClient(this, block)
    }
}
