package study.nikita.chat.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChatRepositoryModule {
    @Provides
    fun provideRepository(): ChatRepository {
        return ChatRepository()
    }
}