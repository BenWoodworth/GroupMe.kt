package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.User

interface AuthenticatedUser : User

internal class AuthenticatedUserImpl(
    userId: String
) : AuthenticatedUser, User by User(userId)

internal fun AuthenticatedUser(userId: String): AuthenticatedUser {
    return AuthenticatedUserImpl(userId)
}
