package study.nikita.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import study.nikita.chat.data.model.Chat
import study.nikita.chat.data.viewmodel.ChatListViewModel

@Composable
fun ChatScreen(navController : NavController, chatListViewModel: ChatListViewModel = viewModel()) {

    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> ChatPortrait(navController, chatListViewModel)
        Configuration.ORIENTATION_LANDSCAPE -> ChatAlbum(navController, chatListViewModel)
        else -> throw Exception("some kind of shit")
    }
}

@Composable
fun ChatPortrait(navController: NavController, chatListViewModel: ChatListViewModel) {
    ChatList(navController, chatListViewModel)
}

@Composable
fun ChatList(navController: NavController, chatListViewModel: ChatListViewModel) {
    val chanList by chatListViewModel.chatList.collectAsState()
// Fetch data if the list is empty
    LaunchedEffect(chanList) {
        if (chanList.isEmpty()) {
            chatListViewModel.getChatList()
        }
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chanList) { channel ->
            CommentItem(channel)
        }
    }
}

@Composable
fun ChatAlbum(navController: NavController, chatListViewModel: ChatListViewModel) {
    Row (modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            ChatList(navController, chatListViewModel)
        }

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            MessageList(navController, chatListViewModel)
        }
    }
}

@Composable
fun MessageList(navController: NavController, chatListViewModel: ChatListViewModel) {

}

@Composable
fun CommentItem(chat: Chat) {
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
            Text(text = chat.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Id: ${chat.id}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
