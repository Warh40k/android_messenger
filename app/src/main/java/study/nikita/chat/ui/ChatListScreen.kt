package study.nikita.chat.ui

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import study.nikita.chat.data.viewmodel.ChannelViewModel

@Composable
fun ChatScreen(navController : NavController, channelViewModel: ChannelViewModel = viewModel()) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> ChatPortrait(navController)
        Configuration.ORIENTATION_LANDSCAPE -> ChatAlbum(navController)
        else -> throw Exception("some kind of shit")
    }
}

@Composable
fun ChatPortrait(navController: NavController) {
    Text("Portrait shit")
}

@Composable
fun ChatAlbum(navController: NavController) {
    Text("Album shit")
}
