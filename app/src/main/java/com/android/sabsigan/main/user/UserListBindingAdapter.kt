package com.android.sabsigan.main.user

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.data.User
import okhttp3.internal.notify

object UserListBindingAdapter {
    @BindingAdapter("app:userItems")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: List<User>?) {
        items?.let {
            (recyclerView.adapter as UserListAdapter).submitList(it)
        }
    }
}