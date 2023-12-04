package com.android.sabsigan.viewModel

import android.net.Uri
import android.text.Layout
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.FileTemp
import com.android.sabsigan.main.chatting.SocketHandler
import com.android.sabsigan.repository.ChatFbRepository
import com.android.sabsigan.repository.FileHelper
import kotlinx.coroutines.launch

class ChatViewModel: WiFiViewModel() {
    private val fbRepository = ChatFbRepository(this)

    private lateinit var chatRoom: ChatRoom
    private lateinit var myName: String
    private lateinit var chatName: String

    private val msgComparator : Comparator<ChatMessage> = compareBy { it.created_at }
    private val _messageList = MutableLiveData<List<ChatMessage>>()
    private val _clickedMessage = MutableLiveData<ChatMessage?>()
    private val _clickedFile = MutableLiveData<FileTemp?>()

    private var socketHandler: SocketHandler? = null
    val messageList: LiveData<List<ChatMessage>> get() = _messageList
    val clickedMessage: LiveData<ChatMessage?> get() = _clickedMessage
    val clickedFile : LiveData<FileTemp?> get() = _clickedFile

    val imgMap = HashMap<String, Uri>()

    val directInputText = MutableLiveData<String>()
    val inputTxt = MutableLiveData<String>()
    val MsgNotEmpty = MutableLiveData<Boolean>()

    var clickedMsgView: View? = null

    val nullTxt = "메시지가 삭제되었습니다"
    var uri:Uri? = null

    init {
        MsgNotEmpty.value = false
    }

    fun getUID() = fbRepository.uid
    fun getChatID() = chatRoom.id
    fun getChatName() = chatRoom.name?: chatName

    fun isFileOrImg(type: String?): Boolean {
        if (type == null)
            return false

        if (type == "img" || type == "file")
            return true

        return false
    }

    fun isFile(type: String?): Boolean {
        if (type == null)
            return false

        if (type == "file")
            return true

        return false
    }

    fun isImg(type: String?): Boolean {
        if (type == null)
            return false

        if (type == "img")
            return true

        return false
    }

    fun fileType(chatMsg: ChatMessage): String {
        if (chatMsg.type == "file") {
            val file = chatMsg.text.split(".")
            return file[1]
        }

        return ""
    }

    fun addMsg(chatMsg: ChatMessage) {
        if (_messageList.value == null) {
            _messageList.postValue(_messageList.value?.apply {
                _messageList.value = arrayListOf(chatMsg)
            }?: mutableListOf(chatMsg))
        } else {
            // postValue를 사용하여 LiveData를 메인 스레드에서 업데이트
            _messageList.postValue(_messageList.value?.apply {
                (this as ArrayList<ChatMessage>).add(chatMsg)
                sortedWith(msgComparator) // 시간 순으로 정렬
            } ?: mutableListOf(chatMsg))

        }
    }



    fun addMsgList(chatMsg: ChatMessage) {
        val index = _messageList.value!!.withIndex()
            .firstOrNull  {chatMsg.id == it.value.id}
            ?.index?: -1

        if (index == -1) {
            (_messageList.value as ArrayList<ChatMessage>).add(chatMsg)
            _messageList.value = _messageList.value!!.sortedWith(msgComparator).toMutableList() // 시간 순으로 정렬

            if (chatMsg.type == "img") {
                viewModelScope.launch {
                    val uri = fbRepository.downloadImg(chatMsg.text)
                    imgMap.put(chatMsg.id, uri!!)
                }
            }

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

        _messageList.value = _messageList.value
        Log.d("msgChange", "MODIFIED")
    }

    fun removeMsgList(chatMsg: ChatMessage) {
        (_messageList.value as ArrayList<ChatMessage>).remove(chatMsg)

        _messageList.value = _messageList.value
        Log.d("msgChange", "REMOVED")
    }

    fun setChatInfo(chatRoom: ChatRoom, myName: String, chatName: String) {
        this.chatRoom = chatRoom
        this.myName = myName
        this.chatName = chatName

        viewModelScope.launch {
            _messageList.value = fbRepository.getMsgList(chatRoom.id)
            _messageList.value = _messageList.value!!.sortedWith(msgComparator).toMutableList() // 시간 순으로 정렬

            _messageList.value!!.forEach {
                if (it.type == "img") {
                    val uri = fbRepository.downloadImg(it.text)

                    if (uri != null)
                        imgMap.put(it.id, uri)
                }
            }

            fbRepository.getChanggeMsgList(chatRoom.id)
        }
    }

    fun setChatInfo(myName: String, chatName: String) {
        this.chatRoom = ChatRoom(name = null, users = arrayListOf())
        this.myName = myName
        this.chatName = chatName

        _messageList.value
    }

    fun sendBtnClick() {
        Log.d("click", "메시지 전송 버튼 클릭")

        if (MsgNotEmpty.value!!) {
            Log.d("click", "메시지 전송")

            if (chatRoom.id != "") {
                fbRepository.sendMessage("msg", inputTxt.value!!, chatRoom.id, myName)
            } else {
                //TODO SocketHandler에서 메시지 보내는 함수 바로 적어 (inputTxt.value의 값을 보내는거야)
                directInputText.value = inputTxt.value!!
//                socketHandler?.sendMessage(inputTxt.value!!)
                val time = getTime()

                addMsg(
                    ChatMessage(
                        cid = "",
                        uid = getUID()!!,
                        userName = "who?",
                        text = inputTxt.value!!,
                        type = "msg",
                        created_at = time,
                        updated_at = time,
                    )
                )
            }

            inputTxt.value = ""
        }
    }

    fun sendFileOrImg(type: String?, extension: String?) {
        if (type != null && type == "msg" && uri != null) {
            fbRepository.uploadImg(uri!!, chatRoom.id, myName, extension!!)
            uri = null
        } else if (type != null && uri != null) {
            fbRepository.uploadFile(uri!!, chatRoom.id, myName, extension!!)
            uri = null
        }
    }

    fun updateMsg(text: String, msgId: String) {
        fbRepository.updateMessage(text, chatRoom.id, msgId)
    }

    fun deleteMsg(msgId: String) {
        fbRepository.deleteMessage(chatRoom.id, msgId)
    }

    fun imgClick(chatMsg: ChatMessage) {
        Log.d("img", "클릭")
        clickedMsgView = null
        _clickedMessage.value = chatMsg
    }

    fun fileClick(chatMsg: ChatMessage) {
        Log.d("file", "클릭")

        viewModelScope.launch {
            _clickedFile.value = FileTemp(chatMsg.text, fbRepository.downloadFile(chatMsg.text))
        }
    }

    fun msgLongClick(view: View, chatMsg: ChatMessage): Boolean {
        Log.d("MSG", "롱클릭")
        clickedMsgView = view
        _clickedMessage.value = chatMsg

        return true
    }
}