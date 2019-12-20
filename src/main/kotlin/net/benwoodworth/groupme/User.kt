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
