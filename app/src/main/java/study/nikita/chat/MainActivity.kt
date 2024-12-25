package study.nikita.chat

import study.nikita.chat.repository.AuthRepository
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import study.nikita.chat.ui.ChatScreen
import study.nikita.chat.ui.FullScreenImage
import study.nikita.chat.ui.MessageList
import study.nikita.chat.ui.MessageScreen
import study.nikita.chat.ui.theme.ChadTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        val authRepository = AuthRepository(context = LocalContext.current)
                        val authToken = remember {authRepository.getAuthToken()}
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = if (authToken.isNullOrEmpty()) "auth" else "main") {
                            composable("auth") { AuthScreen(authRepository, navController) }
                            composable("main") { ChatScreen(navController) }
                            composable("messages") { MessageScreen(navController) }
                            // Full-screen image
                            composable(
                                route = "fullscreen/{imageUrl}",
                                arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                                FullScreenImage(imageUrl) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
