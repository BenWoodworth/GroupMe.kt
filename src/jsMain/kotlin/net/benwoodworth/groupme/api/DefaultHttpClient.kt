package net.benwoodworth.groupme.api

import org.w3c.xhr.XMLHttpRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal actual class DefaultHttpClient actual constructor() : HttpClient {
    override suspend fun sendRequest(
        method: HttpMethod,
        url: String,
        headers: Map<String, String?>,
        params: Map<String, String?>,
        body: String?
    ): HttpResponse {
        return suspendCoroutine { continuation ->
            val request = XMLHttpRequest()

            request.onload = {
                continuation.resume(
                    HttpResponse(
                        request.status.toInt(),
                        request.statusText,
                        request.responseText
                    )
                )
            }

            headers.forEach { (key, value) ->
                value?.let { request.setRequestHeader(key, value) }
            }

            request.send()
        }
    }

    override fun urlEncode(string: String): String {
        return encodeURIComponent(string)
    }
}

private external fun encodeURIComponent(str: String): String
