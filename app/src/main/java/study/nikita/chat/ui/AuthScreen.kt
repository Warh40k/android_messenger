import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import study.nikita.chat.viewmodel.AuthViewModel
import androidx.navigation.NavController
import study.nikita.chat.repository.AuthRepository

@Composable
fun AuthScreen(authRepository: AuthRepository, navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val token by authViewModel.token.collectAsState()
    val errorMessage by authViewModel.error.collectAsState()

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { authViewModel.clearError() }, // Close dialog on outside touch
            title = { Text("Error") },  // Title of the dialog
            text = { Text(errorMessage ?: "") }, // Content/message
            confirmButton = {
                Button(onClick = { authViewModel.clearError() }) {
                    Text("OK")
                }
            },
        )
    }

    if (!token.isNullOrEmpty()) {
        navController.navigate("main")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login")
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Password")
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                authViewModel.getAuthToken(name, password)
                isLoading = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(text = "Auth")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}