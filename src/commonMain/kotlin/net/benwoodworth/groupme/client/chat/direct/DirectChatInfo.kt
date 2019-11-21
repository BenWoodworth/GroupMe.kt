package net.benwoodworth.groupme.client.chat.direct

import net.benwoodworth.groupme.User

class DirectChatInfo internal constructor(
    fromUser: User,
    toUser: User
) : DirectChat(
    fromUser = fromUser,
    toUser = toUser
)
