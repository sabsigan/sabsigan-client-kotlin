package com.android.sabsigan.main.chatting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.databinding.AdapterChatListBinding
import com.android.sabsigan.viewModel.MainViewModel

class ChatListAdapter(private val viewModel: MainViewModel): RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {
    var chatList = arrayListOf<ChatRoom>()

    class ChatListViewHolder private constructor(val binding: AdapterChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: MainViewModel, chatRoom: ChatRoom) {
            binding.chatRoom = chatRoom
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ChatListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AdapterChatListBinding.inflate(layoutInflater, parent, false)

                return ChatListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(viewModel, chatList[position])
    }

    override fun getItemCount() = chatList.size
}