package study.nikita.chat.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.ApiService
import study.nikita.chat.data.model.Chat
import java.util.LinkedList

class MessageListViewModel : ViewModel() {
    private var apiService : ApiService = ApiService.create()
    private var _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> get() = _chatList.asStateFlow()

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
                    var name = comps.component1()
                    chanList.add(Chat((k++).toString(), name))
                }
                _chatList.value = chanList
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}

