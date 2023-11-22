package com.android.sabsigan.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.MainFbRepository
import kotlinx.coroutines.runBlocking

class MainViewModel: WiFiViewModel() {
    private val fbRepository = MainFbRepository()

    private val _userList = MutableLiveData<MutableList<User>>()
    private val _chatList = MutableLiveData<MutableList<ChatRoom>>()
    private val _chatRoomID = MutableLiveData<String>()

    val userList: LiveData<MutableList<User>> get() = _userList
    val chatList: LiveData<MutableList<ChatRoom>> get() = _chatList
    val chatRoomID: LiveData<String> get() = _chatRoomID

    init {
        runBlocking {
            _userList.value = fbRepository.getUserList()
            _chatList.value = fbRepository.getChatList()
        }
    }

    fun clickUser(otherUser: User) {
        fbRepository.createChatRoom(otherUser, 2)
    }

    fun clickChatRoom(chatRoom: ChatRoom) {
        Log.d("chatRoomFragment", "클릭")
        _chatRoomID.value = chatRoom.id
    }

    fun clickKKK() {
        _userList.value?.add(
            User(   // 여기는 datastore로 자기 로컬값 가져오기
                id = "uid",
                name = "name",
                state = "state",
                current_wifi = "current_wifi",
                created_at = "time",
                updated_at = "time",
                last_active = "time",
                online = true
            )
        )

        (_userList.value as ArrayList<User>).forEach {
            Log.d("tttt", it.id)
        }
    }

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
}