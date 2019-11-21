package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject

class GroupChatInfo internal constructor(
    val chatJson: JsonObject
): GroupChat(
    chatId = chatJson.getPrimitive("id").content
) {
    val name: String
        get() = chatJson.getPrimitive("name").content

    val messages: Messages = Messages()

    inner class Messages internal constructor() {
        private val messagesJson: JsonObject
            get() = chatJson.getObject("messages")

        val count: Int
            get() = messagesJson.getPrimitive("count").int

        val preview: GroupSentMessageInfo
            get() = GroupSentMessageInfo(messagesJson.getObject("preview"), GroupChat(chatId))
    }

    override fun toString(): String {
        return "GroupChat($name)"
    }
}
