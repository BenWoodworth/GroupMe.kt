package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.User
import net.benwoodworth.groupme.UserInfo
import java.lang.Integer.max
import java.lang.Integer.min

// TODO Emoji
data class MessageText(
    val text: String,
    val mentions: List<Mention> = emptyList()
) : CharSequence by text {
    companion object {
        /**
         * Concatenates values to create a MessageText instance.
         *
         * `value`:
         * - `is MessageText` -> concatenates MessageText value
         * - `is Message` ->     concatenates `"${value.text}"`, with mentions
         * - `is UserInfo` ->    concatenates `"@${value.nickname}"`, with mention
         * - `is User` ->        concatenates `"@${value.userId}"`, with mention
         * - `else` ->           concatenates `"$value"`
         */
        fun concat(vararg values: Any?): MessageText {
            return concat(values.asIterable())
        }

        /**
         * @see [concat]
         */
        fun concat(values: Iterable<Any?>): MessageText {
            val textBuilder = StringBuilder()
            val mentions = mutableListOf<Mention>()

            fun append(value: Any?) {
                when (value) {
                    is MessageText -> {
                        value.mentions.forEach {
                            val start = max(0, it.start)
                            val end = min(value.text.length, it.end)

                            if (start <= end) {
                                mentions += Mention(it.user, textBuilder.length + start, end - start)
                            }
                        }
                        textBuilder.append(value.text)
                    }
                    is Message -> {
                        val text = value.text
                        if (text != null) {
                            val messageText = MessageText(
                                text = text,
                                mentions = value.attachments
                                    .filterIsInstance<Attachment.Mentions>()
                                    .flatMap { it.mentions }
                            )
                            append(messageText)
                        }
                    }
                    is User -> {
                        val name = when (value) {
                            is UserInfo -> value.nickname
                            else -> value.userId
                        }
                        mentions += Mention(value, textBuilder.length, name.length + 1)
                        textBuilder.append('@', name)
                    }
                    else -> {
                        textBuilder.append(value)
                    }
                }
            }

            values.forEach { append(it) }
            return MessageText(
                text = textBuilder.toString(),
                mentions = mentions.toList()
            )
        }
    }

    fun getMentionsAt(index: Int): List<User> {
        return mentions.mapNotNull { mention ->
            mention
                .takeIf { index >= it.start }
                ?.takeIf { index < (it.start + it.length) }
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

fun Any?.toMessageText(vararg mention: User): MessageText {
    val messageText = MessageText.concat(this)

    return if (mention.isEmpty()) {
        messageText
    } else {
        messageText.copy(
            mentions = messageText.mentions + mention.map { user ->
                Mention(user, 0, messageText.length)
            }
        )
    }
}
