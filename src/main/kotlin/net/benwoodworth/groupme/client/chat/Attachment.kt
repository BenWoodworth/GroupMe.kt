package net.benwoodworth.groupme.client.chat

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.json
import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.client.media.GroupMeImage
import net.benwoodworth.groupme.client.media.GroupMeVideo

sealed class Attachment {
    abstract val json: JsonObject

    val type: String by lazy {
        json["type"]!!.primitive.content
    }

    internal class Unknown(
        override val json: JsonObject
    ) : Attachment()

    class Image private constructor(
        override val json: JsonObject,
        val image: GroupMeImage
    ) : Attachment() {
        internal constructor(json: JsonObject) : this(
            json,
            GroupMeImage(json["url"]!!.primitive.content)
        )

        constructor(image: GroupMeImage) : this(
            json {
                "type" to "image"
                "url" to image.imageUrl
            }
        )
    }

    class Video private constructor(
        override val json: JsonObject,
        val video: GroupMeVideo
    ) : Attachment() {
        internal constructor(json: JsonObject) : this(
            json,
            GroupMeVideo(
                json["url"]!!.primitive.content,
                GroupMeImage(json["preview_url"]!!.primitive.content)
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
        override val json: JsonObject,
        val name: String,
        val latitude: Double,
        val longitude: Double
    ) : Attachment() {
        internal constructor(json: JsonObject) : this(
            json,
            json["name"]!!.primitive.content,
            json["lat"]!!.primitive.double,
            json["lng"]!!.primitive.double
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
        override val json: JsonObject,
        val token: String
    ) : Attachment() {
        internal constructor(json: JsonObject) : this(
            json,
            json["token"]!!.primitive.content
        )

        constructor(token: String) : this(
            json {
                "type" to "split"
                "token" to token
            }
        )
    }

    class Emoji private constructor(
        override val json: JsonObject,
        val placeholder: Char,
        val charmap: List<CharMap>
    ) : Attachment() {
        internal constructor(json: JsonObject) : this(
            json,
            json["placeholder"]!!.primitive.content.single(),
            json["charmap"]!!.jsonArray.toCharMap()
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
        override val json: JsonObject,
        val mentions: List<Mention>
    ) : Attachment() {
        internal constructor(json: JsonObject) : this(
            json,
            json.toMentions()
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

fun Attachment(json: JsonObject): Attachment {
    return when (json["type"]!!.primitive.content) {
        "image" -> Attachment.Image(json)
        "video" -> Attachment.Video(json)
        "location" -> Attachment.Location(json)
        "split" -> Attachment.Split(json)
        "emoji" -> Attachment.Emoji(json)
        "mentions" -> Attachment.Mentions(json)
        else -> Attachment.Unknown(json)
    }
}

internal fun JsonObject.toAttachment(): Attachment {
    return Attachment(this)
}

internal fun JsonArray.toAttachmentList(): List<Attachment> {
    return map { it.jsonObject.toAttachment() }
}
