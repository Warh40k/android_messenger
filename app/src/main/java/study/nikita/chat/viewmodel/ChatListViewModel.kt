package study.nikita.chat.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.Config
import study.nikita.chat.network.NetworkUtils
import study.nikita.chat.network.rest.ApiService
import study.nikita.chat.network.rest.Chat
import study.nikita.chat.network.websocket.ChatWebSocket
import study.nikita.chat.network.websocket.WebSocketEvent
import study.nikita.chat.repository.AuthRepository
import study.nikita.chat.repository.ChatRepository
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val apiService : ApiService,
    authRepository: AuthRepository,
    chatWebSocket: ChatWebSocket
) : ViewModel() {
    private var _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> get() = _chatList.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> get() = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    init {
        val token = authRepository.getAuthToken()
        val name = authRepository.getUsername()
        val serverURL = "${Config.WSS_URL}$name?token=$token"
        chatWebSocket.connect(
            serverURL,
            onMessage = {message ->
                parseWebSocketMessage(message)
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

    fun selectChat(chatID : String) {
        repository.setSelectedChat(chatID)
    }

    fun getChatList(context: Context) {
        viewModelScope.launch {
            try {
                val chanList : List<Chat>
                if (NetworkUtils(context).isInternetAvailable()) {
                    val chatNames = apiService.getChannels()
                    chanList = LinkedList<Chat>()
                    var k = 1
                    for (i in chatNames) {
                        val comps = i.split("@").toTypedArray()
                        if (comps.isEmpty()) {
                            k++
                            continue
                        }
                        val name = comps.component1()
                        chanList.add(Chat((k++).toString(), name))
                    }
                    repository.saveChatsToDb(chanList)
                } else {
                    chanList = repository.getChatsFromDb()
                }
                _chatList.value = chanList
            } catch (e : Exception) {
                _error.value = "Произошла ошибка при получении списка чатов: ${e.message}"
                println(e.message)
            }
        }
    }

    private fun parseWebSocketMessage(json: String) {
        val gson = Gson()
        val jsonObject = JsonParser.parseString(json).asJsonObject
        try {
            if (jsonObject.has("NewMessage")) {
                val event = gson.fromJson(json, WebSocketEvent.NewMessage::class.java)
                repository.addIncomingMessage(event.message.msg)
            }
            if (jsonObject.has("NewMessageText")) {
                gson.fromJson(json, WebSocketEvent.NewMessageText::class.java)
            }
        } catch (e : Exception) {
            _error.value = "Произошла ошибка при обработке WebSocket: ${e.message}"
            println(e.message)
        }
    }
}

