package com.android.sabsigan.main.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.SimpleUser
import com.android.sabsigan.databinding.AdapterSelectedUserBinding
import com.android.sabsigan.viewModel.CreateChatViewModel

class SelectedUserAdapter(private val viewModel: CreateChatViewModel): RecyclerView.Adapter<SelectedUserAdapter.UserViewHolder>() {
    private var userList = listOf<SimpleUser>()
    class UserViewHolder private constructor(val binding: AdapterSelectedUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: CreateChatViewModel, user: SimpleUser) {
            binding.user = user
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AdapterSelectedUserBinding.inflate(layoutInflater, parent, false)

                return UserViewHolder(binding)
            }
        }
    }

    fun setUserList(list: List<SimpleUser>) {
        userList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(viewModel, userList[position])
    }

    override fun getItemCount() = userList.size
}