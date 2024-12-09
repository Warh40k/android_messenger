package study.nikita.chat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import study.nikita.chat.network.rest.Chat
import study.nikita.chat.network.rest.Image
import study.nikita.chat.network.rest.Message
import study.nikita.chat.network.rest.MessageData
import study.nikita.chat.network.rest.Text

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}