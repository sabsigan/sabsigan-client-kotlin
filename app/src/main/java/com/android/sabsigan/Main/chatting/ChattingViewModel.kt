package com.android.sabsigan.Main.chatting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChattingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Chatting Fragment"
    }
    val text: LiveData<String> = _text
}