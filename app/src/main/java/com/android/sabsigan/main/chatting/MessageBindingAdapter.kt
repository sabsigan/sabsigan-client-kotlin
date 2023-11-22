//package com.android.sabsigan.main.chatting
//
//import MessageAdapter
//import android.annotation.SuppressLint
//import androidx.databinding.BindingAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.android.sabsigan.data.ChatMessage
//import com.android.sabsigan.data.ChatRoom
//
//object MessageBindingAdapter {
//    @BindingAdapter("app:messageItems")
//    @JvmStatic
//    fun setMessages(recyclerView: RecyclerView, items: List<ChatMessage>?) {
//        items?.let {
//            (recyclerView.adapter as MessageAdapter).setMessageList(it)
//        }
//    }
//}