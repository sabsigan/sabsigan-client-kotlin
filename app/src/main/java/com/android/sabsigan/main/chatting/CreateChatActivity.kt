package com.android.sabsigan.main.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.android.sabsigan.R
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.ActivityCreateChatBinding

class CreateChatActivity : AppCompatActivity() {
    private var _binding: ActivityCreateChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_create_chat)
        val userList = intent.getSerializableExtra("userList") as ArrayList<*>


//        val myName = intent.array

//        intent.
    }
}