package com.android.sabsigan.main.chatting

import MessageAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.sabsigan.R
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.databinding.ActivityChatBinding
import com.android.sabsigan.main.user.UserListAdapter
import com.android.sabsigan.viewModel.ChatViewModel

class ChatActivity : AppCompatActivity() {
    private var mBinding: ActivityChatBinding? = null // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private val viewModel by viewModels<ChatViewModel>()
    private lateinit var wifiConnectReceiver: WifiConnectReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        val chatRoom = intent.getSerializableExtra("chatRoom") as ChatRoom
        val myName = intent.getStringExtra("myName")
        val chatName = intent.getStringExtra("chatName")

        viewModel.setChatInfo(chatRoom, myName!!, chatName!!)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        wifiConnectReceiver = WifiConnectReceiver(viewModel)

        binding.recyclerView.adapter = MessageAdapter(viewModel)

        binding.backButton.setOnClickListener {
            finish()
        }

        viewModel.messageList.observe(this, Observer {
            (binding.recyclerView.adapter as MessageAdapter).setMessageList(it)
        })

        viewModel.msgView.observe(this, Observer {
            val text = (it as TextView).text.toString()
            var popup = PopupMenu(applicationContext, it)
            menuInflater.inflate(R.menu.msg_popup, popup.menu);
            popup.show()

            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.msg_copy -> {
                        val clipboad = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("label", text)
                        clipboad.setPrimaryClip(clip)
                        Toast.makeText(this, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.msg_modify -> {
                        Log.d("msg_modify", "sss")
                        return@setOnMenuItemClickListener true
                    }
                    R.id.msg_delete -> {
                        viewModel.deleteMsg()
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        if (isReceiverRegistered(this))
            unregisterReceiver(wifiConnectReceiver)
    }

    override fun onResume() {
        super.onResume()

        if (!isReceiverRegistered(this))
            registerReceiver(wifiConnectReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isReceiverRegistered(this))
            unregisterReceiver(wifiConnectReceiver)
    }

    private fun isReceiverRegistered(context: Context): Boolean {
        val pm = context.packageManager
        val intent = Intent(ConnectivityManager.CONNECTIVITY_ACTION)
        val receivers = pm.queryBroadcastReceivers(intent, 0)

        for (receiver in receivers) {
            if (receiver.activityInfo.packageName == context.packageName) {
                return true // 리시버가 현재 등록되어 있음
            }
        }

        return false // 리시버가 현재 등록되어 있지 않음
    }
}