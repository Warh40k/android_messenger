package study.nikita.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import study.nikita.chat.data.model.Chat
import study.nikita.chat.data.model.Message
import study.nikita.chat.data.viewmodel.ChatListViewModel
import study.nikita.chat.data.viewmodel.MessageListViewModel

@Composable
fun ChatScreen(navController : NavController) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> ChatPortrait(navController)
        Configuration.ORIENTATION_LANDSCAPE -> ChatAlbum(navController)
        else -> throw Exception("some kind of shit")
    }
}

@Composable
fun ChatPortrait(navController: NavController) {
    ChatList(navController)
}

@Composable
fun ChatList(navController: NavController, chatListViewModel: ChatListViewModel = hiltViewModel()) {
    val chanList by chatListViewModel.chatList.collectAsState()

    LaunchedEffect(chanList) {
        if (chanList.isEmpty()) {
            chatListViewModel.getChatList()
        }
    }
    Column {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(chanList) { channel ->
                ChatItem(channel, navController)
            }
        }
    }

}

@Composable
fun ChatAlbum(navController: NavController) {
    Row (modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            ChatList(navController)
        }

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            MessageList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageList(messageListViewModel: MessageListViewModel = hiltViewModel()) {
    val messages by messageListViewModel.messages.collectAsState()
    val selected by messageListViewModel.selected.collectAsState()
    val messageField by messageListViewModel.messageInput.collectAsState()
    val isLoading = messageListViewModel.isLoading

    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isEmpty() && selected != "") {
            messageListViewModel.getMessageList(selected, lastId = Int.MAX_VALUE)
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == messages.size - 1) {
            messageListViewModel.getMessageList(selected, lastId = messages.last().id)
        }
    }

    LaunchedEffect(selected) {
        messageListViewModel.cleanMessageList()
        messageListViewModel.getMessageList(selected, lastId = Int.MAX_VALUE)
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
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = listState
                    ) {
                        items(messages) { message ->
                            MessageItem(message)
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
    })
}

@Composable
fun ChatItem(chat: Chat, navController: NavController, chatListViewModel: ChatListViewModel = hiltViewModel()) {
    val orientation = LocalConfiguration.current.orientation
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
            Text(text = chat.name, style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable {
                    chatListViewModel.selectChat(chat.name)
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        navController.navigate("messages")
                    }
                }
            )
            Text(text = "Id: ${chat.id}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun MessageItem(message: Message) {
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
            Text(text = message.from, style = MaterialTheme.typography.titleSmall)
            Text(text = message.data.Text.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
