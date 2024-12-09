package study.nikita.chat.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import study.nikita.chat.db.ChatDao
import study.nikita.chat.db.MessageDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatRepositoryModule {
    @Provides
    @Singleton
    fun provideChatRepository(dao : ChatDao): ChatRepository {
        return ChatRepository(dao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context : Context): AuthRepository {
        return AuthRepository(context)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(dao : MessageDao): MessageRepository {
        return MessageRepository(dao)
    }
}