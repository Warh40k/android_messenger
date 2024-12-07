package study.nikita.chat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chats")
data class Chat(
    val id: String,
    val name: String
)
@Entity(tableName = "messages")
data class Message(
    val id: String,
    val from: String,
    val to: String,
    val data: MessageData,
    val timestamp: String
)
data class MessageData(
    val Text: Text,
    val image: Image
)
data class Text(
    val text: String
)
@Entity(tableName="users")
data class User(
    @PrimaryKey val id: String,
    val name: String
)
@Entity(tableName="images")
data class Image(
    @PrimaryKey val id: String,
    val data: String
)
data class LoginRequest(
    @SerializedName("name") val name: String,
    @SerializedName("pwd") val password: String
)
