package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.Chat

fun GroupChat(
    chatId: String
): GroupChat = GroupChatImpl(
    chatId = chatId
)

private class GroupChatImpl(
    chatId: String
) : GroupChat, Chat by Chat(
    chatId = chatId
) {
    override fun toString(): String {
        return "GroupChat($chatId)"
    }
}
