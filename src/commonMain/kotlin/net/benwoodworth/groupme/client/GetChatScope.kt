package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.api.GroupMeHttpClient
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.group.GroupChat

@GroupMeScope
interface GetChatScope {
    fun getChats(): Flow<Chat>

    fun getDirectChats(): Flow<DirectChat>

    fun getGroupChats(): Flow<GroupChat>
}

internal class GetChatScopeImpl(
    private val httpClient: GroupMeHttpClient,
    private val json: Json
) : GetChatScope {
    override fun getChats(): Flow<Chat> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDirectChats(): Flow<DirectChat> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupChats(): Flow<GroupChat> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
