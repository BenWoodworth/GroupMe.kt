package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.Chat

interface DirectChat : Chat {
    val fromUser: User
    val toUser: User
}
