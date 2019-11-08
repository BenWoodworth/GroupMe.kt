package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatClient
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatClient
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatClient

@GroupMeScope
interface GetChatScope {
    fun getChats(): Flow<Chat>

    fun getDirectChats(): Flow<DirectChat>

    fun getGroupChats(): Flow<GroupChat>
}
