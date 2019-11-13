package net.benwoodworth.groupme.client

import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatClient
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
        val chatIdParts = chat.chatId.split('+')

        return when (chatIdParts.size) {
            1 -> getChatClient(GroupChat(chatIdParts[0]))
            2 -> getChatClient(DirectChat(User(chatIdParts[0]), User(chatIdParts[1])))
            else -> throw UnsupportedOperationException("Unable to create client for chat $chat")
        }
    }

    override fun getChatClient(chat: DirectChat): DirectChatClient {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChatClient(chat: GroupChat): GroupChatClient {
        return GroupChatClientImpl(
            chat = chat,
            groupMessagingScope = GroupMessagingScopeImpl(chat, httpClient, json)
        )
    }
}
