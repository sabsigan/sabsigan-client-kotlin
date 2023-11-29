package com.android.sabsigan.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.viewModel.MainViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class MainFbRepository(val viewModel: MainViewModel): FirebaseRepository() {

    /**
     * db에 저장된 사용자의 name, state 가져오는 함수
     * */
    suspend fun getMyInfo(): HashMap<String, String>? { // 코루틴 동기 처리
        return try {
            var result: HashMap<String, String>? = null
            val myRef = db.collection("users").document(uid!!).get()

            myRef.addOnSuccessListener {
                    if (it != null) {
                        result = hashMapOf<String, String>(
                            "myName" to  it["name"] as String,
                            "myState" to it["state"] as String,
                        )
                        Log.d("myInfo", it["name"] as String)
                    } else { Log.d("myInfo", "No such document") }
                }
                .addOnFailureListener { Log.d("myInfo", "get failed with ", it) }
                .await()
            result
        } catch (e: FirebaseException) {
            Log.w("myInfo", "Error getting documents: ", e)
            null
        }
    }

    suspend fun getUserList(): List<User> { // 코루틴 동기 처리
        return try {
            var items = ArrayList<User>()

            val usersRef = db.collection("users")
            val query = usersRef
                .whereNotEqualTo("id", uid)
//            .whereEqualTo("online", true)
//            .whereEqualTo("current_wifi", getwifiInfo().toString()) // 같은 와이파이만

            query.get()
                .addOnSuccessListener {
                    it.forEach {
                        Log.d("getUsers", "${it.id} => ${it.data}")

                        val user = User(
                            id = it["id"] as String,
                            name = it["name"] as String,
                            state = it["state"] as String,
                            current_wifi = it["current_wifi"] as String,
                            created_at = it["created_at"] as String,
                            updated_at = it["updated_at"] as String,
                            last_active = it["last_active"] as String,
                            online = true
                        )

                        items.add(user)
                    }
                }
                .addOnFailureListener { Log.w("getUsers", "Error getting documents: ", it) }
                .await()

            items
        } catch (e: FirebaseException) {
            Log.w("getUsers", "Error getting documents: ", e)
            ArrayList<User>()
        }
    }

    suspend fun getChatList(): List<ChatRoom> {
        return try {
            var items = ArrayList<ChatRoom>()

            val chatRoomsRef = db.collection("chatRooms")
            val query = chatRoomsRef
//                .whereEqualTo("current_wifi", viewModel.getwifiInfo())

            query.get()
                .addOnSuccessListener {
                    it.forEach {
                        Log.d("getChatRooms", "${it.id} => ${it.data}")

                        var users = it["users"] as ArrayList<String>

                        var isMine = false
                        for (user in users) {
                            if (user.equals(uid))
                                isMine = true
                        }
                        if (isMine) {

                            val chatRoom = ChatRoom(
                                id = it["id"] as String,
                                name = it["name"] as String?,
                                users = users,
                                created_by = it["created_by"] as String,
                                created_at = it["created_at"] as String,
                                updated_at = it["updated_at"] as String,
                                last_message_at = it["last_message_at"] as String,
                                last_message = it["last_message"] as String,
                                member_cnt = it["member_cnt"] as String,
                                disabled = it["disabled"] as Boolean
                            )

                            items.add(chatRoom)
                        }
                    }
                }
                .addOnFailureListener { Log.w("getUsers", "Error getting documents: ", it) }
                .await()

            items
        } catch (e: FirebaseException) {
            Log.w("getUsers", "Error getting documents: ", e)
            ArrayList<ChatRoom>()
        }
    }

    fun getChangeUserList() {
//        var result = hashMapOf<String, User>()
        val usersRef = db.collection("users")
        val query = usersRef
            .whereNotEqualTo("id", uid)
//            .whereEqualTo("online", true)
//            .whereEqualTo("current_wifi", getwifiInfo().toString()) // 같은 와이파이만

        // 데이터 변경 처리
        query.addSnapshotListener { snapshots, e ->
            // 오류 발생 시
            if (e != null) {
                Log.w("fff", "Listen failed: $e")
                return@addSnapshotListener
            }

            // 원하지 않는 문서 무시
            if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

            snapshots.documentChanges.forEach {
                Log.d("userChange", "${it.document.id} => ${it.document.data}")

                val user = User(
                    id = it.document["id"] as String,
                    name = it.document["name"] as String,
                    state = it.document["state"] as String,
                    current_wifi = it.document["current_wifi"] as String,
                    created_at = it.document["created_at"] as String,
                    updated_at = it.document["updated_at"] as String,
                    last_active = it.document["last_active"] as String,
                    online = true
                )

                // 문서가 추가될 경우 추가 처리
                if (it.type == DocumentChange.Type.ADDED)
                    viewModel.addUserList(user)
                // 문서가 수정될 경우 수정 처리
                else if (it.type == DocumentChange.Type.MODIFIED)
                    viewModel.modyfyUserList(user)
                // 문서가 삭제될 경우 삭제 처리
                else if (it.type == DocumentChange.Type.REMOVED)
                    viewModel.removeUserList(user)


//                // user가 list에 있으면 그 index를 없으면 -1을 index에 저장
//                val index = list.value!!.withIndex()
//                    .firstOrNull  {user.id == it.value.id}
//                    ?.index?: -1
//
//                // 문서가 추가될 경우 추가 처리
//                if (it.type == DocumentChange.Type.ADDED && index == -1) {
//                    Log.d("userChange", "ADDED")
//                    (list.value as ArrayList<User>).add(user)
//
//                    list.value = list.value // 값 변경을 databinding으로 알아차릴 수 있게
//                }
//                // 문서가 수정될 경우 수정 처리
//                else if (it.type == DocumentChange.Type.MODIFIED) {
//                    Log.d("userChange", "MODIFIED")
//                    (list.value as ArrayList<User>).get(index).name = user.name
//                    (list.value as ArrayList<User>).get(index).state = user.state
//                    (list.value as ArrayList<User>).get(index).current_wifi = user.current_wifi
//                    (list.value as ArrayList<User>).get(index).updated_at = user.updated_at
//                    (list.value as ArrayList<User>).get(index).last_active = user.last_active
//                    (list.value as ArrayList<User>).get(index).online = user.online
//
//                    list.value = list.value // 값 변경을 databinding으로 알아차릴 수 있게
//                }
//                // 문서가 삭제될 경우 삭제 처리
//                else if (it.type == DocumentChange.Type.REMOVED) {
//                    (list.value as ArrayList<User>).remove(user)
//                    list.value = list.value // 값 변경을 databinding으로 알아차릴 수 있게
//                }
            }
        }
    }

    fun getChangeChatList() {
//        var result = hashMapOf<String, User>()
        val chatRoomsRef = db.collection("chatRooms")
        val query = chatRoomsRef
//            .whereEqualTo("current_wifi", viewModel.getwifiInfo())

        // 데이터 변경 처리
        query.addSnapshotListener { snapshots, e ->
            // 오류 발생 시
            if (e != null) {
                Log.w("fff", "Listen failed: $e")
                return@addSnapshotListener
            }

            // 원하지 않는 문서 무시
            if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

            snapshots.documentChanges.forEach {
                Log.d("chatRoomsChange", "${it.document.id} => ${it.document.data}")

                var users = it.document["users"] as ArrayList<String>

                var isMine = false
                for (user in users) {
                    if (user.equals(uid))
                        isMine = true
                }
                if (isMine) {
                    val chatRoom = ChatRoom(
                        id = it.document["id"] as String,
                        name = it.document["name"] as String?,
                        users = users,
                        created_by = it.document["created_by"] as String,
                        created_at = it.document["created_at"] as String,
                        updated_at = it.document["updated_at"] as String,
                        last_message_at = it.document["last_message_at"] as String,
                        last_message = it.document["last_message"] as String,
                        member_cnt = it.document["member_cnt"] as String,
                        disabled = it.document["disabled"] as Boolean
                    )

                    // 문서가 추가될 경우 추가 처리
                    if (it.type == DocumentChange.Type.ADDED)
                        viewModel.addChatList(chatRoom)
                    // 문서가 수정될 경우 수정 처리
                    else if (it.type == DocumentChange.Type.MODIFIED)
                        viewModel.modyfyChatList(chatRoom)
                    // 문서가 삭제될 경우 삭제 처리
                    else if (it.type == DocumentChange.Type.REMOVED)
                        viewModel.removeChatList(chatRoom)
                }
            }
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