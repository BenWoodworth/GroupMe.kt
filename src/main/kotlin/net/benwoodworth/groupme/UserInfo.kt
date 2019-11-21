package net.benwoodworth.groupme

import kotlinx.serialization.json.JsonObject
import net.benwoodworth.groupme.client.media.GroupMeImage

open class UserInfo internal constructor(
    val userJson: JsonObject
) : User(
    userId = userJson.getPrimitive("id").content
) {
    val name: String
        get() = userJson.getPrimitive("name").content

    val avatar: GroupMeImage?
        get() = userJson.getPrimitive("avatar_url").contentOrNull?.let { GroupMeImage(it) }

    override fun toString(): String {
        return "User($name)"
    }
}
