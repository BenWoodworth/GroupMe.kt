package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject

class GroupChatInfo internal constructor(
    val chatJson: JsonObject
): GroupChat(
    chatId = chatJson.getPrimitive("id").content
) {
    val name: String
        get() = chatJson.getPrimitive("name").content

    override fun toString(): String {
        return "GroupChat($name)"
    }
}
