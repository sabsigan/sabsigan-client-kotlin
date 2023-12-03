package com.android.sabsigan.repository

import android.util.Log
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.viewModel.ChatViewModel
import com.android.sabsigan.viewModel.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

open class FirebaseRepository {
    var auth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage

    val uid = auth.currentUser?.uid

    fun getTime() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
}