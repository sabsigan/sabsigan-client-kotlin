package com.android.sabsigan.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel: WiFiViewModel() {
    private val t = FirebaseRepository()

    private var auth = Firebase.auth
    private val db = Firebase.firestore
    private val uid = auth.currentUser?.uid

    private val _userList = MutableLiveData<ArrayList<User>>()
    val userList : LiveData<ArrayList<User>> get() = _userList

    private val _chatList = MutableLiveData<ArrayList<ChatRoom>>()
    val chatList : LiveData<ArrayList<ChatRoom>> get() = _chatList

    init {
        _userList.value = t.getUserList()
        _chatList.value = t.getChatList()
    }

    fun clickUser(otherUser: User) {
        t.createChatRoom(otherUser, 2)
    }


//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
}