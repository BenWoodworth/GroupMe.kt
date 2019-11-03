package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.Chat

interface GroupChat : Chat

fun GroupChat(chatId: String): GroupChat {
    return object : GroupChat, Chat by Chat(chatId) {
        override fun toString(): String {
            return "GroupChat($chatId)"
        }
    }
}
