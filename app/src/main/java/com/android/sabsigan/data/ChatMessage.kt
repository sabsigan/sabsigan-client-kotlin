package com.android.sabsigan.data

data class ChatMessage(
    val cid: String = "",
    val uid: String = "",
    val id: String = "",
    val userName: String = "",
    var text: String = "",
    val type: String = "",
    val created_at: String = "",
    var updated_at: String = "",
)