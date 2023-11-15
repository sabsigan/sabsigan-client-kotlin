package com.android.sabsigan.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.User

class UserViewModel : WiFiViewModel() {
//    private val _userList = MutableLiveData<ArrayList<User>>()
//    val userList : LiveData<ArrayList<User>> get() = _userList
//
//    init {
//
//    }


    var userList = listOf(
        User(
            id = "아무개1",
            name = "아무개1",
            state = "sssss",
            current_wifi = "current_wifi",
            ),
        User(
            id = "아무개2",
            name = "아무개2",
            state = "sssss",
            current_wifi = "current_wifi",
        ),
        User(
            id = "아무개3",
            name = "아무개3",
            current_wifi = "current_wifi",
        ),
        User(
            id = "아무개4",
            name = "아무개4",
            current_wifi = "current_wifi",
        )
    )

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text


}