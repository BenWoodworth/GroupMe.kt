package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

interface UserInfo : User {
    val nickname: String
    val avatar: GroupMeImage
}
