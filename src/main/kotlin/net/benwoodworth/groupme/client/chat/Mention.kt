package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.User

data class Mention(
    val user: User,
    val start: Int,
    val length: Int
) {
    val end: Int
        get() = start + length

    operator fun contains(index: Int): Boolean {
        return index in start until end
    }

    operator fun contains(user: User): Boolean {
        return this.user == user
    }
}
