package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import kotlin.random.Random
import kotlin.random.nextULong

class Message private constructor(
    val json: JsonObject,
    val text: String?,
    val attachments: List<Attachment>,
    val sourceGuid: String
) {
    companion object {
        fun generateSourceGuid(client: String = "GroupMe.kt"): String {
            @Suppress("EXPERIMENTAL_API_USAGE")
            val rand = Random.nextULong()
                .toString(16)
                .padStart(8, '0')

            return "$client-$rand"
        }
    }

    constructor(json: JsonObject) : this(
        json,
        json["text"]?.primitive?.contentOrNull,
        json["attachments"]!!.jsonArray.map { Attachment(it.jsonObject) },
        json["source_guid"]!!.primitive.content
    )

    constructor(
        text: String? = null,
        attachments: List<Attachment> = emptyList(),
        sourceGuid: String = generateSourceGuid()
    ) : this(
        json {
            "text" to text
            "attachments" to JsonArray(attachments.map { it.json })
            "source_guid" to sourceGuid
        }
    )
}
