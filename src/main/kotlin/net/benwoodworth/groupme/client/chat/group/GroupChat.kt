package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.Chat

open class GroupChat(
    override val chatId: String
) : Chat() {
    override fun toString(): String {
        return "GroupChat($chatId)"
    }
}
