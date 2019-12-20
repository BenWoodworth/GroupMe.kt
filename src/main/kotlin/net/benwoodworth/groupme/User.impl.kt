package net.benwoodworth.groupme

fun User(userId: String): User = UserImpl(userId)

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
