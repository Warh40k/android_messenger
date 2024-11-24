import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import study.nikita.chat.data.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(modifier: Modifier, authViewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Observe authToken and errorMessage from ViewModel
    val authToken = authViewModel.token.observeAsState()
//    val errorMessage = authViewModel.errorMessage.observeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = TextFieldValue(name),
            onValueChange = { name = it.text },
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray),
            singleLine = true
        )
        Spacer(modifier = modifier.height(8.dp))

        TextField(
            value = TextFieldValue(password),
            onValueChange = { password = it.text },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray),
            singleLine = true
        )
        Spacer(modifier = modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                authViewModel.getAuthToken(name, password)
                isLoading = false
            },
            modifier = modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = modifier.size(24.dp), color = Color.White)
            } else {
                Text(text = "Login")
            }
        }

        Spacer(modifier = modifier.height(16.dp))

        // Display AuthToken if login is successful
        authToken.value?.let {
            Text(text = "Logged in with token: $it", modifier = modifier.padding(8.dp))
        }
    }
}