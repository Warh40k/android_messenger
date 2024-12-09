package study.nikita.chat.network.rest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Chat(
    val id: String,
    val name: String
)
data class Message(
    val id: Int,
    val from: String,
    val to: String,
    val data: MessageData?,
    @Expose(serialize = false) val time: Long
)
data class MessageData(
    @SerializedName("Text") val text: Text?,
    @SerializedName("Image") val image: Image?
)
data class Text(
    val text: String
)
data class Image(
    val link : String
)
data class LoginRequest(
    @SerializedName("name") val name: String,
    @SerializedName("pwd") val password: String
)
