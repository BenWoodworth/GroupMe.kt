package net.benwoodworth.groupme.api

import kotlinx.serialization.Serializable

@Serializable
internal class ResponseEnvelope<T : Any>(
    val response: T?,
    val meta: Meta
) {
    @Serializable
    class Meta(
        val code: Int,
        val errors: List<String>
    )

    @Serializable
    class Empty(
        val meta: Meta
    )
}
