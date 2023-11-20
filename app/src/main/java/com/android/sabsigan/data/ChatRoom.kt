package com.android.sabsigan.data


data class ChatRoom(
    val id: String = "",
    val created_by: String = "",
    var current_wifi: String = "",
    val created_at: String = "",
    var updated_at: String = "",
    var last_message_at: String = "",
    var member_cnt: Int = 0,
    var disabled: Boolean = false,
    )