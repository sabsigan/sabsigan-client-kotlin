package com.android.sabsigan.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
// chatActivity viewModel
class ChatViewModel: WiFiViewModel() {
    private var auth = Firebase.auth
    private val uid = auth.currentUser?.uid

    fun getUID(): String? {
        return uid
    }
}