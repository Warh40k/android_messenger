package study.nikita.chat

import AuthManager
import AuthScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import study.nikita.chat.ui.ChatScreen
import study.nikita.chat.ui.MessageList
import study.nikita.chat.ui.theme.ChadTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        val authManager = AuthManager(context = LocalContext.current)
                        val authToken = remember {authManager.getAuthToken()}
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = if (authToken.isNullOrEmpty()) "auth" else "main") {
                            composable("auth") { AuthScreen(authManager, navController) }
                            composable("main") { ChatScreen(navController) }
                            composable("messages") { MessageList() }
                        }
                    }
                }
            }
        }
    }
}
