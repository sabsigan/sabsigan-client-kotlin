package com.android.sabsigan.main

import android.app.Activity
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
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
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.NotificationHelper
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.ActivityMain2Binding
import com.android.sabsigan.main.chatting.ChatActivity
import com.android.sabsigan.main.chatting.CreateChatActivity
import com.android.sabsigan.viewModel.MainViewModel
import com.android.sabsigan.wifidirectsample.view.MainActivity3
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class MainActivity2 : AppCompatActivity() {
    private var mBinding: ActivityMain2Binding? = null
    private val binding get() = mBinding!!
    private lateinit var wifiConnectReceiver: WifiConnectReceiver
    private val viewModel by viewModels<MainViewModel>()

    private val permission = android.Manifest.permission.POST_NOTIFICATIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val activityLauncher = openActivityResultLauncher()
        val notfication = NotificationHelper(this)
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

        val navLayout = drawer.getHeaderView(0)
        val myImg = navLayout.findViewById<CircleImageView>(R.id.myImage)
        val myName = navLayout.findViewById<TextView>(R.id.myName)
        val myState = navLayout.findViewById<TextView>(R.id.myState)
        val myWifi = navLayout.findViewById<TextView>(R.id.myWifi)

        myImg.setImageBitmap(viewModel.generateAvatar(viewModel.getUID()!!))

        //네비와 연결 셋업
        navView.setupWithNavController(navController)
        drawer.setupWithNavController(navController) //drawerNavigation 설정하여 동기화

        viewModel.myState.observe(this, Observer {
            myName.text = viewModel.myName.value
            myState.text = it
            myWifi.text = viewModel.getwifiInfo().value
        })

        viewModel.chatRoom.observe(this, Observer {
            Log.d("chatRoomFragment", "변경")
            openChatRoom(it, viewModel.getClickChatName()!!)
        })

        viewModel.chatList.observe(this, Observer {
//            val intent = Intent(this, AlertDialog::class.java).apply {
//            }
//            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//            val temp = notfication.getChannelNotification("test", "testtest")
//            notfication.getManager().notify(1, temp.build())
        })

        binding.addChat.setOnClickListener {
            val layoutInflater = LayoutInflater.from(this)

            val view = layoutInflater.inflate(R.layout.top_dialog_layout, null)
            val backButton = view.findViewById<ImageView>(R.id.backButton)
            val directChat =  view.findViewById<LinearLayout>(R.id.directChat)
            val groupChat =  view.findViewById<LinearLayout>(R.id.groupChat)

            val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setView(view)
                .create()
            alertDialog.window?.setGravity(Gravity.TOP)
            alertDialog.window?.setWindowAnimations(R.style.TopPopupStyle)


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
                R.id.navigation_name -> {
                    onBackPressed()

                    val layoutInflater = LayoutInflater.from(this)
                    val view = layoutInflater.inflate(R.layout.signin_popup, null)
                    val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setView(view)
                        .create()

                    val userImg = view.findViewById<CircleImageView>(R.id.UserImg)
                    val textTitle = view.findViewById<TextView>(R.id.Title)
                    val inputNickname =  view.findViewById<EditText>(R.id.inputNickname)
                    val inputTemp =  view.findViewById<EditText>(R.id.inputTemp)
                    val buttonConfirm =  view.findViewById<Button>(R.id.Button)

                    userImg.setImageBitmap(viewModel.generateAvatar(viewModel.getUID()!!))
                    textTitle.text = "프로필 편집"
                    inputNickname.setText(viewModel.myName.value)
                    inputNickname.hint = "닉네임을 입력하세요"
                    if (viewModel.myState.value != "")
                        inputTemp.setText(viewModel.myState.value)
                    inputTemp.hint = "상태 메시지를 입력해주세요"
                    buttonConfirm.text = "변경하기"

                    buttonConfirm.setOnClickListener {
                        val nickName = inputNickname.text.toString()
                        val state = inputTemp.text.toString()
                        Log.d("프로필 편집", "id: $nickName, temp: $state")

                        if (nickName == null || nickName.equals(""))
                            Toast.makeText(this, "닉네임을 다시 입력해주세요", Toast.LENGTH_SHORT).show()
                        else if (nickName == viewModel.myName.value && state == viewModel.myState.value) {
                            Toast.makeText(this, "변경된 것이 없습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateMyInfo(nickName, state, viewModel.getwifiInfo().value!!)
                            viewModel.myName.value = nickName
                            viewModel.myState.value = state
                            alertDialog.dismiss()
                        }
                    }
                    alertDialog.show()
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

        requestNotificationPermission()

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

                viewModel.createChat(list, chatRoomName)
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

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { ok ->
            if (ok) {
                // 알림권한 허용 o
            } else {
                // 알림권한 허용 x. 자유롭게 대응..
            }
        }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
                -> {
                    // 권한이 존재하는 경우
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    // 권한이 거부 되어 있는 경우
                    showPermissionContextPopup()
                }
                else -> {
                    // 처음 권한을 시도했을 때 띄움
                    requestPermissions(arrayOf(permission), 1000)
                }
            }
        }
    }

    private fun showPermissionContextPopup() {
        android.app.AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다")
            .setMessage("알림을 받으시려면 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(permission), 1000)
            }
            .setNegativeButton("취소하기",{ _,_ ->})
            .create()
            .show()
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