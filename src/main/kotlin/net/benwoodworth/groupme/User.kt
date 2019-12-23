package net.benwoodworth.groupme

interface User {
    val userId: String

    /**
     * Compares [User]s by [userId].
     */
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}

private class UserImpl(
    override val userId: String
) : User {
    override fun equals(other: Any?): Boolean {
        return other is User && userId == other.userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }

    override fun toString(): String {
        return "User($userId)"
    }
}

fun User(
    userId: String
): User = UserImpl(
    userId = userId
)
