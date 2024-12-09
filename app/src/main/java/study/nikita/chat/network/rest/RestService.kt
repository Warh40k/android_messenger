package study.nikita.chat.network.rest

import retrofit2.http.*

interface ApiService {
    @POST("/login")
    suspend fun login(@Body loginRequest : String) : String

    @POST("/addusr")
    suspend fun register(@Body userData: String) : String

    @POST("/messages")
    suspend fun sendMessage(@Header("X-Auth-Token") token: String, @Body message: Message) : String

    @POST("/logout")
    suspend fun logout(@Header("X-Auth-Token") token: String)

    @GET("/channels")
    suspend fun getChannels(): List<String>

    @GET("/users")
    suspend fun getUsers(): List<String>

    @GET("/img/{path}")
    suspend fun getImage(@Path("path") path: String): Image

    @GET("/thumb/{path}")
    suspend fun getThumb(@Path("path") path: String): Image

    @GET("/channel/{channel}")
    suspend fun getMessages(
        @Path("channel") chatId: String?,
        @Query("lastKnownId") lastKnownId : Int = 0,
        @Query("limit") limit : Int = 20,
        @Query("reverse") reverse : Boolean = false
    ): List<Message>
}