package study.nikita.chat.data.api

import okhttp3.*
import okio.ByteString

class ChatWebSocket(private val serverURL : String) {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun connect(
        onOpen: () -> Unit,
        onMessage: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url(serverURL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                onOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                onMessage(bytes.utf8())
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError(t.message ?: "Unknown error")
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket.send(message)
    }

    fun disconnect() {
        webSocket.close(1000, "Client closed the connection")
    }
}