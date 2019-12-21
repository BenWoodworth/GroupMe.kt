package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
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
