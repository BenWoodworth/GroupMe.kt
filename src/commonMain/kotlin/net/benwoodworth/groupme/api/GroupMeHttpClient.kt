package net.benwoodworth.groupme.api

internal class GroupMeHttpClient(
    private val httpClient: HttpClient,
    apiKey: String,
    private val apiV3UrlBase: String
) : HttpClient by httpClient {
    private val accessTokenHeader = "X-ACCESS-TOKEN" to apiKey

    override suspend fun sendRequest(
        method: String,
        url: String,
        headers: Map<String, String?>,
        params: Map<String, String?>,
        body: String?
    ): HttpClient.Response {
        return httpClient.sendRequest(
            method = method,
            url = url,
            headers = headers + accessTokenHeader,
            params = params,
            body = body
        )
    }

    suspend fun sendApiV3Request(
        method: String,
        endpoint: String,
        headers: Map<String, String?> = emptyMap(),
        params: Map<String, String?> = emptyMap(),
        body: String? = null
    ): HttpClient.Response {
        return sendRequest(
            method = method,
            url = "$apiV3UrlBase$endpoint",
            headers = headers,
            params = params,
            body = body
        )
    }
}
