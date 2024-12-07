package study.nikita.chat.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.rest.ApiService
import study.nikita.chat.data.model.Chat
import study.nikita.chat.data.repository.ChatRepository
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(private val repository: ChatRepository) : ViewModel() {
    private var apiService : ApiService = ApiService.create()
    private var _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> get() = _chatList.asStateFlow()

    fun selectChat(chatID : String) {
        repository.setSelectedChat(chatID)
    }

    fun getChatList() {
        viewModelScope.launch {
            try {
                val chatNames = apiService.getChannels()
                val chanList = LinkedList<Chat>()
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
                _chatList.value = chanList
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}

