package com.android.sabsigan.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.SimpleUser
import com.android.sabsigan.data.User

class CreateChatViewModel: WiFiViewModel() {
    private val _userList = MutableLiveData<List<SimpleUser>>()
    private val _selectedList = MutableLiveData<List<SimpleUser>>()

    val userList: LiveData<List<SimpleUser>> get() = _userList
    val selectedList: LiveData<List<SimpleUser>> get() = _selectedList

    fun setUserList(list: List<User>) {
        // User 리스트를 SimpleUser 리스트로
        _userList.value = list.map { user -> SimpleUser(id = user.id, name = user.name) }
//        _searchedList.value = userList
//        _selectedList.value = listOf<User>()
    }

    fun clickUser(user: SimpleUser) {
        val index = userList.value!!.withIndex()
            .first  {user.id == it.value.id}
            .index
//        val sIndex = _searchedList.value!!.withIndex()
//            .first  {user.id == it.value.id}
//            .index

        userList.value!!.get(index).checked = !userList.value!!.get(index).checked
//        _searchedList.value!!.get(sIndex).checked = !_searchedList.value!!.get(sIndex).checked
//        _searchedList.value = _searchedList.value

        // _selectedList를 ArrayList로 형변환 해서 사용하기 위한 temp
        // _selectedList가 비어있을 때도 사용할 수 있도록 _selectedList.value as ArrayList 사용X
        val temp = arrayListOf<SimpleUser>()
        _selectedList.value?.forEach { temp.add(it) }

        // 기존에 있으면 그 index, 없으면 -1
        val tIndex = temp.withIndex()
            .firstOrNull  {user.id == it.value.id}
            ?.index?: -1

        if (tIndex == -1)
            temp.add(user)
        else
            temp.remove(user)

        _selectedList.value = temp

//        Log.d("test", _searchedList.value.toString())
    }
}