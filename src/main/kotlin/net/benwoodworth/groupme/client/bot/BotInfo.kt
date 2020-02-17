package net.benwoodworth.groupme.client.bot

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.chat.Chat
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.toGroupMeImage

interface BotInfo : Bot {
    val json: JsonObject
    val name: String
    val chat: Chat
    val avatar: GroupMeImage?
    val callbackUrl: String?
}

class BotInfoImpl internal constructor(
    override val json: JsonObject,
    botId: String,
    override val name: String,
    override val chat: Chat,
    override val avatar: GroupMeImage?,
    override val callbackUrl: String?
) : BotInfo, Bot by Bot(botId) {
    override fun toString(): String {
        return "Bot($name)"
    }
}

internal fun BotInfo(
    json: JsonObject,
    botId: String = json.getPrimitive("bot_id").content,
    name: String = json.getPrimitive("name").content,
    chat: Chat = Chat(json.getPrimitive("group_id").content),
    avatar: GroupMeImage? = json.getPrimitive("avatar_url").contentOrNull?.toGroupMeImage(),
    callbackUrl: String? = json.getPrimitive("callback_url").content
): BotInfo = BotInfoImpl(
    json = json,
    botId = botId,
    name = name,
    chat = chat,
    avatar = avatar,
    callbackUrl = callbackUrl
)
