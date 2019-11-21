package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject

class GroupChatInfo internal constructor(
    val groupJson: JsonObject
): GroupChat(
    chatId = groupJson.getPrimitive("id").content
) {
    val name: String
        get() = groupJson.getPrimitive("name").content

    override fun toString(): String {
        return "GroupChat($name)"
    }
}
