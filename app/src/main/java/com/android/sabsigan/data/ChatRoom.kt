package com.android.sabsigan.data


data class ChatRoom(
    val id: String = "",
    var name: String = "",
    var users: ArrayList<String>,
    val created_by: String = "",
    val created_at: String = "",
    var updated_at: String = "",
    var last_message_at: String = "", // 시간
    var last_message: String = "", // 텍스트
    var member_cnt: Int = 0,
    var disabled: Boolean = false,
    )