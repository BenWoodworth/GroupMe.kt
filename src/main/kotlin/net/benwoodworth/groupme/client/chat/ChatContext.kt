package net.benwoodworth.groupme.client.chat

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.AuthenticatedUserInfo
import net.benwoodworth.groupme.client.GroupMeClient

@GroupMeScope
abstract class ChatContext internal constructor(
    protected val client: GroupMeClient
) {
    abstract val chat: Chat

    abstract suspend fun sendMessage(message: Message): SentMessageInfo

    abstract suspend fun Message.send(): SentMessageInfo

    abstract fun getMessages(): Flow<SentMessageInfo>

    abstract fun getMessagesBefore(before: SentMessage): Flow<SentMessageInfo>

    abstract fun getMessagesSince(since: SentMessage): Flow<SentMessageInfo>

    abstract fun getMessagesAfter(after: SentMessage): Flow<SentMessageInfo>


    suspend fun getUserInfo(user: User): UserInfo {
        return client.getUserInfo(user)
    }

    suspend fun User.getInfo() = getUserInfo(this)

    suspend fun getAuthenticatedUserInfo(): AuthenticatedUserInfo {
        return client.getAuthenticatedUserInfo()
    }


    suspend fun likeMessage() {
        return client.likeMessage()
    }

    suspend fun Message.like() = likeMessage()

    suspend fun unlikeMessage() {
        return client.unlikeMessage()
    }

    suspend fun Message.unlike() = unlikeMessage()
}
