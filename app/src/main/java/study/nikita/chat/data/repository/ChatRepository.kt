package study.nikita.chat.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatRepository {
    private val _selectedChat = MutableStateFlow("")
    val selectedChat: StateFlow<String> = _selectedChat

    fun updateData(newData: String) {
        _selectedChat.value = newData
    }
}