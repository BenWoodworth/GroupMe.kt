package net.benwoodworth.groupme

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
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
internal suspend inline fun <reified T : Any> HttpClient.getEnveloped(
    block: HttpRequestBuilder.() -> Unit
): ResponseEnvelope<T> {
    val response = this.get<HttpResponse> { block() }
    val serializer = ResponseEnvelope.serializer(serializer<T>())
    return GroupMe.json.parse(serializer, response.readText())
}

@UseExperimental(ImplicitReflectionSerializer::class)
internal suspend inline fun <reified T : Any> HttpClient.postEnveloped(
    block: HttpRequestBuilder.() -> Unit
): ResponseEnvelope<T> {
    val response = this.post<HttpResponse> { block() }
    val serializer = ResponseEnvelope.serializer(serializer<T>())
    return GroupMe.json.parse(serializer, response.readText())
}

@UseExperimental(ImplicitReflectionSerializer::class)
internal suspend inline fun <reified T : Any> HttpResponse.toResponseEnvelope(): ResponseEnvelope<T> {
    val serializer = ResponseEnvelope.serializer(serializer<T>())
    return GroupMe.json.parse(serializer, readText())
}
