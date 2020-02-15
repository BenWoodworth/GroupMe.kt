package net.benwoodworth.groupme

import kotlinx.serialization.Serializable

@Serializable
internal class ResponseEnvelope<T : Any>(
    val response: T?,
    val meta: Meta
) {
    @Serializable
    class Meta(
        val code: Int,
        val errors: List<String>? = null
    )

    @Serializable
    class Empty(
        val meta: Meta
    )
}
