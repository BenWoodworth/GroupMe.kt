package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json

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

private class MessageImpl(
    override val json: JsonObject,
    override val text: String?,
    override val attachments: List<Attachment>,
    override val sourceGuid: String
) : Message
