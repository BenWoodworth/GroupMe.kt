package net.benwoodworth.groupme.client.chat

import net.benwoodworth.groupme.GroupMeDsl

interface ChatClient {
    val chat: Chat

    suspend operator fun invoke(@GroupMeDsl block: suspend ChatClient.() -> Unit): ChatClient
}
