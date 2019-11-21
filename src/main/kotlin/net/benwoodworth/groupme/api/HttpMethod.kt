package net.benwoodworth.groupme.api

class HttpMethod(val method: String) {
    companion object {
        val Get = HttpMethod("GET")
        val Post = HttpMethod("POST")
        val Delete = HttpMethod("DELETE")
    }
}
