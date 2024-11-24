package study.nikita.chat

import AuthManager
import AuthScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val authManager = AuthManager(context = LocalContext.current)
    val authToken = remember {authManager.getAuthToken()}
    NavHost(navController = navController, startDestination = if (authToken.isNullOrEmpty()) "auth" else "main") {
        composable("auth") { AuthScreen(authManager) }
        composable("main") { ChatList(navController) }
    }
    if (authToken.isNullOrEmpty()) {
        // Navigate to Authorization screen if no token
        navController.navigate("auth")
    } else {
        // Main screen after successful authentication
        ChatList(navController)
    }
}

@Composable
fun ChatList(navController: NavController) {
    Text("Not implemented")
}