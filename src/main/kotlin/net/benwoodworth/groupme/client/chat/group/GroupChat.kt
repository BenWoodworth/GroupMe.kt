package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.Chat

interface GroupChat : Chat

fun GroupChat(
    chatId: String
): GroupChat = object : GroupChat, Chat by Chat(
    chatId = chatId
) {
    override fun toString(): String {
        return "GroupChat($chatId)"
    }
}
