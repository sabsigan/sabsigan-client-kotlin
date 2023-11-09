package com.android.sabsigan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.sabsigan.databinding.ActivityTestChatListBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

class TestChatListActivity : AppCompatActivity() {
    private var mBinding: ActivityTestChatListBinding? = null    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    var MY_KEY = BuildConfig.STREAM_KEY // API KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTestChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = this,
        )

        // Step 2 - ChatClient 초기화
        val client = ChatClient.Builder(MY_KEY, applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL)
            .build()

        // Step 3 - 유저 정보 초기화
        val user = User(
            id = "marvel",
            name = "Iron Man",
            image = "https://bit.ly/2TIt8NR"
        )
        val token = client.devToken(user.id) // developer 토큰 생성
        client.connectUser( // 유저 로그인
            user = user,
            token = token
        ).enqueue {
            if (it.isSuccess) {
                // Step 4 - Set the channel list filter and order
                // This can be read as requiring only channels whose "type" is "messaging" AND
                // whose "members" include our "user.id"
                val filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(user.id))
                )
                val viewModelFactory = ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
                val viewModel: ChannelListViewModel by viewModels { viewModelFactory }

                // Step 5 - Connect the ChannelListViewModel to the ChannelListView, loose
                //          coupling makes it easy to customize
                // Step 5 - ChannelListViewModel 생성 및 ChannelListView과 연동
                viewModel.bindView(binding.channelListView, this)
                binding.channelListView.setChannelItemClickListener { channel ->
                    startActivity(TestMessageListActivity.newIntent(this, channel))
                }
            } else {
                Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show()
            }

            // Step 4 - 새로운 그룹 (채널) 생성
//            client.createChannel(
//                channelType = "messaging",
//                channelId = "new_channel_02",
//                memberIds = listOf(user.id),
//                extraData = mapOf("name" to "My New Channel")
//            ).enqueue()
        }
    }
}