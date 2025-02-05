package study.nikita.chat.viewmodel

import study.nikita.chat.repository.AuthRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import study.nikita.chat.network.rest.ApiService
import study.nikita.chat.network.rest.LoginRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apiService: ApiService,
) : ViewModel() {

    private var _token = MutableStateFlow<String?>(null)
    val token : StateFlow<String?> get() = _token.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> get() = _error.asStateFlow()

    fun getAuthToken(name : String, password : String) {
        viewModelScope.launch {
            try {
                val gson = Gson()
                _token.value = apiService.login(gson.toJson(LoginRequest(name, password)))
                authRepository.saveAuthToken(_token.value.toString())
                authRepository.saveUsername(name)
            } catch (e : Exception) {
                _error.value = "Произошла ошибка при авторизации: ${e.message}"
                println(e.message)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}