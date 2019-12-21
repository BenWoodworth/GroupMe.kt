package net.benwoodworth.groupme.client

import kotlinx.serialization.json.Json
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.api.GroupMeHttpClient

@GroupMeScope
class GroupMeClient internal constructor(
    val authenticatedUser: User,
    internal val httpClient: GroupMeHttpClient,
    internal val json: Json,

    private val bots: GroupMeClient_BotsImpl = GroupMeClient_BotsImpl(),
    private val chats: GroupMeClient_ChatsImpl = GroupMeClient_ChatsImpl(),
    private val messages: GroupMeClient_MessagesImpl = GroupMeClient_MessagesImpl(),
    private val users: GroupMeClient_UsersImpl = GroupMeClient_UsersImpl()
) : GroupMeClient_Bots by bots,
    GroupMeClient_Chats by chats,
    GroupMeClient_Messages by messages,
    GroupMeClient_Users by users {

    init {
        bots.client = this
        chats.client = this
        messages.client = this
        users.client = this
    }

    inline operator fun invoke(block: GroupMeClient.() -> Unit) = block()
}
