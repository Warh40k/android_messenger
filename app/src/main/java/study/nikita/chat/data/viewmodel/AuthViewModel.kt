package study.nikita.chat.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import study.nikita.chat.data.api.ApiService
import study.nikita.chat.data.model.LoginRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val name : String,
    private val password : String) : ViewModel() {

    private val apiService = ApiService.create()

    private lateinit var token : String

    private fun token() {
        viewModelScope.launch {
            try {
                token = apiService.login(LoginRequest(name, password))

            } catch (e: Exception) {

            }
        }
    }
}