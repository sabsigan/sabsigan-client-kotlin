package com.android.sabsigan.main.chatting

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatRoom

object ChatListBindingAdapter {
    @SuppressLint("NotifyDataSetChanged")
    @BindingAdapter("app:chatRoomItems")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: List<ChatRoom>?) {
        items?.let {
            (recyclerView.adapter as ChatListAdapter).submitList(it)
        }
    }
}