package com.android.sabsigan.main.chatting

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.databinding.AdapterMyMessageBinding
import com.android.sabsigan.databinding.AdapterOtherMessageBinding
import com.android.sabsigan.viewModel.ChatViewModel


class MessageAdapter(private val viewModel: ChatViewModel): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    var messageList = arrayListOf<ChatMessage>()

    val TYPE_MY = 0
    val TYPE_OTHER = 1

    class MessageViewHolder private constructor(var binding: ViewDataBinding, val viewType: Int) : RecyclerView.ViewHolder(binding.root) {
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

        companion object {
            fun from(parent: ViewGroup, viewType: Int) : MessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = if (viewType == 0) AdapterMyMessageBinding.inflate(layoutInflater, parent, false)
                                else  AdapterOtherMessageBinding.inflate(layoutInflater, parent, false)

                return MessageViewHolder(binding, viewType)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(messageList[position].uid.equals(viewModel.getUID())) {
            Log.d("ViewType", "0")
            return TYPE_MY;
        } else {
            Log.d("ViewType", "1")
            return TYPE_OTHER;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.from(parent, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(viewModel, messageList[position])
    }

    override fun getItemCount() = messageList.size
}