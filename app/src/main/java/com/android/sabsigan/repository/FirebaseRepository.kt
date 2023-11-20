package com.android.sabsigan.repository

import android.util.Log
import android.widget.Toast
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    fun getUserList(): ArrayList<User> {
        var items = ArrayList<User>()

        val usersRef = db.collection("users")
        val query = usersRef
            .whereNotEqualTo("id", uid)
//            .whereEqualTo("online", true)
//            .whereEqualTo("current_wifi", getwifiInfo().toString()) // 같은 와이파이만

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("firebase", "${document.id} => ${document.data}")

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
                Log.w("firebase", "Error getting documents: ", exception)
            }

        return items

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

    fun getChatList(): ArrayList<ChatRoom> {
        var items = ArrayList<ChatRoom>()

        return items
    }

    fun createChatRoom(otherUser: User, cnt: Int) {
        val time = getTime()
        val chatRoomID = uid + "_" + otherUser.id
        val chatRef = db.collection("chatRooms")
        val query = chatRef.document(chatRoomID)

        val chatRoom = ChatRoom(
            id = chatRoomID,
            created_by = "",
            current_wifi = "",
            created_at = time,
            updated_at = time,
            last_message_at = "",
            member_cnt = cnt,
            disabled = false
        )

    //     val messageRef = db
    // .collection("rooms").document("roomA")
    // .collection("messages").document("message1")

        query.set(chatRoom)
            .addOnSuccessListener {
                query.

            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }
}