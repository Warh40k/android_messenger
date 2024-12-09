package study.nikita.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import study.nikita.chat.Config
import study.nikita.chat.network.rest.Message
import study.nikita.chat.viewmodel.MessageListViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageList(navController: NavController, messageListViewModel: MessageListViewModel = hiltViewModel()) {
    val messages by messageListViewModel.messages.collectAsState()
    val selected by messageListViewModel.selected.collectAsState()
    val incoming by messageListViewModel.incomingMsg.collectAsState()
    val messageField by messageListViewModel.messageInput.collectAsState()
    val isLoading by messageListViewModel.isLoading.collectAsState()

    val listState = rememberLazyListState()
    val context = LocalContext.current

    val errorMessage by messageListViewModel.error.collectAsState()

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { messageListViewModel.clearError() }, // Close dialog on outside touch
            title = { Text("Error") },  // Title of the dialog
            text = { Text(errorMessage ?: "") }, // Content/message
            confirmButton = {
                Button(onClick = { messageListViewModel.clearError() }) {
                    Text("OK")
                }
            },
        )
    }

    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
        if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == messages.size - 1) {
            messageListViewModel.getMessageList(context, lastId = messages.last().id)
        }
    }

    LaunchedEffect(selected) {
        messageListViewModel.cleanMessageList()
        messageListViewModel.getMessageList(context, lastId = Int.MAX_VALUE)
    }

    LaunchedEffect(incoming) {
        if (incoming.isNotEmpty()) {
            messageListViewModel.receiveNewMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selected) },
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column (
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = listState,
                        reverseLayout = true
                    ) {
                        items(messages) { message ->
                            MessageItem(message, navController)
                        }

                        if (isLoading) {
                            item {
                                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
                            }
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = messageField,
                            onValueChange = { messageListViewModel.onTextChanged(it) },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.Gray),
                            singleLine = true,
                        )
                        Button(
                            onClick = {
                                messageListViewModel.sendMessage()
                                messageListViewModel.cleanUserInput()
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text(text = "Send")
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun MessageItem(message: Message, navController: NavController) {
    val pattern = "yyyy-MM-dd HH:mm:ss.SSS"
    val date = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(message.time))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (message.data?.image != null) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            navController.navigate("fullscreen/${message.data.image.link}")
                        },
                    contentScale = ContentScale.Crop,
                    model = Config.BASE_URL + "thumb/" + message.data.image.link,
                    contentDescription = null
                )
            }
            Text(text = message.from, style = MaterialTheme.typography.titleSmall)
            Text(text = message.data?.text?.text ?: "", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun FullScreenImage(imageUrl: String, onBack : () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            model = Config.BASE_URL + "image/" + imageUrl,
            contentDescription = "Full Screen Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onBack() }
        )
    }
}