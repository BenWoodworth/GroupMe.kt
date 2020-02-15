package net.benwoodworth.groupme.client.bot

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress

internal class CallbackServer(
    val port: Int,
    private val json: Json,
    private val callbackHandler: CallbackHandler
) {
    private val context = CallbackServerContextImpl(this)

    private lateinit var server: HttpServer

    fun start() {
        server = createServer()
        server.start()
    }

    fun stop() {
        server.stop(0)
    }

    private fun createServer(): HttpServer {
        return HttpServer.create(InetSocketAddress(port), 0).apply {
            executor = null
            createContext("/") {
                runBlocking { handleRequest(it) }
            }
        }
    }

    private fun handleRequest(exchange: HttpExchange) {
        if (exchange.requestMethod != "POST") {
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
            return
        }

        val callback = try {
            val body = exchange.requestBody.bufferedReader().readText()
            val bodyJson = json.parseJson(body).jsonObject

            Callback(
                requestUri = exchange.requestURI,
                json = bodyJson
            )
        } catch (e: Exception) {
            exchange.sendResponseHeaders(400, 0L)
            exchange.responseBody.close()
            e.printStackTrace()
            return
        }

        exchange.sendResponseHeaders(200, 0L)
        exchange.responseBody.close()

        runBlocking {
            try {
                context.run { callbackHandler(callback) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
