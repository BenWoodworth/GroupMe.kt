package net.benwoodworth.groupme.api

internal interface HttpClient {
    suspend fun sendRequest(
        method: String,
        url: String,
        headers: Map<String, String?> = emptyMap(),
        params: Map<String, String?> = emptyMap(),
        body: String? = null
    ): Response

    fun urlEncode(string: String): String

    class Response(
        val code: Int,
        val message: String,
        val data: String
    )
}
