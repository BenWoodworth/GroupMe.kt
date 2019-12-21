package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.chat.ChatInfo

interface DirectChatInfo : DirectChat, ChatInfo {
    override val toUser: UserInfo
    override val lastMessage: DirectSentMessageInfo
}
