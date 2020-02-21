package net.benwoodworth.groupme

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.header

object HttpClientFactory {
    fun create(groupMeApiToken: String?): HttpClient {
        return HttpClient(Apache) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(GroupMe.json)
            }

            defaultRequest {
                header("X-Access-Token", groupMeApiToken)
            }
        }
    }
}