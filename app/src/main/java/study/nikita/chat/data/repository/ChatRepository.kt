package study.nikita.chat.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import study.nikita.chat.data.model.Message

class ChatRepository {
    private val _selectedChat = MutableStateFlow("")
    val selectedChat: StateFlow<String> get() = _selectedChat.asStateFlow()

    private var _newMessages = MutableStateFlow<List<Message>>(mutableListOf())
    val newMessages: StateFlow<List<Message>> get() = _newMessages.asStateFlow()

    fun setSelectedChat(newData: String) {
        _selectedChat.value = newData
    }

    fun addIncomingMessage(msg : Message) {
        _newMessages.value += msg
    }

    fun popIncomingMessage() : Message {
        val message = _newMessages.value.first()
        _newMessages.value = _newMessages.value.drop(1)
        return message
    }
}