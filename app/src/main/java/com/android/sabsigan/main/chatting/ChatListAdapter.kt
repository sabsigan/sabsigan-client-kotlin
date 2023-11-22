package com.android.sabsigan.main.chatting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.AdapterChatListBinding
import com.android.sabsigan.viewModel.MainViewModel

class ChatListAdapter(private val viewModel: MainViewModel): ListAdapter<ChatRoom, ChatListAdapter.ChatListViewHolder>(
    diffUtil
) {
    inner class ChatListViewHolder(val binding: AdapterChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: MainViewModel, chatRoom: ChatRoom) {
            binding.chatRoom = chatRoom
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterChatListBinding.inflate(layoutInflater, parent, false)

        return ChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatRoom>() {
            override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom) =
                oldItem == newItem
        }
    }
}