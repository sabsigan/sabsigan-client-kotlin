package com.android.sabsigan.main.chatting

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatRoom

object ChatListBindingAdapter {
    @SuppressLint("NotifyDataSetChanged")
    @BindingAdapter("app:chatRoomItems")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: ArrayList<ChatRoom>?) {
//        if(recyclerView.adapter == null) {
//            val adapter = ChatListAdapter()
//            adapter.setHasStableIds(true)
//            recyclerView.adapter = adapter
//        }

        items?.let {
            val adapter = recyclerView.adapter as ChatListAdapter
            adapter.chatList = items
            adapter.notifyDataSetChanged()
        }
    }
}