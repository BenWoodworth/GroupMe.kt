package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject

interface ChatInfo {
    val json: JsonObject
    val messageCount: Int
}
