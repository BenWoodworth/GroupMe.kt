package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.media.GroupMeImage

class AuthenticatedUserInfo internal constructor(
    userId: String,
    name: String,
    avatar: GroupMeImage
) : UserInfo(
    userId = userId,
    name = name,
    avatar = avatar
)
