package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Chat

open class DirectChat internal constructor(
    val fromUser: User,
    val toUser: User
) : Chat() {
    override val chatId: String
        get() = "${fromUser.userId}+${toUser.userId}"

    override fun toString(): String {
        return "DirectChat($fromUser, $toUser)"
    }
}
