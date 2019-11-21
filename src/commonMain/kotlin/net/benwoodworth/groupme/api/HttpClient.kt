package net.benwoodworth.groupme.api

interface HttpClient {
    suspend fun sendRequest(
        method: HttpMethod,
        url: String,
        headers: Map<String, String?> = emptyMap(),
        params: Map<String, String?> = emptyMap(),
        body: String? = null
    ): HttpResponse

    fun urlEncode(string: String): String
}
