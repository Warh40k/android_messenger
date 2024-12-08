package study.nikita.chat.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.data.network.rest.ApiService
import study.nikita.chat.data.network.websocket.ChatWebSocket
import study.nikita.chat.data.model.Message
import study.nikita.chat.data.model.MessageData
import study.nikita.chat.data.model.Text
import study.nikita.chat.data.network.websocket.NewMessageTextData
import study.nikita.chat.data.network.websocket.WebSocketEvent
import study.nikita.chat.data.repository.AuthRepository
import study.nikita.chat.data.repository.ChatRepository
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    var repository: ChatRepository,
    private val chatWebSocket: ChatWebSocket,
    private val authRepository: AuthRepository,
    private val apiService: ApiService
) : ViewModel() {
    private var _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    val selected: StateFlow<String> get() = repository.selectedChat


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

    fun cleanUserInput() {
        _messageInput.value = ""
    }

    fun getMessageList(lastId : Int = 0) {
        if (_isLoading.value) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val messagesList = apiService.getMessages(selected.value, lastKnownId = lastId, reverse = true)
                _messages.value += messagesList
            } catch (e : Exception) {
                println(e.message)
            }
        }
        _isLoading.value = false
    }

    fun sendMessage() {
        val gson = Gson()
        val messageText = WebSocketEvent.NewMessageText(
            NewMessageTextData(
                to = selected.value,
                text = messageInput.value
            )
        )

        val message = Message(
            id = 0,
            from = authRepository.getUsername() ?: "",
            to = selected.value,
            data = MessageData(
                Text(messageInput.value),
                image = null
            ),
            time = 0
        )

        chatWebSocket.sendMessage(gson.toJson(messageText))
        _messages.value = listOf(message) + _messages.value
    }
}

