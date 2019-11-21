package net.benwoodworth.groupme.client.bot

import net.benwoodworth.groupme.client.chat.group.GroupChat
import net.benwoodworth.groupme.client.media.GroupMeImage

class BotInfo internal constructor(
    botId: String,
    val name: String,
    val group: GroupChat,
    val avatar: GroupMeImage,
    val callbackUrl: String
) : Bot(botId)
