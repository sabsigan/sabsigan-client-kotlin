package com.android.sabsigan.main.user

import ChatListAdapter
import MessageAdapter
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User

object BindingAdapter {
    @BindingAdapter("app:userItems")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: MutableList<User>?) {
        items?.let {
            (recyclerView.adapter as UserListAdapter).setUserList(it)
        }
    }

    @BindingAdapter("app:chatRoomItems")
    @JvmStatic
    fun setChats(recyclerView: RecyclerView, items: List<ChatRoom>?) {
        items?.let {
            (recyclerView.adapter as ChatListAdapter).setChatList(items)
        }
    }

    @BindingAdapter("app:messageItems")
    @JvmStatic
    fun setMessages(recyclerView: RecyclerView, items: List<ChatMessage>?) {
        items?.let {
            (recyclerView.adapter as MessageAdapter).setMessageList(it)
        }
    }
}