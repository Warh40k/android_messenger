package study.nikita.chat.data.viewmodel

import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import study.nikita.chat.data.api.ApiService
import study.nikita.chat.data.api.ApiService.Companion.BASE_URL

class ChatViewModel : ViewModel() {
    private var apiService : ApiService;

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }
}