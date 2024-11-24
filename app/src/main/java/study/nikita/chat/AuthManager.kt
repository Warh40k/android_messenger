import android.content.Context
import android.content.SharedPreferences

class AuthManager(private val context: Context) {
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