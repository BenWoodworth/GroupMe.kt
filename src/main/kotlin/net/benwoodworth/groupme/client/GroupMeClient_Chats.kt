package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatContext
import net.benwoodworth.groupme.client.chat.ChatInfo
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatContext
import net.benwoodworth.groupme.client.chat.direct.DirectChatInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatContext
import net.benwoodworth.groupme.client.chat.group.GroupChatInfo

interface GroupMeClient_Chats {
    fun getChats(): Flow<ChatInfo>
    fun getDirectChats(): Flow<DirectChatInfo>
    fun getGroupChats(): Flow<GroupChatInfo>

    fun getChatContext(chat: Chat): ChatContext
    fun getChatContext(chat: DirectChat): DirectChatContext
    fun getChatContext(chat: GroupChat): GroupChatContext

    val Chat.context: ChatContext
        get() = getChatContext(this)

    val DirectChat.context: DirectChatContext
        get() = getChatContext(this)

    val GroupChat.context: GroupChatContext
        get() = getChatContext(this)

    suspend operator fun <T : ChatContext> T.invoke(block: suspend T.() -> Unit) = block()

    suspend operator fun DirectChat.invoke(block: suspend DirectChatContext.() -> Unit) = context { block() }
    suspend operator fun GroupChat.invoke(block: suspend GroupChatContext.() -> Unit) = context { block() }

    suspend fun DirectChat(
        fromUser: User,
        toUser: User,
        block: suspend DirectChatContext.() -> Unit
    ) = (DirectChat(fromUser, toUser)) { block() }

    suspend fun GroupChat(
        chatId: String,
        block: suspend GroupChatContext.() -> Unit
    ) = (GroupChat(chatId)) { block() }
}
