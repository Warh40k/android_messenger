package study.nikita.chat.network.websocket

import com.google.gson.annotations.SerializedName
import study.nikita.chat.network.rest.Message

sealed class WebSocketEvent {
    data class TypingChanged(
        @SerializedName("TypingChanged") val chats: Map<String, List<String>>
    ) : WebSocketEvent()

    data class NewMessage(
        @SerializedName("NewMessage") val message: MessageWrapper
    ) : WebSocketEvent()

    data class StartTyping(
        @SerializedName("StartTyping") val chat: StartTypingData
    ) : WebSocketEvent()

    data object EndTyping : WebSocketEvent()

    data class NewMessageText(
        @SerializedName("NewMessageText") val messageText: NewMessageTextData
    ) : WebSocketEvent()
}

data class MessageWrapper(
    val msg: Message
)

data class StartTypingData(
    val chat: String
)

data class NewMessageTextData(
    val to: String,
    val text: String
)