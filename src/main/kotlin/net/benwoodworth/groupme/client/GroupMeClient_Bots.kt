package net.benwoodworth.groupme.client

import kotlinx.coroutines.flow.Flow
import net.benwoodworth.groupme.client.bot.Bot
import net.benwoodworth.groupme.client.bot.BotInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

interface GroupMeClient_Bots {
    fun getBots(): Flow<BotInfo>


    suspend fun createBot(
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
    ): BotInfo


    suspend fun deleteBot(bot: Bot)

    suspend fun Bot.delete() = deleteBot(this)


    suspend fun getBotInfo(bot: Bot): BotInfo

    suspend fun Bot.getInfo() = getBotInfo(this)


    suspend fun setBotInfo(
        bot: Bot,
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
    ): BotInfo

    suspend fun Bot.setInfo(
        name: String? = null,
        avatar: GroupMeImage? = null,
        callbackUrl: String? = null
    ) = setBotInfo(this, name, avatar, callbackUrl)
}