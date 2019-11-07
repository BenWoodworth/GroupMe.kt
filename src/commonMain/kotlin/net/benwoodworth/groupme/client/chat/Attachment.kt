package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.*
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.GroupMeVideo

sealed class Attachment {
    abstract val attachmentJson: JsonObject

    val type: String by lazy {
        attachmentJson["type"]!!.primitive.content
    }

    internal class Unknown(
        override val attachmentJson: JsonObject
    ) : Attachment()

    class Image private constructor(
        override val attachmentJson: JsonObject,
        val image: GroupMeImage
    ) : Attachment() {
        internal constructor(attachmentJson: JsonObject) : this(
            attachmentJson,
            GroupMeImage(attachmentJson["url"]!!.primitive.content)
        )

        constructor(image: GroupMeImage) : this(
            json {
                "type" to "image"
                "url" to image.imageUrl
            }
        )
    }

    class Video private constructor(
        override val attachmentJson: JsonObject,
        val video: GroupMeVideo
    ) : Attachment() {
        internal constructor(attachmentJson: JsonObject) : this(
            attachmentJson,
            GroupMeVideo(
                attachmentJson["url"]!!.primitive.content,
                GroupMeImage(attachmentJson["preview_url"]!!.primitive.content)
            )
        )

        constructor(video: GroupMeVideo) : this(
            json {
                "type" to "video"
                "url" to video.videoUrl
                "preview_url" to video.preview.imageUrl
            }
        )
    }

    class Location private constructor(
        override val attachmentJson: JsonObject,
        val name: String,
        val latitude: Double,
        val longitude: Double
    ) : Attachment() {
        internal constructor(attachmentJson: JsonObject) : this(
            attachmentJson,
            attachmentJson["name"]!!.primitive.content,
            attachmentJson["lat"]!!.primitive.double,
            attachmentJson["lng"]!!.primitive.double
        )

        constructor(name: String, latitude: Double, longitude: Double) : this(
            json {
                "type" to "location"
                "name" to name
                "lat" to latitude
                "lng" to longitude
            }
        )
    }

    @Deprecated("Deprecated in the GroupMe API.")
    class Split private constructor(
        override val attachmentJson: JsonObject,
        val token: String
    ) : Attachment() {
        internal constructor(attachmentJson: JsonObject) : this(
            attachmentJson,
            attachmentJson["token"]!!.primitive.content
        )

        constructor(token: String) : this(
            json {
                "type" to "split"
                "token" to token
            }
        )
    }

    class Emoji private constructor(
        override val attachmentJson: JsonObject,
        val placeholder: Char,
        val charmap: List<CharMap>
    ) : Attachment() {
        internal constructor(attachmentJson: JsonObject) : this(
            attachmentJson,
            attachmentJson["placeholder"]!!.primitive.content.single(),
            attachmentJson["charmap"]!!.jsonArray.toCharMap()
        )

        constructor(charmap: List<CharMap>) : this(
            json {
                "type" to "emoji"
                "charmap" to charmap.map { (packId, offset) ->
                    listOf(packId, offset)
                }
            }
        )

        private companion object {
            fun JsonArray.toCharMap(): List<CharMap> {
                return map { mapping ->
                    mapping.jsonArray.let { array ->
                        CharMap(
                            packId = array[0].primitive.int,
                            offset = array[1].primitive.int
                        )
                    }
                }
            }
        }
    }

    class Mentions private constructor(
        override val attachmentJson: JsonObject,
        val mentions: List<Mention>
    ) : Attachment() {
        internal constructor(attachmentJson: JsonObject) : this(
            attachmentJson,
            attachmentJson.toMentions()
        )

        constructor(mentions: List<Mention>) : this(
            json {
                "type" to "emoji"
                "user_ids" to mentions.map { it.user.userId }
                "loci" to mentions.map { listOf(it.location, it.length) }
            }
        )

        private companion object {
            fun JsonObject.toMentions(): List<Mention> {
                val userIds = this["user_ids"]!!.jsonArray
                val loci = this["loci"]!!.jsonArray

                return List(userIds.size) { i ->
                    val loc = loci[i].jsonArray
                    Mention(
                        user = User(userIds[i].primitive.content),
                        location = loc[0].int,
                        length = loc[1].int
                    )
                }
            }
        }
    }
}

fun Attachment(attachmentJson: JsonObject): Attachment {
    return when (attachmentJson["type"]!!.primitive.content) {
        "image" -> Attachment.Image(attachmentJson)
        "video" -> Attachment.Video(attachmentJson)
        "location" -> Attachment.Location(attachmentJson)
        "split" -> Attachment.Split(attachmentJson)
        "emoji" -> Attachment.Emoji(attachmentJson)
        "mentions" -> Attachment.Mentions(attachmentJson)
        else -> Attachment.Unknown(attachmentJson)
    }
}
