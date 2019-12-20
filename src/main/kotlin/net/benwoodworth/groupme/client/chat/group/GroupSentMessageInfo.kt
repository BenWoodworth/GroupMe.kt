package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.SentMessageInfo

interface GroupSentMessageInfo : SentMessageInfo {
    override val chat: GroupChat
}
