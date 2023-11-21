package com.android.sabsigan.main.user

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.User

object UserListBindingAdapter {
    @SuppressLint("NotifyDataSetChanged")
    @BindingAdapter("app:userItems")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: ArrayList<User>?) {
//        if(recyclerView.adapter == null) {
//            val adapter = UserListAdapter()
//            adapter.setHasStableIds(true)
//            recyclerView.adapter = adapter
//        }

        items?.let {
            val adapter = recyclerView.adapter as UserListAdapter
            adapter.userList = items
            adapter.notifyDataSetChanged()
        }
    }
}