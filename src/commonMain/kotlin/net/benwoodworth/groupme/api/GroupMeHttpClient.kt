package net.benwoodworth.groupme.api

internal class GroupMeHttpClient(
    private val httpClient: HttpClient,
    private val apiKey: String
) : HttpClient by httpClient {
    private val accessTokenHeader = "X-ACCESS-TOKEN" to apiKey

    override suspend fun sendRequest(
        method: String,
        url: String,
        headers: Map<String, String?>,
        params: Map<String, String?>,
        body: String?
    ): HttpResponse {
        return httpClient.sendRequest(
            method = method,
            url = url,
            headers = headers + accessTokenHeader,
            params = params,
            body = body
        )
    }
}
