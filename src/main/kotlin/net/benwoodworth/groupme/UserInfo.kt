package net.benwoodworth.groupme

import net.benwoodworth.groupme.client.media.GroupMeImage

interface UserInfo : User {
    val name: String
    val avatar: GroupMeImage
}
