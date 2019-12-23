package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import kotlin.random.Random
import kotlin.random.nextULong

interface Message {
    val json: JsonObject
    val text: String?
    val attachments: List<Attachment>
    val sourceGuid: String

    companion object {
        fun generateSourceGuid(client: String = "GroupMe.kt"): String {
            @Suppress("EXPERIMENTAL_API_USAGE")
            val rand = Random.nextULong()
                .toString(16)
                .padStart(8, '0')

            return "$client-$rand"
        }
    }
}

private class MessageImpl(
    override val json: JsonObject,
    override val text: String?,
    override val attachments: List<Attachment>,
    override val sourceGuid: String
) : Message

fun Message(json: JsonObject): Message = MessageImpl(
    json = json,
    text = json.getPrimitive("text").contentOrNull,
    attachments = json.getArray("attachments").map { Attachment(it.jsonObject) },
    sourceGuid = json.getPrimitive("source_guid").content
)

fun Message(
    text: String? = null,
    attachments: List<Attachment> = emptyList(),
    sourceGuid: String = Message.generateSourceGuid()
): Message = MessageImpl(
    json = json {
        "text" to text
        "attachments" to JsonArray(attachments.map { it.json })
        "source_guid" to sourceGuid
    },
    text = text,
    attachments = attachments,
    sourceGuid = sourceGuid
)
