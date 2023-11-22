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

    private var auth = Firebase.auth
    private val uid = auth.currentUser?.uid
    private val chatID = MutableLiveData<String>()

    private val _messageList = MutableLiveData<List<ChatMessage>>()
    val messageList: LiveData<List<ChatMessage>> get() = _messageList

    val inputTxt = MutableLiveData<String>()


    init {

    }

    fun setMessageList(list: List<ChatMessage>) {
        _messageList.value = list
    }

    fun getUID(): String? {
        return uid
    }

    fun setChatID(cid: String) {
        chatID.value = cid
        fbRepository.setMessageList()
    }

    fun getChatID() = chatID.value

    fun getMessageTxt() {
        Log.d("click", "메시지 전송 버튼 클릭")
        val msg = inputTxt.value

        if (msg != null && !msg.equals("")) {
            Log.d("click", "메시지 전송")
            fbRepository.sendMessage(msg, chatID.value!!)
            inputTxt.value = ""
        }
    }
}