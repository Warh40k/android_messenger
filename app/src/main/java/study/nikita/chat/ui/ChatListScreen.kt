package study.nikita.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import study.nikita.chat.data.model.Channel

@Composable
fun ChatListScreen(chats: List<Channel>, onSelectChat: (String) -> Unit) {
    LazyColumn {
        items(chats) { chat ->
            Text(chat.name, Modifier.clickable { onSelectChat(chat.id) })
        }
    }
}
