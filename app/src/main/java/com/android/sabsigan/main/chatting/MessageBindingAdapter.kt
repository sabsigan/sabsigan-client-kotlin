package com.android.sabsigan.main.chatting

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom

object MessageBindingAdapter {
    @BindingAdapter("app:messageItems")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: List<ChatMessage>?) {
        items?.let {
            (recyclerView.adapter as MessageAdapter).submitList(it)
        }
    }
}