package com.android.sabsigan.main.user

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.SimpleUser
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.AdapterSelectUserBinding
import com.android.sabsigan.viewModel.CreateChatViewModel

class SelectUserAdapter(private val viewModel: CreateChatViewModel): RecyclerView.Adapter<SelectUserAdapter.UserViewHolder>(), Filterable {
    private var userList = listOf<SimpleUser>()
    var filteredList= ArrayList<SimpleUser>()
    var itemFilter = ItemFilter()

    init {
        filteredList.addAll(userList)
    }

    class UserViewHolder private constructor(val binding: AdapterSelectUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: CreateChatViewModel, user: SimpleUser) {
            binding.user = user
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AdapterSelectUserBinding.inflate(layoutInflater, parent, false)

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
    override fun getFilter() = itemFilter

    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterString = constraint.toString()
            val results = FilterResults()

            //검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList = ArrayList<SimpleUser>()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = userList
                results.count = userList.size

                return results
            } else { //공백제외 -> 이름으로 or 초성 // 초성은 아직X
                for (user in userList) {
                    if (user.name.contains(filterString)) {
                        filteredList.add(user)
                    }
                }
            }
            results.values = filteredList
            results.count = filteredList.size

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        }
    }
}