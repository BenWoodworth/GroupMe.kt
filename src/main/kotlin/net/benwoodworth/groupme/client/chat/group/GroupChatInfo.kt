package net.benwoodworth.groupme.client.chat.group

import kotlinx.serialization.json.JsonObject

class GroupChatInfo internal constructor(
    val json: JsonObject
) : GroupChat by GroupChat(
    chatId = json.getPrimitive("id").content
) {
    val name: String
        get() = json.getPrimitive("name").content

    val messages: Messages = Messages()

    inner class Messages internal constructor() {
        private val json: JsonObject
            get() = this@GroupChatInfo.json.getObject("messages")

        val count: Int
            get() = json.getPrimitive("count").int

//        val preview: GroupSentMessageInfo = json.getObject("preview")
//            .let { GroupSentMessageInfo(GroupChat(chatId), it) }
    }

    override fun toString(): String {
        return "GroupChat($name)"
    }
}
