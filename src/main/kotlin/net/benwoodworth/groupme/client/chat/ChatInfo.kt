package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.media.GroupMeImage

interface ChatInfo {
    val json: JsonObject
    val messageCount: Int
    val image: GroupMeImage
    val name: String
}
