package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.Chat

interface GroupChat : Chat

internal class GroupChatImpl(
    override val chatId: String
): GroupChat, Chat by Chat(chatId) {
    override fun toString(): String {
        return "GroupChat($chatId)"
    }
}

fun GroupChat(chatId: String): GroupChat {
    return GroupChatImpl(chatId)
}
