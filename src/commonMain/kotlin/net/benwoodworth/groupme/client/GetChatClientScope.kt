package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatClient
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatClient

@GroupMeScope
interface GetChatClientScope {
    fun getChatClient(chat: Chat): ChatClient

    fun Chat.getClient(): ChatClient {
        return getChatClient(this)
    }


    fun getChatClient(chat: DirectChat): DirectChatClient

    fun DirectChat.getClient(): DirectChatClient {
        return getChatClient(this)
    }

    suspend operator fun DirectChat.invoke(block: suspend DirectChatClient.() -> Unit): DirectChatClient {
        return getChatClient(this)
    }


    fun getChatClient(chat: GroupChat): GroupChatClient

    fun GroupChat.getClient(): GroupChatClient {
        return getChatClient(this)
    }

    suspend operator fun GroupChat.invoke(block: suspend GroupChatClient.() -> Unit): GroupChatClient {
        return getChatClient(this)
    }
}
