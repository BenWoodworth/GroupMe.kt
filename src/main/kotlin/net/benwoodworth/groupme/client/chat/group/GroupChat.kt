package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.Chat

interface GroupChat : Chat

private class GroupChatImpl(
    chatId: String
) : GroupChat, Chat by Chat(
    chatId = chatId
) {
    override fun toString(): String {
        return "GroupChat($chatId)"
    }
}

fun GroupChat(
    chatId: String
): GroupChat = GroupChatImpl(
    chatId = chatId
)
