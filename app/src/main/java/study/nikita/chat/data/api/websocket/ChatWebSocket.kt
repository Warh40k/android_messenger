package study.nikita.chat.data.api.websocket

import okhttp3.*
import okio.ByteString

class ChatWebSocket {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect(
        serverURL : String,
        onOpen: () -> Unit,
        onMessage: (String) -> Unit,
        onError: (String) -> Unit,
        onClosing: () -> Unit
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

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                onClosing()
            }
        })
    }

    fun sendMessage(message: String) {
        val result = webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closed the connection")
    }
}