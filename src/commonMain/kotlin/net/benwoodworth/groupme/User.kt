package net.benwoodworth.groupme

interface User {
    val userId: String

    /**
     * Compares [User]s by [userId].
     */
    override fun equals(other: Any?): Boolean
}

fun User(userId: String): User {
    return object : User {
        override val userId: String = userId

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
}
