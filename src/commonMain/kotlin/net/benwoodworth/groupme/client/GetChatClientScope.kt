package net.benwoodworth.groupme.client

import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatClient
import net.benwoodworth.groupme.client.chat.direct.DirectChatClientImpl
import net.benwoodworth.groupme.client.chat.direct.DirectMessagingScopeImpl
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatClient
import net.benwoodworth.groupme.client.chat.group.GroupChatClientImpl
import net.benwoodworth.groupme.client.chat.group.GroupMessagingScopeImpl

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


    fun getChatClient(chat: GroupChat): GroupChatClient

    fun GroupChat.getClient(): GroupChatClient {
        return getChatClient(this)
    }
}

internal class GetChatClientScopeImpl(
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : GetChatClientScope {
    override fun getChatClient(chat: Chat): ChatClient {
        throw UnsupportedOperationException("Unable to create client for $chat")
    }

    override fun getChatClient(chat: DirectChat): DirectChatClient {
        return DirectChatClientImpl(
            chat = chat,
            directMessagingScope = DirectMessagingScopeImpl(chat, httpClient, json)
        )
    }

    override fun getChatClient(chat: GroupChat): GroupChatClient {
        return GroupChatClientImpl(
            chat = chat,
            groupMessagingScope = GroupMessagingScopeImpl(chat, httpClient, json)
        )
    }
}
