package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.User

interface AuthenticatedUser : User

internal fun AuthenticatedUser(userId: String): AuthenticatedUser {
    return object : AuthenticatedUser, User by User(userId) {
        override fun toString(): String {
            return "AuthenticatedUser($userId)"
        }
    }
}
