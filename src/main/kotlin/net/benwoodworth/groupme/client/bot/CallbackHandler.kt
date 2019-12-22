package net.benwoodworth.groupme.client.bot

typealias CallbackHandler = suspend CallbackServerContext.(callback: Callback) -> Unit
