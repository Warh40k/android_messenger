package study.nikita.chat.data.api.websocket

/*data class WebSocketMessage(
    val NewMessage : Pair<String, Message>,
    val TypingChanged : HashMap<String, List<String>>,
    val StartTyping : Pair<String, String>,
    val EndTyping : List<String>,
    val NewMessageText: HashMap<String, String>
)*/

enum class MessageType() {
    NewMessage,
    TypingChanged,
    StartTyping,
    EndTyping,
    NewMessageText
}