package study.nikita.chat.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.rest.ApiService

class RegisterViewModel(
    private val name : String,
) : ViewModel() {

    private val apiService = ApiService.create()

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private fun register() {
        try {
            viewModelScope.launch {
                val response = apiService.register(name)
                _password.value = response.split(' ')[1]
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}