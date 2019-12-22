package net.benwoodworth.groupme.client.bot

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.chat.group.GroupSentMessageInfo
import java.net.URI
import java.net.URLDecoder

class Callback internal constructor(
    requestUri: URI,
    val json: JsonObject
) {
    val urlPath: List<String>
    val urlParams: Map<String, String>

    val message: GroupSentMessageInfo = GroupSentMessageInfo(
        json = json,
        likes = emptyList()
    )

    init {
        urlPath = requestUri.path
            .split("/")
            .map { URLDecoder.decode(it, "UTF-8") }
            .let { it.subList(1, it.size) }

        urlParams = requestUri.query
            ?.split("&")
            ?.mapNotNull { param ->
                param
                    .split("=", limit = 1)
                    .takeIf { it.size == 2 }
                    ?.map { URLDecoder.decode(it, "UTF-8") }
                    ?.let { it[0] to it[1] }
            }
            ?.toMap()
            ?: emptyMap()
    }
}
