package com.android.sabsigan.main.chatting

import MessageAdapter
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.android.sabsigan.R
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.GlideApp
import com.android.sabsigan.databinding.ActivityChatBinding
import com.android.sabsigan.repository.FileHelper
import com.android.sabsigan.viewModel.ChatViewModel
import java.net.InetAddress

class ChatActivity : AppCompatActivity(), View.OnClickListener, SocketHandler.MessageListener {
    private var mBinding: ActivityChatBinding? = null // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private val viewModel by viewModels<ChatViewModel>()
    private lateinit var wifiConnectReceiver: WifiConnectReceiver

    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    private val permission = // 안드로이드 버전에 따라 권한 요청 다르게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
    private val savePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private var msgType: String? = null

    private var socketHandler: SocketHandler? = null
    private val port = 8988
    private var groupOwnerAddress : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        activityLauncher = openActivityResultLauncher()

        val chatRoom = intent.getSerializableExtra("chatRoom") as ChatRoom
        val myName = intent.getStringExtra("myName")
        val chatName = intent.getStringExtra("chatName")


        if(chatRoom.id != "")  {//(일반 채팅)
            viewModel.setChatInfo(chatRoom, myName!!, chatName!!)
        }
        else{//(다이렉트 채팅)

            viewModel.setChatInfo(myName!!, chatName!!)
            groupOwnerAddress = intent.getStringExtra("groupOwnerAddress")
            val groupOwnerInetAddress: InetAddress = InetAddress.getByName(groupOwnerAddress)
            if(myName == "owner"){ //서버
                startSocekt(true,groupOwnerInetAddress)
                viewModel.directInputText.observe(this, Observer {
                    sendMessageToSocket(it)
                })
            }else{ //클라이언트
                startSocekt(false,groupOwnerInetAddress)
                viewModel.directInputText.observe(this, Observer {
                    sendMessageToSocket(it)
                })
            }
        }



        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        wifiConnectReceiver = WifiConnectReceiver(viewModel)

        binding.backButton.setOnClickListener { finish() }
        binding.plusBtn.setOnClickListener(this)
        binding.galleryBtn.setOnClickListener(this)
        binding.fileBtn.setOnClickListener(this)
        binding.toolbar.setOnClickListener(this)
        binding.messageBox.setOnClickListener(this)

        binding.recyclerView.adapter = MessageAdapter(this, viewModel)

        viewModel.inputTxt.observe(this, Observer {
            viewModel.MsgNotEmpty.value = !it.isNullOrBlank()
        })

        viewModel.messageList.observe(this, Observer {
            (binding.recyclerView.adapter as MessageAdapter).setMessageList(it)
            binding.recyclerView.scrollToPosition(it.size-1)
        })

        viewModel.clickedMessage.observe(this, Observer {
            if (it != null && viewModel.clickedMsgView != null)
                showMenuPopUp(it, viewModel.clickedMsgView!!)
            else if (it != null && viewModel.clickedMsgView == null) {
                showImgPopup(it.id, it.text)
            }
        })

