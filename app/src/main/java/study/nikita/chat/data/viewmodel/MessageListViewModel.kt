package study.nikita.chat.data.viewmodel

import study.nikita.chat.data.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.rest.ApiService
import study.nikita.chat.data.api.websocket.ChatWebSocket
import study.nikita.chat.data.api.websocket.MessageText
import study.nikita.chat.data.api.websocket.MessageType
import study.nikita.chat.data.model.Message
import study.nikita.chat.data.model.MessageData
import study.nikita.chat.data.model.Text
import study.nikita.chat.data.repository.ChatRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(var repository: ChatRepository, var authRepository: AuthRepository) : ViewModel() {
    private var apiService : ApiService = ApiService.create()
    private var chatWebSocket : ChatWebSocket = ChatWebSocket()
    private val gson = Gson()

    private var _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus

    val selected: StateFlow<String> get() = repository.selectedChat

    private val serverUrlBase = "wss://faerytea.name:8008/ws/"

    private var _messageInput = MutableStateFlow("")
    var messageInput: StateFlow<String>
        get() = _messageInput.asStateFlow()
        set(value) {
            _messageInput.value = value.value
        }

    fun onChannelSelected() {
        chatWebSocket.disconnect()
        val token = authRepository.getAuthToken()
        val serverURL = "${serverUrlBase}kologriviy?token=$token"
        chatWebSocket.connect(
            serverURL,
            onMessage = {message ->
                parseMessage(message)
            },
            onOpen = {
                println("websocket open: $serverURL")
            },
            onError = {error ->
                println("error: $error")
            },
            onClosing = {
                println("connection closing")
            }
        )
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

    fun parseMessage(msg : String) {
        val typeMap = object: TypeToken<List<Pair<String, Any>>>() {}.type
        val wsMessage : List<Pair<String,Any>> = gson.fromJson(msg, typeMap)
        if (wsMessage.isEmpty()) {
            return
        }
        val first = wsMessage.first()
        when(first.first) {
            MessageType.NewMessage.name -> {
                val message = first.second as? Pair<String, Message> ?: return
                _messages.value += message.second
            }
        }
    }

    fun sendMessage() {
        val map : HashMap<String, String> = hashMapOf(
            "to" to selected.value,
            "text" to messageInput.value
        )
        var message : HashMap<String, HashMap<String,String>> = hashMapOf("NewMessageText" to map)
        chatWebSocket.sendMessage(gson.toJson(message))
    }
}

