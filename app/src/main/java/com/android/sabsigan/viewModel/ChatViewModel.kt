package com.android.sabsigan.viewModel
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.ChatFbRepository
import com.android.sabsigan.repository.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// chatActivity viewModel
class ChatViewModel: WiFiViewModel() {
    private val fbRepository = ChatFbRepository(this)

    private lateinit var chatRoom: ChatRoom
    private lateinit var myName: String
    private lateinit var chatName: String

    private val _messageList = MutableLiveData<List<ChatMessage>>()
    private val _msgView = MutableLiveData<View>()
    private var msgId = ""

    private val msgComparator : Comparator<ChatMessage> = compareBy { it.created_at }

    val inputTxt = MutableLiveData<String>()
    val nullTxt = "메시지가 삭제되었습니다"
    val messageList: LiveData<List<ChatMessage>> get() = _messageList
    val msgView: LiveData<View> get() = _msgView

    val white = "#FFFFFFFF"
    val gray = "#EEEEEE"

    init {

    }

    fun getUID() = fbRepository.uid
    fun getChatID() = chatRoom.id
    fun getChatName() = chatRoom.name?: chatName

    fun setMessageList(list: List<ChatMessage>) {
        _messageList.value = list
        _messageList.value = _messageList.value!!.sortedWith(msgComparator).toMutableList() // 시간 순으로 정렬
    }

    fun addMsgList(chatMsg: ChatMessage) {
        val index = _messageList.value!!.withIndex()
            .firstOrNull  {chatMsg.id == it.value.id}
            ?.index?: -1

        if (index == -1) {
            (_messageList.value as ArrayList<ChatMessage>).add(chatMsg)  // 이미 있는 유저면
            _messageList.value = _messageList.value!!.sortedWith(msgComparator).toMutableList() // 시간 순으로 정렬
            Log.d("msgChange", "ADDED")
        } else
            modyfyMsgList(chatMsg)
    }

    fun modyfyMsgList(chatMsg: ChatMessage) {
        val index = _messageList.value!!.withIndex()
            .first {chatMsg.id == it.value.id}
            .index

        _messageList.value?.get(index)?.text = chatMsg.text
        _messageList.value?.get(index)?.updated_at = chatMsg.updated_at
        _messageList.value?.get(index)?.type = chatMsg.type
        _messageList.value?.get(index)?.updated_at = chatMsg.updated_at

        _messageList.value = _messageList.value!!.sortedWith(msgComparator).toMutableList() // 시간 순으로 정렬
        Log.d("msgChange", "MODIFIED")
    }

    fun removeMsgList(chatMsg: ChatMessage) {
        (_messageList.value as ArrayList<ChatMessage>).remove(chatMsg)

        _messageList.value = _messageList.value!!.sortedWith(msgComparator).toMutableList() // 시간 순으로 정렬
        Log.d("msgChange", "REMOVED")
    }

    fun setChatInfo(chatRoom: ChatRoom, myName: String, chatName: String) {
        this.chatRoom = chatRoom
        this.myName = myName
        this.chatName = chatName


        viewModelScope.launch {
            _messageList.value = fbRepository.getMsgList(chatRoom.id)
            fbRepository.getChanggeMsgList(chatRoom.id)
        }
    }

    fun sendBtnClick() {
        Log.d("click", "메시지 전송 버튼 클릭")
        val msg = inputTxt.value

        if (msg != null && msg.isNotEmpty() && !msg.isNullOrBlank()) {
            Log.d("click", "메시지 전송")
            fbRepository.sendMessage(msg, chatRoom.id, myName)
            inputTxt.value = ""
        }
    }

    fun updateMsg(text: String) {
        fbRepository.updateMessage(text, chatRoom.id, msgId)
    }

    fun deleteMsg() {
        fbRepository.deleteMessage(chatRoom.id, msgId)
    }

    fun msgLongClick(view: View, chatMsg: ChatMessage): Boolean {
        Log.d("MSG", "롱클릭")

        _msgView.value = view
        msgId = chatMsg.id

        return true
    }
}