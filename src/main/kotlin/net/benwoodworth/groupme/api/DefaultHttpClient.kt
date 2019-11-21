package net.benwoodworth.groupme.api

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class DefaultHttpClient : HttpClient {
    override suspend fun sendRequest(
        method: HttpMethod,
        url: String,
        headers: Map<String, String?>,
        params: Map<String, String?>,
        body: String?
    ): HttpResponse {
        return suspendCoroutine { continuation ->
            val paramsUrl = params
                .filterValues { it != null }
                .takeIf { it.any() }
                ?.map { (key, value) -> "${urlEncode(key)}=${urlEncode(value!!)}" }
                ?.joinToString("&")
                ?.let { "$url?$it" }
                ?: url

            val connection = URL(paramsUrl).openConnection() as HttpURLConnection

            connection.requestMethod = method.method
            connection.doInput = true

            for ((key, value) in headers) {
                value?.let { connection.addRequestProperty(key, value) }
            }

            body?.let {
                connection.doOutput = true
                connection.outputStream.use { outputStream ->
                    outputStream.write(body.toByteArray(Charsets.UTF_8))
                }
            }

            val responseData = try {
                connection.inputStream.use { it.readBytes() }
            } catch (exception: IOException) {
                null
            }

            continuation.resume(
                HttpResponse(
                    code = connection.responseCode,
                    message = connection.responseMessage,
                    data = responseData?.toString(Charsets.UTF_8) ?: ""
                )
            )
        }
    }

    override fun urlEncode(string: String): String {
        return URLEncoder.encode(string, Charsets.UTF_8.name())
    }
}
