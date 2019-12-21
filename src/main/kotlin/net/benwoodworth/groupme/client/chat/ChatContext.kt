package net.benwoodworth.groupme.client.chat

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.GroupMeClient

@GroupMeScope
abstract class ChatContext internal constructor(
    val client: GroupMeClient
) {
    abstract val chat: Chat

    abstract suspend fun sendMessage(message: Message): SentMessageInfo
    suspend fun Message.send() = sendMessage(this)

    abstract fun getMessages(): Flow<SentMessageInfo>
    abstract fun getMessagesBefore(before: SentMessage): Flow<SentMessageInfo>
    abstract fun getMessagesSince(since: SentMessage): Flow<SentMessageInfo>
    abstract fun getMessagesAfter(after: SentMessage): Flow<SentMessageInfo>

    suspend fun getUserInfo(user: User) = client.getUserInfo(user)
    suspend fun User.getInfo() = getUserInfo(this)

    suspend fun getAuthenticatedUserInfo() = client.getAuthenticatedUserInfo()

    suspend fun likeMessage(message: SentMessage) = client.likeMessage(message)
    suspend fun unlikeMessage(message: SentMessage) = client.unlikeMessage(message)

    suspend fun SentMessage.like() = likeMessage(this)
    suspend fun SentMessage.unlike() = unlikeMessage(this)
}
