package study.nikita.chat

import android.app.Application
import study.nikita.chat.data.model.AppDatabase

class ChatApplication : Application() {
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Initialize the Room database
        database = AppDatabase.getDatabase(this)
    }
}