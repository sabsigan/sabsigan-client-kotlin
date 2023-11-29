package com.android.sabsigan.data

import java.io.Serializable

data class ChatMessage(
    val cid: String = "",
    val uid: String = "",
    val id: String = "",
    val userName: String = "",
    var text: String = "",
    var type: String?,
    val created_at: String = "",
    var updated_at: String = "",
    ) : Serializable