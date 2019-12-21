package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.list
import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.api.HttpMethod
import net.benwoodworth.groupme.api.ResponseEnvelope
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatContext
import net.benwoodworth.groupme.client.chat.ChatInfo
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatContext
import net.benwoodworth.groupme.client.chat.direct.DirectChatInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatContext
import net.benwoodworth.groupme.client.chat.group.GroupChatInfo
import net.benwoodworth.groupme.client.media.toGroupMeImage

internal class GroupMeClient_ChatsImpl : GroupMeClient_Chats {
    lateinit var client: GroupMeClient

    override fun getChats(): Flow<ChatInfo> = flow<ChatInfo> {
        getGroupChats().collect { emit(it) }
        getDirectChats().collect { emit(it) }
    }

    override fun getDirectChats(): Flow<DirectChatInfo> = flow {
        var page = 1
        do {
            val response = client.httpClient.sendApiV3Request(
                method = HttpMethod.Get,
                endpoint = "/chats",
                params = mapOf(
                    "page" to page.toString(),
                    "per_page" to "100"
                )
            )

            val responseData = client.json.parse(
                deserializer = ResponseEnvelope.serializer(JsonObject.serializer().list),
                string = response.data
            )

            responseData.response!!.forEach {
                val otherUser = it.getObject("other_user").run {
                    NamedUserInfo(
                        userId = getPrimitive("id").content,
                        name = getPrimitive("name").content,
                        avatar = getPrimitive("avatar_url").content.toGroupMeImage()
                    )
                }

                emit(DirectChatInfo(it, client.authenticatedUser, otherUser))
            }

            page++
        } while (responseData.response!!.any())
    }

    override fun getGroupChats(): Flow<GroupChatInfo> = flow {
        var page = 1
        do {
            val response = client.httpClient.sendApiV3Request(
                method = HttpMethod.Get,
                endpoint = "/groups",
                params = mapOf(
                    "page" to page.toString(),
                    "per_page" to "100",
                    "omit" to "memberships"
                )
            )

            val responseData = client.json.parse(
                deserializer = ResponseEnvelope.serializer(JsonObject.serializer().list),
                string = response.data
            )

            responseData.response!!.forEach {
                emit(GroupChatInfo(it))
            }

            page++
        } while (responseData.response!!.any())
    }

    override fun getChatContext(chat: Chat): ChatContext = when (chat) {
        is DirectChat -> getChatContext(chat)
        is GroupChat -> getChatContext(chat)
        else -> throw IllegalStateException("Unable to create client for $chat")
    }


    override fun getChatContext(chat: DirectChat): DirectChatContext {
        return DirectChatContext(chat, client)
    }


    override fun getChatContext(chat: GroupChat): GroupChatContext {
        return GroupChatContext(chat, client)
    }
}
