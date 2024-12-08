package study.nikita.chat.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

class ChatRepository {
    private val _selectedChat = MutableStateFlow("")
    val selectedChat: StateFlow<String> = _selectedChat

    fun setSelectedChat(newData: String) {
        _selectedChat.value = newData
    }
}