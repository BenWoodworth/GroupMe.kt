package net.benwoodworth.groupme.api

internal interface HttpClient {
    suspend fun sendRequest(
        method: String,
        url: String,
        headers: Map<String, String?> = emptyMap(),
        params: Map<String, String?> = emptyMap(),
        body: String? = null
    ): HttpResponse

    fun urlEncode(string: String): String
}