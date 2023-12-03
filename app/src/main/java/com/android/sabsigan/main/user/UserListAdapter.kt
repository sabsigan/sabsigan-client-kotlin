package com.android.sabsigan.main.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.AdapterUserListBinding
import com.android.sabsigan.viewModel.MainViewModel

class UserListAdapter(private val viewModel: MainViewModel): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    private var userList = listOf<User>()

    class UserViewHolder private constructor(val binding: AdapterUserListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: MainViewModel, user: User) {
            binding.user = user
            binding.viewModel = viewModel
            binding.imageView.setImageBitmap(viewModel.generateAvatar(user.id))
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

    fun setUserList(list: List<User>) {
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