package net.benwoodworth.groupme

open class User(
    val userId: String
) {
    /**
     * Compares [User]s by [userId].
     */
    final override fun equals(other: Any?): Boolean {
        return other is User && userId == other.userId
    }

    final override fun hashCode(): Int {
        return userId.hashCode()
    }

    override fun toString(): String {
        return "User($userId)"
    }
}
