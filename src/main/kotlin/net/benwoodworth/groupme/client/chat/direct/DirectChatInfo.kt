package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.NamedUserInfo
import net.benwoodworth.groupme.client.chat.ChatInfo

interface DirectChatInfo : DirectChat, ChatInfo {
    override val toUser: NamedUserInfo
    override val lastMessage: DirectSentMessageInfo
}
