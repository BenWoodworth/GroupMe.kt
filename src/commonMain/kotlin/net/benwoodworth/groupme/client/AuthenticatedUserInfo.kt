package net.benwoodworth.groupme.client

import net.benwoodworth.groupme.UserInfo
import net.benwoodworth.groupme.client.AuthenticatedUser
import net.benwoodworth.groupme.media.GroupMeImage

class AuthenticatedUserInfo internal constructor(
    userId: String,
    override val name: String,
    override val avatar: GroupMeImage?
) : AuthenticatedUser by AuthenticatedUser(userId), UserInfo
