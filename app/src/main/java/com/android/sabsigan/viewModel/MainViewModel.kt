package com.android.sabsigan.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.MainFbRepository
import kotlinx.coroutines.runBlocking

class MainViewModel: WiFiViewModel() {
    private val fbRepository = MainFbRepository(this)

    private val _userList = MutableLiveData<List<User>>()
    private val _chatList = MutableLiveData<List<ChatRoom>>()
    private val _chatRoom = MutableLiveData<ChatRoom>()

    val userList: LiveData<List<User>> get() = _userList
    val chatList: LiveData<List<ChatRoom>> get() = _chatList
    val chatRoom: LiveData<ChatRoom> get() = _chatRoom

    val myName = MutableLiveData<String>()
    val myState = MutableLiveData<String>()

    init {

    }

    private fun getOtherUserName(chatRoom: ChatRoom): String {
        val otherUserID = chatRoom.users.withIndex()
            .first { fbRepository.uid != it.value }
            .index

        Log.d("chatRoom test", otherUserID.toString())
//
//        val user = _userList.value!!.withIndex()
//            .first { otherUserID == it.value.id }
//            .value
//
//        Log.d("user test", user.name)

        return "user.name"
    }

    fun setUserInfo(name: String, state: String) {
        myName.value = name
        myState.value = state
    }

    fun setUserList(list: List<User>) {
        _userList.value = list
    }

    fun setChatList(list: List<ChatRoom>) {
        _chatList.value = list
    }

    /**
     * chatRoomName null이면 상대방 이름
     * @param chatRoomName 사용자가 지정한 이름
     * chatRoom의 사람이 2명일 때만 nullable
     */
    fun getChatRoomName(chatRoom: ChatRoom): String {
        return chatRoom.name?: getOtherUserName(chatRoom)
    }
    
    fun clickUser(otherUser: User) {
        Log.d("userFragment", "유저 클릭")

        fbRepository.createChatRoom(arrayListOf(otherUser), null)
        // 여러명 채팅방은 이름 무조건 지정해야함
    }

    fun longClickUser(): Boolean {
        Log.d("userFragment", "유저 long클릭")

        return true
    }

    fun clickChatRoom(chatRoom: ChatRoom) {
        Log.d("chatRoomFragment", "채팅방 클릭")

        _chatRoom.value = chatRoom
    }

    fun longClickChatRoom(chatRoom: ChatRoom): Boolean {
        Log.d("chatRoomFragment", "채팅방 long클릭")

        return true
    }

    fun isIncluded(key: String): Boolean {
        var result = false
        (_chatList.value as List<ChatRoom>).forEach {
            if (it.id.equals(key))
                result = true
        }

        return result
    }

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
}