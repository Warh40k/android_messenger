package study.nikita.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import study.nikita.chat.network.rest.Chat
import study.nikita.chat.viewmodel.ChatListViewModel

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
    val context = LocalContext.current

    LaunchedEffect(chanList) {
        if (chanList.isEmpty()) {
            chatListViewModel.getChatList(context)
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
            MessageList(navController)
        }
    }
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
