package com.android.sabsigan.main.chatting

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.sabsigan.R
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.databinding.ActivityChatBinding
import com.android.sabsigan.viewModel.ChatViewModel

class ChatActivity : AppCompatActivity() {
    private var mBinding: ActivityChatBinding? = null // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private val viewModel by viewModels<ChatViewModel>()
    private lateinit var wifiConnectReceiver: WifiConnectReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        val value = intent.getStringExtra("chatRoomID")
        viewModel.setChatID(value!!)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        wifiConnectReceiver = WifiConnectReceiver(viewModel)

//        setupAdapter()
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

    private fun setupAdapter() {
        binding.recyclerView.adapter = MessageAdapter(viewModel)
    }
}