package study.nikita.chat.data.viewmodel

import AuthManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import study.nikita.chat.data.api.ApiService
import study.nikita.chat.data.api.ApiService.Companion.BASE_URL
import study.nikita.chat.data.model.LoginRequest

class AuthViewModel : ViewModel() {
    private var apiService : ApiService;

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    private var _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    fun getAuthToken(name : String, password : String, authManager : AuthManager) {
        viewModelScope.launch {
            try {
                val gson = Gson()
                _token.value = apiService.login(gson.toJson(LoginRequest(name, password)))
                authManager.saveAuthToken(token.toString())
                println(token)
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}