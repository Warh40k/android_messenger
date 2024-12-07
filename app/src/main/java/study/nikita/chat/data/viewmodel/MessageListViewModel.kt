package study.nikita.chat.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.ApiService
import study.nikita.chat.data.model.Message
import study.nikita.chat.data.repository.ChatRepository
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(private val repository: ChatRepository) : ViewModel() {
    private var apiService : ApiService = ApiService.create()
    private var _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    val selected: StateFlow<String> = repository.selectedChat

    fun getMessageList(channel : String?) {
        viewModelScope.launch {
            try {
                val messagesList = apiService.getMessages(channel)
                _messages.value = messagesList
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }

    fun selectChat(chatID : String) {
        repository.updateData(chatID)
    }
}

