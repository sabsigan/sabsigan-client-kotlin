package com.android.sabsigan.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.sabsigan.R
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.SimpleUser
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.ActivityMain2Binding
import com.android.sabsigan.main.chatting.ChatActivity
import com.android.sabsigan.main.chatting.CreateChatActivity
import com.android.sabsigan.viewModel.MainViewModel
import com.android.sabsigan.wifidirectsample.view.MainActivity3

class MainActivity2 : AppCompatActivity() {
    private var mBinding: ActivityMain2Binding? = null
    private val binding get() = mBinding!!
    private lateinit var wifiConnectReceiver: WifiConnectReceiver
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val activityLauncher = openActivityResultLauncher()
        wifiConnectReceiver = WifiConnectReceiver(viewModel)

        if (!isReceiverRegistered(this))
            registerReceiver(wifiConnectReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록

        window.statusBarColor = ContextCompat.getColor(this, R.color.custom_blue) // 스테이터스바 색 변경
        setSupportActionBar(binding.toolbar)        //엑션바 설정
        supportActionBar?.setTitle(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 왼쪽 상단에 버튼 만들기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24) // 왼쪽 상단 버튼 아이콘 지정

        val navView: BottomNavigationView = binding.navView //바텀 네비게이션
        val drawer = binding.navDrawer // 왼쪽 드로우어
        val navController = findNavController(R.id.nav_host_fragment_activity_main2) // 네비 컨트롤러

        //네비와 연결 셋업
        navView.setupWithNavController(navController)
        drawer.setupWithNavController(navController) //drawerNavigation 설정하여 동기화

        viewModel.chatRoom.observe(this, Observer {
            Log.d("chatRoomFragment", "변경")
            openChatRoom(it, viewModel.getClickChatName()!!)
        })

        binding.addChat.setOnClickListener {
            val layoutInflater = LayoutInflater.from(this)
            val view = layoutInflater.inflate(R.layout.top_dialog_layout, null)

            val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setView(view)
                .create()
            alertDialog.window?.setGravity(Gravity.TOP)
            alertDialog.window?.setWindowAnimations(R.style.TopPopupStyle)

            val backButton = view.findViewById<ImageView>(R.id.backButton)
            val directChat =  view.findViewById<LinearLayout>(R.id.directChat)
            val groupChat =  view.findViewById<LinearLayout>(R.id.groupChat)

            backButton.setOnClickListener { alertDialog.dismiss() }
            directChat.setOnClickListener {
                val intent =  Intent(this, MainActivity3::class.java) // 와이파이 다이렉트 채팅
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("userList", viewModel.userList.value as ArrayList<User>)

                startActivity(intent)
                alertDialog.dismiss() }
            groupChat.setOnClickListener {
                val intent =  Intent(this, CreateChatActivity::class.java) // 그룹 채팅 생성 액티비티
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("userList", viewModel.userList.value as ArrayList<User>)

                activityLauncher.launch(intent)
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        //drawer wifi선택시 와이파이 다이렉트 이동
        drawer.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_wifi -> {
                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) { //앱바 왼쪽 상단 매뉴 선택 (home자리임)
            android.R.id.home -> binding.mainActivityLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(binding.mainActivityLayout.isDrawerOpen(binding.navDrawer))
            binding.mainActivityLayout.closeDrawer(GravityCompat.START)
        else {
//            super.onBackPressed()
            finish()
        }
    }

    private fun openActivityResultLauncher() : ActivityResultLauncher<Intent> {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d("create ChatRoom", "Success")
                val list = it.data?.getSerializableExtra("selectedList") as ArrayList<User>
                val chatRoomName = it.data?.getStringExtra("chatRoomName")
                Log.d("create ChatRoom", chatRoomName!!)

                viewModel.createGroupChat(list, chatRoomName)
            } else { Log.d("create ChatRoom", "failed") }
        }

        return resultLauncher
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

    private fun openChatRoom(chatRoom: ChatRoom, chatName: String) {
        if (!chatRoom.id.equals("")) {
            Log.d("chatRoomFragment", chatRoom.id)

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("myName", viewModel.myName.value)
            intent.putExtra("chatRoom", chatRoom)
            intent.putExtra("chatName", chatName)

            startActivity(intent)
        }
    }
}