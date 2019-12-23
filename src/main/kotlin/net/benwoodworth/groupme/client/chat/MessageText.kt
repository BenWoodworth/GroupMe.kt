package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.User

// TODO Emoji
data class MessageText(
    val text: String,
    val mentions: List<Mention> = emptyList()
) : CharSequence by text {
    constructor(text: String, vararg mention: User) : this(
        text = text,
        mentions = mention.map { Mention(it, 0, text.length) }
    )

    fun getMentions(atIndex: Int): List<User> {
        return mentions.mapNotNull { mention ->
            mention
                .takeIf { atIndex >= it.start }
                ?.takeIf { atIndex < (it.start + it.length) }
                ?.user
        }
    }

    fun subText(startIndex: Int, endIndex: Int = lastIndex): MessageText {
        return when {
            startIndex == 0 && endIndex == length -> this
            else -> copy(
                text = substring(startIndex, endIndex),
                mentions = mentions
                    .filter { it.start < length && it.end >= 0 }
                    .map { it.copy(start = it.start - startIndex) }
            )
        }
    }

    operator fun plus(other: MessageText): MessageText {
        return when {
            other.isEmpty() -> this
            isEmpty() -> other
            else -> {
                val newText = text + other.text
                val newOtherMentions = other.mentions
                    .map { it.copy(start = length + it.start) }

                MessageText(
                    text = newText,
                    mentions = (mentions + newOtherMentions)
                )
            }
        }
    }

    operator fun plus(other: String): MessageText {
        return when {
            other.isEmpty() -> this
            isEmpty() -> MessageText(other)
            else -> copy(text = text + other)
        }
    }

    operator fun plus(other: Mention): MessageText {
        return copy(mentions = mentions + other)
    }

    operator fun plus(other: Iterable<Mention>): MessageText {
        return when {
            other.none() -> return this
            else -> copy(mentions = (mentions + other))
        }
    }

    operator fun plus(other: Message): MessageText {
        return when (val otherText = other.toMessageText()) {
            null -> this
            else -> this + otherText
        }
    }

    operator fun plus(other: Any?): MessageText {
        return this + other.toString()
    }

    fun toMessage(
        attachments: List<Attachment> = emptyList(),
        sourceGuid: String = Message.generateSourceGuid()
    ): Message {
        val newAttachments = mutableListOf<Attachment>()

        if (mentions.any()) {
            newAttachments += Attachment.Mentions(mentions)
        }

        newAttachments += attachments
        return Message(text, attachments.toList(), sourceGuid)
    }
}

fun String.toMessageText(vararg mention: User): MessageText {
    return MessageText(this, *mention)
}
