package com.android.sabsigan.main.chatting

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.AdapterMyMessageBinding
import com.android.sabsigan.databinding.AdapterOtherMessageBinding
import com.android.sabsigan.viewModel.ChatViewModel


class MessageAdapter(private val viewModel: ChatViewModel): ListAdapter<ChatMessage, MessageAdapter.MessageViewHolder>(diffUtil) {
    val TYPE_MY = 0
    val TYPE_OTHER = 1

    class MessageViewHolder(var binding: ViewDataBinding, val viewType: Int) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: ChatViewModel, chatMessage: ChatMessage) {
            if (viewType == 0) {
                (binding as AdapterMyMessageBinding).chatMessage = chatMessage
                (binding as AdapterMyMessageBinding).viewModel = viewModel
                binding.executePendingBindings()
            } else {
                (binding as AdapterOtherMessageBinding).chatMessage = chatMessage
                (binding as AdapterOtherMessageBinding).viewModel = viewModel
                binding.executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(getItem(position).uid.equals(viewModel.getUID())) {
            Log.d("ViewType", "0")
            return TYPE_MY;
        } else {
            Log.d("ViewType", "1")
            return TYPE_OTHER;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == 0) AdapterMyMessageBinding.inflate(layoutInflater, parent, false)
        else  AdapterOtherMessageBinding.inflate(layoutInflater, parent, false)

        return MessageViewHolder(binding, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
                oldItem == newItem
        }
    }
}