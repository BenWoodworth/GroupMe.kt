package net.benwoodworth.groupme

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

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

@UseExperimental(ImplicitReflectionSerializer::class)
internal suspend inline fun <reified T : Any> HttpResponse.toResponseEnvelope(): ResponseEnvelope<T> {
    val serializer = ResponseEnvelope.serializer(serializer<T>())
    return GroupMe.json.parse(serializer, readText())
}
