package study.nikita.chat.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.network.NetworkUtils
import study.nikita.chat.network.rest.ApiService
import study.nikita.chat.network.websocket.ChatWebSocket
import study.nikita.chat.network.rest.Message
import study.nikita.chat.network.rest.MessageData
import study.nikita.chat.network.rest.Text
import study.nikita.chat.network.websocket.NewMessageTextData
import study.nikita.chat.network.websocket.WebSocketEvent
import study.nikita.chat.repository.AuthRepository
import study.nikita.chat.repository.ChatRepository
import study.nikita.chat.repository.MessageRepository
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private var chatRepository: ChatRepository,
    private var messageRepository: MessageRepository,
    private val chatWebSocket: ChatWebSocket,
    private val authRepository: AuthRepository,
    private val apiService: ApiService
) : ViewModel() {
    private var _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    val selected: StateFlow<String> get() = chatRepository.selectedChat
    val incomingMsg: StateFlow<List<Message>> get() = chatRepository.newMessages

    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> get() = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

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

    fun getMessageList(context: Context, lastId : Int = 0) {
        if (_isLoading.value) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val messagesList : List<Message>
                if (NetworkUtils(context).isInternetAvailable()) {
                    messagesList = apiService.getMessages(selected.value, lastKnownId = lastId, reverse = true)
                    messageRepository.insertAllMessages(messagesList)
                } else {
                    messagesList = messageRepository.getChatMessages(selected.value)
                }
                _messages.value += messagesList
            } catch (e : Exception) {
                _error.value = "Ошибка при получении списка сообщений: ${e.message}"
                println(e.message)
            }
        }
        _isLoading.value = false
    }

    fun receiveNewMessage() {
        if (incomingMsg.value.isEmpty()) {
            return
        }
        val message = chatRepository.popIncomingMessage()

        if (selected.value != message.to) {
            return
        }
        _messages.value = listOf(message) + _messages.value
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
                text = Text(text = messageInput.value),
                image = null
            ),
            time = LocalTime.now().second.toLong()
        )

        chatWebSocket.sendMessage(gson.toJson(messageText))
        _messages.value = listOf(message) + _messages.value
    }
}

