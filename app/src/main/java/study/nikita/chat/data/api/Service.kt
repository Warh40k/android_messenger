package study.nikita.chat.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import study.nikita.chat.data.model.Channel
import study.nikita.chat.data.model.Message
import retrofit2.http.*
import study.nikita.chat.data.model.Image
import study.nikita.chat.data.model.LoginRequest
import study.nikita.chat.data.model.User

interface ApiService {
    @POST("/login")
    suspend fun login(@Body loginRequest : LoginRequest) : String

    @POST("/addusr")
    suspend fun register(@Body userData: String) : String

    @POST("/messages")
    suspend fun sendMessage(@Header("X-Auth-Token") token: String, @Body message: Message) : String

    @POST("/logout")
    suspend fun logout(@Header("X-Auth-Token") token: String)

    @GET("/channels")
    suspend fun getChannels(): List<Channel>

    @GET("/users")
    suspend fun getUsers(): List<User>

    @GET("/img/{path}")
    suspend fun getImage(@Path("path") path: String): Image

    @GET("/thumb/{path}")
    suspend fun getThumb(@Path("path") path: String): Image

    @GET("/channel/{channel}")
    suspend fun getMessages(@Path("channel") chatId: String): List<Message>

    companion object {
        private const val BASE_URL = "https://faerytea.name:8008"

        fun create(): ApiService {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}