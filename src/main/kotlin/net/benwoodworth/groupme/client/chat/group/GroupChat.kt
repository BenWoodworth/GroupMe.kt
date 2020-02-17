package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.chat.ChatImpl

interface GroupChat : Chat

private class GroupChatImpl(
    chatId: String
) : ChatImpl(
    chatId = chatId
), GroupChat {
    override fun toString(): String {
        return "GroupChat($chatId)"
    }
}

fun GroupChat(
    chatId: String
): GroupChat = GroupChatImpl(
    chatId = chatId
)
