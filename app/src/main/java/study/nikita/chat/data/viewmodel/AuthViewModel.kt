package study.nikita.chat.data.viewmodel

import study.nikita.chat.data.repository.AuthRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import study.nikita.chat.data.network.rest.ApiService
import study.nikita.chat.data.model.LoginRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apiService: ApiService,
) : ViewModel() {

    private var _token = MutableLiveData<String?>()

    fun getAuthToken(name : String, password : String) {
        viewModelScope.launch {
            try {
                val gson = Gson()
                _token.value = apiService.login(gson.toJson(LoginRequest(name, password)))
                authRepository.saveAuthToken(_token.value.toString())
                authRepository.saveUsername(name)
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}