package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.User

data class Mention(
    val user: User,
    val location: Int,
    val length: Int
)
