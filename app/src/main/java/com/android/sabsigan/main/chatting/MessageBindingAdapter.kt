package com.android.sabsigan.main.chatting

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom

object MessageBindingAdapter {
    @SuppressLint("NotifyDataSetChanged")
    @BindingAdapter("app:messageItems")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: ArrayList<ChatMessage>?) {
//        if(recyclerView.adapter == null) {
//            val adapter = ChatListAdapter()
//            adapter.setHasStableIds(true)
//            recyclerView.adapter = adapter
//        }

        items?.let {
            val adapter = recyclerView.adapter as MessageAdapter
            adapter.messageList = items
            adapter.notifyDataSetChanged()
        }
    }
}