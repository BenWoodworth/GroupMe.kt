package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.client.chat.SentMessageInfo

interface DirectSentMessageInfo : SentMessageInfo {
    override val chat: DirectChat
}
