package com.android.sabsigan.data

import java.io.Serializable
import java.text.SimpleDateFormat

data class ChatMessage(
    val cid: String = "",
    val uid: String = "",
    val id: String = "",
    val userName: String = "",
    var text: String = "",
    var type: String?,
    val created_at: String = "",
    var updated_at: String = "",
    ) : Serializable {

    fun getLastMsgAt(): String {
        // last_message_at == yyyy-mm-dd HH:mm:ss
        val date = created_at.split(" ") // 날짜와 시간 분리
        val yyyymmdd = date[0].split("-") // 연월일 분리
        val time = date[1].substring(0, 5) // 초를 제외,  HH:mm:ss에서 ss 제거

        if (date[0].equals(getDate())) // 날짜가 같으면
            return time

        return "${yyyymmdd[1]}월 ${yyyymmdd[2]}일 $time"
    }

    private fun getDate(): String {
        val currentTime : Long = System.currentTimeMillis() // ms로 반환
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dataFormat.format(currentTime)

        return date
    }
}