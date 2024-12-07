package study.nikita.chat.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class AuthRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun clearAuthToken() {
        sharedPreferences.edit().remove("auth_token").apply()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext context: Context): AuthRepository {
        return AuthRepository(context)
    }
}