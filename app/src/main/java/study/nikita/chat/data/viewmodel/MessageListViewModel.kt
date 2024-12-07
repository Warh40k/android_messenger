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
    var isLoading = false

    private var _messageInput = MutableStateFlow("")
    var messageInput: StateFlow<String>
        get() = _messageInput.asStateFlow()
        set(value) {
            _messageInput.value = value.value
        }

    fun onTextChanged(text : String) {
        _messageInput.value = text
    }

    fun cleanMessageList() {
        _messages.value = emptyList()
    }

    fun getMessageList(channel : String?, lastId : Int = 0) {
        if (isLoading) {
            return
        }
        isLoading = true
        viewModelScope.launch {
            try {
                val messagesList = apiService.getMessages(channel, lastKnownId = lastId, reverse = true)
                _messages.value += messagesList
            } catch (e : Exception) {
                println(e.message)
            }
        }
        isLoading = false
    }
}

