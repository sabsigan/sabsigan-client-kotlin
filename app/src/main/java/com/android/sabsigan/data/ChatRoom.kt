package com.android.sabsigan.data

import java.io.Serializable
import java.text.SimpleDateFormat

data class ChatRoom (
    val id: String = "",
    var name: String?,
    var users: ArrayList<String>,
    var current_wifi: String = "",
    val created_by: String = "",
    val created_at: String = "",
    var updated_at: String = "",
    var last_message_at: String = "", // 시간
    var last_message: String = "", // 텍스트
    var member_cnt: String = "0",
    var disabled: Boolean = false,
    ) : Serializable {
    fun getMemberInt() = member_cnt.toInt()

    fun getLastMsgAt(): String {
        // last_message_at == yyyy-mm-dd HH:mm:ss
        val date = last_message_at.split(" ") // 날짜와 시간 분리

        if (date[0].equals(getDate())) // 날짜가 같으면
            return date[1].substring(0, 5) // 초를 제외한 시간을,  HH:mm:ss에서 ss 제거

        val yyyymmdd = date[0].split("-") // 연월일 분리
        return "${yyyymmdd[1]}월 ${yyyymmdd[2]}일"
    }

    private fun getDate(): String {
        val currentTime : Long = System.currentTimeMillis() // ms로 반환
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dataFormat.format(currentTime)

        return date
    }
}