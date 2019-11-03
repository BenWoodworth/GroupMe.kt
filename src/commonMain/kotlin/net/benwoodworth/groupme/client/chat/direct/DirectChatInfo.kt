package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.chat.ChatInfo

class DirectChatInfo internal constructor(
    fromUser: User,
    toUser: User
) : DirectChat by DirectChat(fromUser, toUser), ChatInfo
