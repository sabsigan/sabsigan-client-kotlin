package com.android.sabsigan.main.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.viewModel.UserViewModel
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.AdapterUserListBinding

class UserListAdapter(private val viewModel: UserViewModel): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    lateinit var userList: List<User>

    class UserViewHolder private constructor(val binding: AdapterUserListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: UserViewModel, user: User) {
            binding.user = user
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AdapterUserListBinding.inflate(layoutInflater, parent, false)

                return UserViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(viewModel, userList[position])
    }

    override fun getItemCount() = userList.size
}