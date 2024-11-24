package study.nikita.chat.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.ApiService
import study.nikita.chat.data.model.LoginRequest

class AuthViewModel : ViewModel() {
    private val apiService = ApiService.create()

    private var _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    fun getAuthToken(name : String, password : String) {
        viewModelScope.launch {
            try {
                _token.value = apiService.login(LoginRequest(name, password))
                println(token)
            } catch (e : Exception) {
                println(e.message)
            }

        }
    }
}