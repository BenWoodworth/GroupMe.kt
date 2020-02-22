package net.benwoodworth.groupme

import io.ktor.http.HttpStatusCode

internal class HttpStatusCodeClass private constructor(
    private val valueRange: IntRange
) {
    companion object {
        val Information = HttpStatusCodeClass(100..199)
        val Success = HttpStatusCodeClass(200..299)
        val Redirection = HttpStatusCodeClass(300..399)
        val ClientErrors = HttpStatusCodeClass(400..499)
        val ServerErrors = HttpStatusCodeClass(500..599)
    }

    operator fun contains(statusCode: HttpStatusCode): Boolean {
        return statusCode.value in valueRange
    }

    operator fun contains(statusCode: Int): Boolean {
        return statusCode in valueRange
    }
}

internal val HttpStatusCode.Companion.Class: HttpStatusCodeClass.Companion
    get() = HttpStatusCodeClass.Companion