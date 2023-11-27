package com.android.sabsigan.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.viewModel.MainViewModel
import com.google.firebase.firestore.DocumentChange
import java.security.MessageDigest

class MainFbRepository(val viewModel: MainViewModel): FirebaseRepository() {
    init {
        setMyInfo()
        setUserList()
        setChatList()
    }

    /**
     * db에 저장된 사용자의 name, state 가져오는 함수
     * */
    private fun setMyInfo() {
        val myRef = db.collection("users").document(uid!!)
        myRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("myInfo", "DocumentSnapshot data: ${document.data}")
                    viewModel.setUserInfo(document["name"] as String, document["state"] as String)
                } else { Log.d("myInfo", "No such document") }
            }
            .addOnFailureListener { Log.d("myInfo", "get failed with ", it) }
    }

    private fun setUserList() {
        var items = ArrayList<User>()

        try {
            val usersRef = db.collection("users")
            val query = usersRef
                .whereNotEqualTo("id", uid)
//            .whereEqualTo("online", true)
//            .whereEqualTo("current_wifi", getwifiInfo().toString()) // 같은 와이파이만

            query.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("getUsers", "${document.id} => ${document.data}")

                        val user = User(
                            id = document["id"] as String,
                            name = document["name"] as String,
                            state = document["state"] as String,
                            current_wifi = document["current_wifi"] as String,
                            created_at = document["created_at"] as String,
                            updated_at = document["updated_at"] as String,
                            last_active = document["last_active"] as String,
                            online = true
                        )

                        items.add(user)
                    }

                    viewModel.setUserList(items)
                }
                .addOnFailureListener { Log.w("getUsers", "Error getting documents: ", it) }

            // 데이터 변경 처리
            query.addSnapshotListener { snapshots, e ->
                // 오류 발생 시
                if (e != null) {
                    Log.w("fff", "Listen failed: $e")
                    return@addSnapshotListener
                }

                // 원하지 않는 문서 무시
                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

                for (doc in snapshots.documentChanges) {
                    Log.d("userChange", "${doc.document.id} => ${doc.document.data}")

                    val user = User(
                        id = doc.document["id"] as String,
                        name = doc.document["name"] as String,
                        state = doc.document["state"] as String,
                        current_wifi = doc.document["current_wifi"] as String,
                        created_at = doc.document["created_at"] as String,
                        updated_at = doc.document["updated_at"] as String,
                        last_active = doc.document["last_active"] as String,
                        online = true
                    )

                    // 문서가 추가될 경우 추가 처리
                    if (doc.type == DocumentChange.Type.ADDED)
                        viewModel.addUserList(user)
                    // 문서가 수정될 경우 수정 처리
                    else if (doc.type == DocumentChange.Type.MODIFIED)
                        viewModel.modyfyUserList(user)
                    // 문서가 삭제될 경우 삭제 처리
                    else if (doc.type == DocumentChange.Type.REMOVED)
                        viewModel.removeUserList(user)
                }
            }
        } catch (exception: Exception) { Log.w("getUsers", "Error getting documents: ", exception) }
    }

    private fun setChatList() {
        val items = ArrayList<ChatRoom>()

        try {
            val chatRoomsRef = db.collection("chatRooms")
            val query = chatRoomsRef
                .whereEqualTo("current_wifi", viewModel.getwifiInfo())

            chatRoomsRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("getChatRooms", "${document.id} => ${document.data}")

                        var users = document["users"] as ArrayList<String>

                        var isMine = false
                        for (user in users) {
                            if (user.equals(uid))
                                isMine = true
                        }
                        if (!isMine) continue

                        val chatRoom = ChatRoom(
                            id = document["id"] as String,
                            name = document["name"] as String?,
                            users = users,
                            created_by = document["created_by"] as String,
                            created_at = document["created_at"] as String,
                            updated_at = document["updated_at"] as String,
                            last_message_at = document["last_message_at"] as String,
                            last_message = document["last_message"] as String,
                            member_cnt = document["member_cnt"] as String,
                            disabled = document["disabled"] as Boolean
                        )

                        items.add(chatRoom)
                    }
                    viewModel.setChatList(items)
                }
                .addOnFailureListener { exception ->
                    Log.w("getChatRooms", "Error getting documents: ", exception)
                }

            chatRoomsRef.addSnapshotListener { snapshots, e ->
                // 오류 발생 시
                if (e != null) {
                    Log.w("chatRoomsChange", "Listen failed: $e")
                    return@addSnapshotListener
                }

                // 원하지 않는 문서 무시
                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

                for (doc in snapshots.documentChanges) {
                    Log.d("chatRoomsChange", "${doc.document.id} => ${doc.document.data}")

                    var users = doc.document["users"] as ArrayList<String>

                    var isMine = false
                    for (user in users) {
                        if (user.equals(uid))
                            isMine = true
                    }
                    if (!isMine) continue

                    val chatRoom = ChatRoom(
                        id = doc.document["id"] as String,
                        name = doc.document["name"] as String?,
                        users = users,
                        created_by = doc.document["created_by"] as String,
                        created_at = doc.document["created_at"] as String,
                        updated_at = doc.document["updated_at"] as String,
                        last_message_at = doc.document["last_message_at"] as String,
                        last_message = doc.document["last_message"] as String,
                        member_cnt = doc.document["member_cnt"] as String,
                        disabled = doc.document["disabled"] as Boolean
                    )

                    // 문서가 추가될 경우 추가 처리
                    if (doc.type == DocumentChange.Type.ADDED)
                        viewModel.addChatList(chatRoom)
                    // 문서가 수정될 경우 수정 처리
                    else if (doc.type == DocumentChange.Type.MODIFIED)
                        viewModel.modyfyChatList(chatRoom)
                    // 문서가 삭제될 경우 삭제 처리
                    else if (doc.type == DocumentChange.Type.REMOVED)
                        viewModel.removeChatList(chatRoom)
                }
            }
        } catch (exception: Exception) {
            Log.w("getChatRooms", "Error getting documents: ", exception)
        }
    }

    /**
     * chatRoom document 생성용 해시 함수
     * @param ids 해시값 계산을 위한 문자열 리스트
     * @return String 해시 값
     * */
    fun customHash(ids: ArrayList<String>) : String {
        // 두 문자열을 정렬하여 순서에 상관없이 같은 문자열을 생성
        val sortedStrings = ids.sorted()
        // 정렬된 문자열을 이용하여 고유한 해시 값을 생성

        var combinedString = ""
        sortedStrings.forEach { combinedString += it}
        combinedString += viewModel.getwifiInfo().value // 채팅방에 포함된 유저 id와 wifi 이름으로 해시키 생성

        // SHA-256 해시 함수 사용
        val bytes = MessageDigest.getInstance("SHA-256").digest(combinedString.toByteArray())

        // 바이트 배열의 2/3 사용하여 16진수 문자열로 변환하여 반환
        val halfLength = (bytes.size / 3) * 2
        val halfBytes = bytes.copyOfRange(0, halfLength)

        // 바이트 배열을 16진수 문자열로 변환하여 반환
        return halfBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * 채팅방 생성 메서드
     * 
     * @param otherUsers 자신을 제외한 채팅방 유저들
     * @param cname 채팅방 이름, otherUsers.size가 1일 때만 nullable
     */
    fun createChatRoom(otherUsers: ArrayList<User>, cname: String?) {
        val users = arrayListOf(uid!!)
        otherUsers.forEach { users.add(it.id) }
        val chatRoomID = customHash(users)

        if (viewModel.isIncluded(chatRoomID)) // 이미 있는 채팅방이면 안 만듦
            return

        val chatRef = db.collection("chatRooms").document(chatRoomID)
        val time = getTime()
        val chatRoom = ChatRoom(
            id = chatRoomID,
            created_by = uid!!,
            name = cname,
            users = users,
            current_wifi = viewModel.getwifiInfo().value!!,
            created_at = time,
            updated_at = time,
            last_message_at = time,
            member_cnt = (otherUsers.size + 1).toString(),
        )

        chatRef.set(chatRoom)
            .addOnSuccessListener { Log.d("createChat", "DocumentSnapshot Success") }
            .addOnFailureListener { Log.w("TAG", "Error adding document", it) }
    }
}