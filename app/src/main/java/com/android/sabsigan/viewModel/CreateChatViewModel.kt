package com.android.sabsigan.viewModel

import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.SimpleUser
import com.android.sabsigan.data.User

class CreateChatViewModel: WiFiViewModel() {
    private lateinit var userList: List<SimpleUser>
    private val _searchedList = MutableLiveData<List<SimpleUser>>()
    private val _selectedList = MutableLiveData<List<SimpleUser>>()

    val searchedList: LiveData<List<SimpleUser>> get() = _searchedList
    val selectedList: LiveData<List<SimpleUser>> get() = _selectedList
    val inputTxt = MutableLiveData<String>()

    var temp = false

    fun setUserList(list: List<User>) {
        // User 리스트를 SimpleUser 리스트로
        userList = list.map { user -> SimpleUser(id = user.id, name = user.name) }
        _searchedList.value = userList
    }

    fun isSelectedZero(): Boolean {
        if (selectedList.value!!.size > 0)
            return false

        return true
    }

    fun removeUser(user: SimpleUser) {
        val index = userList.withIndex()
            .first  { user.id == it.value.id }
            .index

        userList.get(index).checked = !userList.get(index).checked
        _searchedList.value = _searchedList.value

        (_selectedList.value as ArrayList<SimpleUser>).remove(user)
        _selectedList.value = _selectedList.value
    }

    fun clickUser(user: SimpleUser) {
        val index = userList.withIndex()
            .first { user.id == it.value.id}
            .index

        userList.get(index).checked = !userList.get(index).checked
        _searchedList.value = _searchedList.value

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
    }

    fun searchUser(search: String?) {
        //검색이 필요없을 경우를 위해 원본 배열을 복제
        val list = ArrayList<SimpleUser>()
        //공백제외 아무런 값이 없을 경우 -> 원본 배열
        if (search?.trim { it <= ' ' }!!.isEmpty()) {
            _searchedList.value = userList

            return
        } else { //공백제외 -> 이름으로 or 초성 // 초성은 아직X
            for (user in userList) {
                if (user.name.contains(search)) {
                    list.add(user)
                }
            }
        }

        _searchedList.value = list
    }
}