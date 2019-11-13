package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.GroupMeScope
import net.benwoodworth.groupme.client.chat.ChatClient

@GroupMeScope
interface DirectChatClient : ChatClient {
    override val chat: DirectChat
}