        viewModel.clickedFile.observe(this, Observer {
            if (it != null) {
                val fileHelper = FileHelper(this)
                fileHelper.saveAllFile(it.uri!!, it.name)
            }
        })


    }

    private fun startSocekt(isServer : Boolean, address: InetAddress) {
        socketHandler = SocketHandler(port,isServer,address)   //받는거
        socketHandler?.setMessageListener(this)

        val directThread = Thread(socketHandler)
        directThread.start()
    }
    // MessageListener의 메서드 구현
    override fun onMessageReceived(message: String) {
        // 받은 텍스트 처리
        // 여기에서 UI 업데이트 등을 수행할 수 있습니다.
        Log.d("onMessageReceived", message)
        //TODO: 리스트에 메시지를 추가해주면됨
            val time = viewModel.getTime()
            val uuu = viewModel.getUID()
            val uid = viewModel.customHash(arrayListOf(groupOwnerAddress!!, viewModel.getUID()!!))

            viewModel.addMsg(
                    ChatMessage(
                    cid = "",
                    uid =  uid,
                    userName = "익명",
                    text = message,
                    type = "msg",
                    created_at = time,
                    updated_at = time,
                )
            )
    }

    //TODO : 전송 버튼 누르면 메시지 보낼도록 연결해야함
    private fun sendMessageToSocket(message: String) { //보내는거
        socketHandler?.sendMessage(message)
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

        socketHandler?.closeSocket()
        socketHandler?.stopThread() // 스레드 중지

    }

    override fun onClick(v: View?) {
        Log.d("클릭", "ㅇㅇㅇㅇㅇㅇㅇ")

        when (v?.id) {
            R.id.plusBtn -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.messageBox.windowToken, 0)
                binding.plusLayout.visibility = View.VISIBLE
                binding.recyclerView.scrollToPosition(viewModel.messageList.value!!.size-1)
            }

            R.id.galleryBtn -> {
                msgType = "msg"
                binding.plusLayout.visibility = View.GONE
                when {
                    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
                    -> {
                        // 권한이 존재하는 경우
                        openGalley()
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

            R.id.fileBtn -> {
                msgType = "file"
                binding.plusLayout.visibility = View.GONE
                when {
                    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
                    -> {
                        // 권한이 존재하는 경우
                        openFile()
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

            R.id.messageBox -> {
                binding.plusLayout.visibility = View.GONE
                binding.recyclerView.scrollToPosition(viewModel.messageList.value!!.size-1)
            }

            else -> {
                    binding.plusLayout.visibility = View.GONE
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.messageBox.windowToken, 0)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.plusLayout.visibility == View.VISIBLE)
            binding.plusLayout.visibility = View.GONE
        else
            finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // 권한이 부여 된 것입니다.
                    // 허용 클릭 시
                    if (msgType == "img")
                        openGalley()
                    else if (msgType == "file")
                        openFile()
                    else if (msgType == null) {

                    }
                } else {
                    // 거부 클릭시
                    Toast.makeText(this,"권한을 거부했습니다.",Toast.LENGTH_SHORT).show()
                }
            } else -> {
            //Do Nothing
            }
        }
    }

    private fun openActivityResultLauncher() : ActivityResultLauncher<Intent> {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                Log.d("getFile", "Success ${it.data!!.data}")
                viewModel.uri = it.data!!.data

                val extension = getExtensionFromUri(contentResolver, viewModel.uri!!)
                Log.d("getFile", "type $extension")

                viewModel.sendFileOrImg(msgType, extension)
                msgType = null
            } else { Log.d("getFile", "failed") }
        }

        return resultLauncher
    }

    private fun getExtensionFromUri(contentResolver: ContentResolver, uri: Uri): String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()

        // 파일의 확장자를 얻기 위해 MIME 타입을 확인
        val mimeType = contentResolver.getType(uri)

        // MIME 타입으로부터 확장자 추출
        val extension = mimeTypeMap.getExtensionFromMimeType(mimeType)

        return extension
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

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다")
            .setMessage("전자액자에서 사진을 선택하려면 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(permission), 1000)
            }
            .setNegativeButton("취소하기",{ _,_ ->})
            .create()
            .show()
    }

    private fun showMenuPopUp(chatMessage: ChatMessage, view: View) {
        var popup = PopupMenu(applicationContext, view)
        menuInflater.inflate(R.menu.msg_popup, popup.menu);

        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.msg_copy -> {
                    val clipboad = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    if (chatMessage.type == "msg") {
                        val clip = ClipData.newPlainText("label", chatMessage.text)
                        clipboad.setPrimaryClip(clip)
                        Toast.makeText(this, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                    } else
                        Toast.makeText(this, "텍스트만 복사할 수 있습니다.", Toast.LENGTH_SHORT).show()

                    return@setOnMenuItemClickListener true
                }
//                R.id.msg_modify -> {
//                    Log.d("msg_modify", "sss")
//                    return@setOnMenuItemClickListener true
//                }
                R.id.msg_delete -> {
                    viewModel.deleteMsg(chatMessage.id)
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }

        popup.show()
    }

    private fun showImgPopup(id: String, fileName: String) {
        val uri = viewModel.imgMap.get(id)
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.image_popup, null)

        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
        val imageView =  view.findViewById<ImageView>(R.id.imageView)
        val downBtn =  view.findViewById<ImageView>(R.id.downBtn)

        GlideApp.with(this)
            .load(uri)
            .into(imageView)

        closeBtn.setOnClickListener { alertDialog.dismiss() }
        downBtn.setOnClickListener {
            val d = imageView.drawable
            val b = d.toBitmap()

            val fileHelper = FileHelper(this)
            fileHelper.saveBitmapFile(b, fileName)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun openGalley() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        activityLauncher.launch(intent)
    }

    private fun openFile() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        activityLauncher.launch(intent)
    }
}