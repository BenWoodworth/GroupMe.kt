package net.benwoodworth.groupme.client.chat.group

import net.benwoodworth.groupme.client.chat.ChatInfo

class GroupChatInfo internal constructor(
    chatId: String
): GroupChat by GroupChat(chatId), ChatInfo
