package com.android.sabsigan.viewModel

import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.NotificationHelper
import com.android.sabsigan.data.User
import com.android.sabsigan.repository.MainFbRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest

class MainViewModel: WiFiViewModel() {
    private val fbRepository = MainFbRepository(this)

    private val _userList = MutableLiveData<List<User>>()
    private val _chatList = MutableLiveData<List<ChatRoom>>()
    private val _chatRoom = MutableLiveData<ChatRoom>()
    private val clickChatName = MutableLiveData<String>()
    private val _clickUser = MutableLiveData<User?>()

    private val userComparator : Comparator<User> = compareBy { it.name }
    private val chatRoomComparator : Comparator<ChatRoom> = compareByDescending { it.last_message_at }

    val userList: LiveData<List<User>> get() = _userList
    val chatList: LiveData<List<ChatRoom>> get() = _chatList
    val chatRoom: LiveData<ChatRoom> get() = _chatRoom
    val clickUser: LiveData<User?> get() = _clickUser

    val myName = MutableLiveData<String>()
    val myState = MutableLiveData<String>()
    val temp = MutableLiveData<ChatRoom>()

    init {
        viewModelScope.launch {
            val myInfo = fbRepository.getMyInfo()
            myName.value = myInfo?.get("myName")
            myState.value = myInfo?.get("myState")

            _userList.value = fbRepository.getUserList()
            _userList.value = _userList.value!!.sortedWith(userComparator).toMutableList() // 이름 순으로 정렬
            _chatList.value = fbRepository.getChatList()
            _chatList.value = _chatList.value!!.sortedWith(chatRoomComparator).toMutableList() // 시간 순으로 정렬
            fbRepository.getChangeUserList()
            fbRepository.getChangeChatList()
        }
    }

    fun updateMyInfo(nickName: String, state: String, wifi: String) {
        fbRepository.updateUser(nickName, state, wifi)
    }

    fun getUID() = fbRepository.uid

    private fun getOtherUserName(chatRoom: ChatRoom): String {
        // chatRoom에 있는 사용자를 제외한 첫번째 유저 ID
        val otherUserID = chatRoom.users.withIndex()
            .first { fbRepository.uid != it.value }
            .value

        // 유저 ID로 유저 이름 얻어오기
        val user = _userList.value!!.withIndex()
            .first { otherUserID == it.value.id }
            .value
        
        return user.name
    }

    fun addUserList(user: User) {
        val index = userList.value!!.withIndex()
            .firstOrNull  {user.id == it.value.id}
            ?.index?: -1

        if (index == -1) {
            (_userList.value as ArrayList<User>).add(user)  // 이미 있는 유저면

            _userList.value = _userList.value!!.sortedWith(userComparator).toMutableList() // 이름 순으로 정렬
        } else
            modyfyUserList(user)
    }

    fun modyfyUserList(user: User) {
        val index = userList.value!!.withIndex()
            .first {user.id == it.value.id}
            .index

        _userList.value?.get(index)?.name = user.name
        _userList.value?.get(index)?.state = user.state
        _userList.value?.get(index)?.current_wifi = user.current_wifi
        _userList.value?.get(index)?.updated_at = user.updated_at
        _userList.value?.get(index)?.last_active = user.last_active
        _userList.value?.get(index)?.online = user.online

        _userList.value = _userList.value!!.sortedWith(userComparator).toMutableList() // 이름 순으로 정렬
    }

    fun removeUserList(user: User) {
        (_userList.value as ArrayList<User>).remove(user)

        _userList.value = _userList.value!!.sortedWith(userComparator).toMutableList() // 이름 순으로 정렬
        Log.d("userChange", "REMOVED")
    }

    fun addChatList(chatRoom: ChatRoom) {
        val index = chatList.value!!.withIndex()
            .firstOrNull  {chatRoom.id == it.value.id}
            ?.index?: -1

        if (index == -1) {
            (_chatList.value as ArrayList<ChatRoom>).add(chatRoom)  // 이미 있는 유저면
            _chatList.value = _chatList.value!!.sortedWith(chatRoomComparator).toMutableList() // 시간 순으로 정렬
            Log.d("chatRoomChange", "ADDED")
        } else
            modyfyChatList(chatRoom)
    }

