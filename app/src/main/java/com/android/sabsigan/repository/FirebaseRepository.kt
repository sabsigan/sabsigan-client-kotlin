package com.android.sabsigan.repository

import android.util.Log
import android.widget.Toast
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class FirebaseRepository() {
    private var auth = Firebase.auth
    private val db = Firebase.firestore
    private val uid = auth.currentUser?.uid

    init {

    }

    private fun getTime(): String {
        val currentTime : Long = System.currentTimeMillis() // ms로 반환
        val dataFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val time = dataFormat.format(currentTime)
        
        return time
    }


    suspend fun getUserList(): ArrayList<User> = withContext(Dispatchers.IO) { // 코틀린코루틴
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
                }
                .addOnFailureListener { exception ->
                    Log.w("getUsers", "Error getting documents: ", exception)
                }

        } catch (exception: Exception) {
            Log.w("getUsers", "Error getting documents: ", exception)
        }

        return@withContext items

        // 데이터 변경 처리
//        query
//            .addSnapshotListener { snapshots, e ->
//                // 오류 발생 시
//                if (e != null) {
//                    Log.w("fff", "Listen failed: $e")
//                    return@addSnapshotListener
//                }
//
//                // 원하지 않는 문서 무시
//                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener
//
//                for (doc in snapshots.documentChanges) {
//                    Log.d("firebase", "${doc.document.id} => ${doc.document.data}")
//
//                    // 문서가 추가될 경우 추가
//                    if (doc.type == DocumentChange.Type.ADDED) {
//                        val user = User(
//                            id = doc.document["id"] as String,
//                            name = doc.document["name"] as String,
//                            state = doc.document["state"] as String,
//                            current_wifi = doc.document["current_wifi"] as String,
//                            created_at = doc.document["created_at"] as String,
//                            updated_at = doc.document["updated_at"] as String,
//                            last_active = doc.document["last_active"] as String,
//                            online = true
//                        )
//
//                        items.add(user)
//                    }
//
//                    // 문서가 수정될 경우 수정 처리
//                    if (doc.type == DocumentChange.Type.MODIFIED) {
//
//                    }
//
//                    // 문서가 삭제될 경우 삭제 처리
//                    if (doc.type == DocumentChange.Type.REMOVED) {
//
//                    }
//                }

//                _userList.value = items
//            }
    }

    suspend fun getChatList(): ArrayList<ChatRoom> = withContext(Dispatchers.IO) {
        val items = ArrayList<ChatRoom>()

        try {
            val chatRoomsRef = db.collection("chatRooms")

            chatRoomsRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("getChatRooms", "${document.id} => ${document.data}")

                        val chatRoom = ChatRoom(
                            id = document["id"] as String,
                            created_by = document["created_by"] as String,
                            created_at = document["created_at"] as String,
                            updated_at = document["updated_at"] as String,
                            last_message_at = document["last_message_at"] as String,
                            member_cnt = (document["member_cnt"] as Long).toInt(),
                            disabled = document["disabled"] as Boolean
                        )

                        items.add(chatRoom)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("getChatRooms", "Error getting documents: ", exception)
                }
        } catch (exception: Exception) {
            Log.w("getChatRooms", "Error getting documents: ", exception)
        }

        return@withContext items
    }

    fun createChatRoom(otherUser: User, cnt: Int): Boolean {
        var value = false
        val time = getTime()
        val chatRef = db.collection("chatRooms")
        val chatList = listOf<User>(
            otherUser,
            User(   // 여기는 datastore로 자기 로컬값 가져오기
                id = uid!!,
                name = "name",
                state = "state",
                current_wifi = "current_wifi",
                created_at = time,
                updated_at = time,
                last_active = time,
                online = true
            )
        )

        val chatRoom = ChatRoom(
            created_by = uid!!,
            created_at = time,
            updated_at = time,
            last_message_at = "",
            member_cnt = cnt,
            disabled = false
        )

        chatRef.add(chatRoom)
            .addOnSuccessListener {
                Log.d("createChat", "DocumentSnapshot written with ID: ${it.id}")
                val query = chatRef.document(it.id).collection("member")

                val updates = hashMapOf<String, Any>(
                    "id" to it.id,
                )

                chatRef.document(it.id).update(updates)
                    .addOnSuccessListener {
                        Log.d("createChat", "DocumentSnapshot written with ID: ${it}")
                    }
                    .addOnFailureListener {e ->
                        Log.w("createChat", "Error adding document", e)
                    }

                for (user in chatList) {
                    query.add(user)
                        .addOnSuccessListener {
                            Log.d("createChat", "DocumentSnapshot written with ID: ${it.id}")
                            value = true
                        }
                        .addOnFailureListener { e ->
                            Log.w("createChat", "Error adding document", e)
                        }
                }
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error adding document", e) }

        return value
    }
}