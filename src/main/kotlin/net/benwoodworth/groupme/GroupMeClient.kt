package net.benwoodworth.groupme

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.chat.*
import net.benwoodworth.groupme.client.chat.direct.DirectChat
import net.benwoodworth.groupme.client.chat.direct.DirectChatInfo
import net.benwoodworth.groupme.client.chat.direct.DirectSentMessageInfo
import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.chat.group.GroupChatInfo
import net.benwoodworth.groupme.client.chat.group.GroupSentMessageInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

@Suppress("unused")
interface GroupMeClient {
    suspend fun Bot.sendMessage(message: Message)

    interface Authenticated : GroupMeClient {
        /**
         * The authenticated user.
         */
        val user: User

        suspend fun User.getInfo(): NamedUserInfo

        suspend fun User.getInfo(chat: Chat): NamedUserInfo

        suspend fun User.getInfo(chat: DirectChat): NamedUserInfo

        suspend fun User.getInfo(chat: GroupChat): NamedUserInfo

        val bots: Bots

        interface Bots {
            fun getBots(): Flow<BotInfo>

            suspend fun create(
                name: String,
                group: GroupChat,
                avatar: GroupMeImage?,
                callbackUrl: String?
            ): BotInfo
        }

        suspend fun Bot.destroy()

        suspend fun Bot.getInfo(): BotInfo

        val chats: Chats

        interface Chats {
            fun getChats(): Flow<ChatInfo>

            fun getDirectChats(): Flow<DirectChatInfo>

            fun getGroupChats(): Flow<GroupChatInfo>
        }

        fun GroupMe.DirectChat(toUser: User): DirectChat

        suspend fun Chat.sendMessage(message: Message): SentMessageInfo

        suspend fun GroupChat.sendMessage(message: Message): GroupSentMessageInfo

        suspend fun DirectChat.sendMessage(message: Message): DirectSentMessageInfo

        fun Chat.getMessages(): Flow<SentMessageInfo>

        fun DirectChat.getMessages(): Flow<DirectSentMessageInfo>

        fun GroupChat.getMessages(): Flow<GroupSentMessageInfo>

        fun Chat.getMessagesBefore(before: SentMessage): Flow<SentMessageInfo>

        fun DirectChat.getMessagesBefore(before: SentMessage): Flow<DirectSentMessageInfo>

        fun GroupChat.getMessagesBefore(before: SentMessage): Flow<GroupSentMessageInfo>

        fun Chat.getMessagesSince(since: SentMessage): Flow<SentMessageInfo>

        fun DirectChat.getMessagesSince(since: SentMessage): Flow<DirectSentMessageInfo>

        fun GroupChat.getMessagesSince(since: SentMessage): Flow<GroupSentMessageInfo>

        fun Chat.getMessagesAfter(after: SentMessage): Flow<SentMessageInfo>

        fun DirectChat.getMessagesAfter(after: SentMessage): Flow<DirectSentMessageInfo>

        fun GroupChat.getMessagesAfter(after: SentMessage): Flow<GroupSentMessageInfo>

        suspend fun Chat.getMembers(): Flow<NamedUserInfo>

        suspend fun DirectChat.getMembers(): Flow<NamedUserInfo>

        suspend fun GroupChat.getMembers(): Flow<NamedUserInfo>

        suspend fun SentMessage.like()

        suspend fun SentMessage.unlike()
    }
}