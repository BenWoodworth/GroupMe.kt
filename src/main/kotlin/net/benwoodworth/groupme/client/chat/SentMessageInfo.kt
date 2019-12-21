package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo

interface SentMessageInfo : SentMessage, Message {
    val chat: Chat
    val sender: UserInfo
    val likes: List<User>
    val created: Long
}
