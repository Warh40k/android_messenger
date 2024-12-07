package study.nikita.chat.data.viewmodel

import study.nikita.chat.data.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.ApiService
import study.nikita.chat.data.api.ChatWebSocket
import study.nikita.chat.data.model.Message
import study.nikita.chat.data.repository.ChatRepository
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(var repository: ChatRepository, var authRepository: AuthRepository) : ViewModel() {
    private var apiService : ApiService = ApiService.create()
    private var chatWebSocket : ChatWebSocket? = null

    private var _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    var selected: StateFlow<String>
        get() = repository.selectedChat
        set(value) {
            onChannelSelected(authRepository)
            repository.setSelectedChat(value.value)
        }

    private val serverUrlBase = "wss://faerytea.name:8008/ws/"

    private var _messageInput = MutableStateFlow("")
    var messageInput: StateFlow<String>
        get() = _messageInput.asStateFlow()
        set(value) {
            _messageInput.value = value.value
        }

    fun onChannelSelected(authRepository: AuthRepository) {
        chatWebSocket?.disconnect()
        val token = authRepository.getAuthToken()
        val serverUrl = "$serverUrlBase$selected?token=$token"
        chatWebSocket = ChatWebSocket(serverUrl)
    }

    fun onTextChanged(text : String) {
        _messageInput.value = text
    }

    fun cleanMessageList() {
        _messages.value = emptyList()
    }

    fun getMessageList(channel : String?, lastId : Int = 0) {
        if (_isLoading.value) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val messagesList = apiService.getMessages(channel, lastKnownId = lastId, reverse = true)
                _messages.value += messagesList
            } catch (e : Exception) {
                println(e.message)
            }
        }
        _isLoading.value = false
    }
}

