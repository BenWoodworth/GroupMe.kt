package net.benwoodworth.groupme.client.chat

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.GroupMe
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User

@GroupMeScope
interface ChatContext {
    val client: GroupMe

    val chat: Chat

    suspend fun sendMessage(message: Message): SentMessageInfo
    suspend fun Message.send() = sendMessage(this)

    fun getMessages(): Flow<SentMessageInfo>
    fun getMessagesBefore(before: SentMessage): Flow<SentMessageInfo>
    fun getMessagesSince(since: SentMessage): Flow<SentMessageInfo>
    fun getMessagesAfter(after: SentMessage): Flow<SentMessageInfo>

    suspend fun getUserInfo(user: User) = client.getUserInfo(user)
    suspend fun User.getInfo() = getUserInfo(this)

    suspend fun getAuthenticatedUserInfo() = client.getAuthenticatedUserInfo()

    suspend fun likeMessage(message: SentMessageInfo) = client.likeMessage(message)
    suspend fun unlikeMessage(message: SentMessageInfo) = client.unlikeMessage(message)

    suspend fun SentMessageInfo.like() = likeMessage(this)
    suspend fun SentMessageInfo.unlike() = unlikeMessage(this)
}
