package com.android.sabsigan.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

class MainViewModel: WiFiViewModel() {
    private val firebaseRepository = FirebaseRepository()

    private val _userList = MutableLiveData<ArrayList<User>>()
    private val _chatList = MutableLiveData<ArrayList<ChatRoom>>()
    private val _chatRoomID = MutableLiveData<String>()

    val userList: LiveData<ArrayList<User>> get() = _userList
    val chatList: LiveData<ArrayList<ChatRoom>> get() = _chatList
    val chatRoomID: LiveData<String> get() = _chatRoomID

    init {
        runBlocking {
            _userList.value = firebaseRepository.getUserList()
            _chatList.value = firebaseRepository.getChatList()
        }
    }

    fun clickUser(otherUser: User) {
        firebaseRepository.createChatRoom(otherUser, 2)
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
    }

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
}