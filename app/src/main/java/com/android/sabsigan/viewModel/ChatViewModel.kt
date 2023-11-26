package com.android.sabsigan.viewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.ChatFbRepository
import com.android.sabsigan.repository.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

// chatActivity viewModel
class ChatViewModel: WiFiViewModel() {
    private val fbRepository = ChatFbRepository(this)

    private lateinit var chatRoom: ChatRoom
    private lateinit var myName: String
    private lateinit var otherName: String

    private val _messageList = MutableLiveData<List<ChatMessage>>()
    val messageList: LiveData<List<ChatMessage>> get() = _messageList

    val inputTxt = MutableLiveData<String>()

    init {
    }

    fun getUID() = fbRepository.uid
    fun getChatID() = chatRoom.id
    fun getChatRoom() = chatRoom.name?: otherName

    fun setOtherName(oname: String) {
        otherName = oname
    }

    fun setMessageList(list: List<ChatMessage>) {
        _messageList.value = list
    }

    fun setChatInfo(chatRoom: ChatRoom, myName: String) {
        this.chatRoom = chatRoom
        this.myName = myName

        val otherUserID = chatRoom.users.withIndex()
            .first { fbRepository.uid == it.value }
            .value

        fbRepository.setOtherName(otherUserID)
        fbRepository.setMessageList()
    }

    fun sendBtnClick() {
        Log.d("click", "메시지 전송 버튼 클릭")
        val msg = inputTxt.value

        if (msg != null && !msg.equals("")) {
            Log.d("click", "메시지 전송")
            fbRepository.sendMessage(msg, chatRoom.id, myName)
            inputTxt.value = ""
        }
    }

    fun myMsgLongClick(): Boolean {
        Log.d("myMSG", "롱클릭")

        return true
    }

    fun otherMsgLongClick(): Boolean {
        Log.d("otherMSG", "롱클릭")

        return true
    }
}