    fun modyfyChatList(chatRoom: ChatRoom) {
        val index = chatList.value!!.withIndex()
            .first {chatRoom.id == it.value.id}
            .index

        _chatList.value?.get(index)?.name = chatRoom.name
        _chatList.value?.get(index)?.users = chatRoom.users
        _chatList.value?.get(index)?.updated_at = chatRoom.updated_at
        _chatList.value?.get(index)?.last_message_at = chatRoom.last_message_at
        _chatList.value?.get(index)?.last_message = chatRoom.last_message
        _chatList.value?.get(index)?.member_cnt = chatRoom.member_cnt
        _chatList.value?.get(index)?.disabled = chatRoom.disabled

        _chatList.value = _chatList.value!!.sortedWith(chatRoomComparator).toMutableList() // 시간 순으로 정렬
        Log.d("chatRoomChange", "MODIFIED")
    }

    fun removeChatList(chatRoom: ChatRoom) {
        (_chatList.value as ArrayList<ChatRoom>).remove(chatRoom)

        _chatList.value = _chatList.value!!.sortedWith(chatRoomComparator).toMutableList() // 시간 순으로 정렬
        Log.d("chatRoomChange", "REMOVED")
    }

    fun isTextType(text: String): String {
        val split = text.split(".")

        if (split.size > 1) {
            if (split[1] == "png")
                return "사진"
            else
                return "문서"
        }

        return text
    }

    fun getClickChatName() = clickChatName.value

    /**
     * chatRoomName null이면 상대방 이름
     * @param chatRoomName 사용자가 지정한 이름
     * chatRoom의 사람이 2명일 때만 nullable
     */
    fun getChatRoomName(chatRoom: ChatRoom) = chatRoom.name?: getOtherUserName(chatRoom)

    fun createChat(otherUsers: ArrayList<User>, chatName: String? = null) {
        val users = arrayListOf(fbRepository.uid!!)
        otherUsers.forEach { users.add(it.id) }
        val chatRoomID = customHash(users)
        val chatRoom = isIncluded(chatRoomID)

        if (chatRoom != null) { // 이미 있는 채팅방이면 안 만듦
            clickChatName.value = getChatRoomName(chatRoom)
            _chatRoom.value = chatRoom!!

            return
        }

        viewModelScope.launch {
            val result = fbRepository.createChatRoom(users, chatRoomID, chatName)

            if (result != null) {
                clickChatName.value = getChatRoomName(result)
                _chatRoom.value = result!!
            }
        }
    }

    fun changeChatRoomName(chatRoom: ChatRoom, text: String) {
        fbRepository.changeChatRoomName(chatRoom, text)
    }

    fun exitChatRoom(chatRoom: ChatRoom) {
        fbRepository.exitChatRoom(chatRoom, getUID()!!)
    }
    
    fun clickUser(otherUser: User) {
        Log.d("userFragment", "유저 클릭")

        _clickUser.value = otherUser
    }

    fun setUserNull() {
        _clickUser.value = null
    }

    fun longClickUser(): Boolean {
        Log.d("userFragment", "유저 long클릭")

        return true
    }

    fun clickChatRoom(chatRoom: ChatRoom) {
        Log.d("chatRoomFragment", "채팅방 클릭")

        clickChatName.value = getChatRoomName(chatRoom)
        _chatRoom.value = chatRoom
    }

    fun longClickChatRoom(chatRoom: ChatRoom): Boolean {
        Log.d("chatRoomFragment", "채팅방 long클릭")

        temp.value = chatRoom

        return true
    }

    fun isIncluded(key: String): ChatRoom? {
        val chatRoom = chatList.value!!.withIndex()
            .firstOrNull() { key == it.value.id }
            ?.value

        if (chatRoom != null)
            return chatRoom

        else return null
    }

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
}