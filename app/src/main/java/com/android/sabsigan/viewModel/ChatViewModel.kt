package com.android.sabsigan.viewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

// chatActivity viewModel
class ChatViewModel: WiFiViewModel() {
    private val firebaseRepository = FirebaseRepository()

    private var auth = Firebase.auth
    private val uid = auth.currentUser?.uid
    private val chatID = MutableLiveData<String>()

    private val _messageList = MutableLiveData<ArrayList<ChatMessage>>()
    val messageList: LiveData<ArrayList<ChatMessage>> get() = _messageList

    init {

    }

    fun getUID(): String? {
        return uid
    }

    fun setChatID(cid: String) {
        chatID.value = cid

        runBlocking {
            _messageList.value = firebaseRepository.getMessageList(chatID.value!!)
        }
    }
}