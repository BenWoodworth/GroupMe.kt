package net.benwoodworth.groupme

import net.benwoodworth.groupme.media.GroupMeImage

interface UserInfo : User {
    val name: String
    val avatar: GroupMeImage?
}
