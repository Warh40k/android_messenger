package study.nikita.chat.viewmodel

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import study.nikita.chat.network.NetworkUtils
import study.nikita.chat.network.rest.ApiService
import study.nikita.chat.network.rest.Image
import study.nikita.chat.network.websocket.ChatWebSocket
import study.nikita.chat.network.rest.Message
import study.nikita.chat.network.rest.MessageData
import study.nikita.chat.network.rest.Text
import study.nikita.chat.network.websocket.NewMessageTextData
import study.nikita.chat.network.websocket.WebSocketEvent
import study.nikita.chat.repository.AuthRepository
import study.nikita.chat.repository.ChatRepository
import study.nikita.chat.repository.MessageRepository
import java.io.File
import java.io.FileOutputStream
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

    private var _selectedImage = MutableStateFlow<Uri>(Uri.EMPTY)
    var selectedImage: StateFlow<Uri> get() = _selectedImage.asStateFlow()
        set(value) { _selectedImage.value = value.value }

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

    fun cleanSelected() {
        chatRepository.setSelectedChat("")
    }

    fun setImageUri(uri: Uri) {
        _selectedImage.value = uri
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
        if (incomingMsg.value.isEmpty() || incomingMsg.value.first().from == authRepository.getUsername()) {
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

    fun uploadImage(context: Context) {
        val filePath = getRealPathFromUri(context, selectedImage.value)
        val file = File(filePath)

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val message = Message(
            id = 0,
            from = authRepository.getUsername() ?: "",
            to = selected.value,
            data = MessageData(
                text = null,
                image = Image(link="${authRepository.getUsername()}/${file.name}")
            ),
            time = System.currentTimeMillis() / 1000L
        )
        val gson = Gson()
        val strMsg = gson.toJson(message)
        val image = MultipartBody.Part.createFormData("picture", file.name, requestFile)
        val msg = MultipartBody.Part.createFormData("msg", strMsg)

        viewModelScope.launch {
            try {
                apiService.uploadImage(authRepository.getAuthToken()?:"", image, msg)
                _messages.value = listOf(message) + _messages.value
            } catch (e : Exception) {
                _error.value = "Ошибка при загрузке изображения: ${e.message}"
                println(e.message)
            }
        }
    }

    fun getRealPathFromUri(context: Context, uri: Uri): String {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val displayName: String = cursor?.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)) ?: ""
        cursor?.close()

        val file = File(context.cacheDir, displayName)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file.path
    }
}

