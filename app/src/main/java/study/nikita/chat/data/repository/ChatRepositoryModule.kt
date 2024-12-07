package study.nikita.chat.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatRepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(): ChatRepository {
        return ChatRepository()
    }
}