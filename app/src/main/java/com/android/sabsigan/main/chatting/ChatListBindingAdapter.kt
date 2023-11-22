//package com.android.sabsigan.main.chatting
//
//import ChatListAdapter
//import android.annotation.SuppressLint
//import androidx.databinding.BindingAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.android.sabsigan.data.ChatRoom
//
//object ChatListBindingAdapter {
//    @BindingAdapter("app:chatRoomItems")
//    @JvmStatic
//    fun setChats(recyclerView: RecyclerView, items: List<ChatRoom>?) {
//        items?.let {
//            (recyclerView.adapter as ChatListAdapter).setChatList(items)
//        }
//    }
//}