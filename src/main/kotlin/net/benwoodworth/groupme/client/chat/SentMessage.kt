package net.benwoodworth.groupme.client.chat

abstract class SentMessage internal constructor() {
    abstract val messageId: String
}
