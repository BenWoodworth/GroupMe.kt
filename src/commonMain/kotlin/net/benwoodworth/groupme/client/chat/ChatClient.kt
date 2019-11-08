package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.GroupMeScope

@GroupMeScope
interface ChatClient : MessagingScope {
    val chat: Chat
}
