package com.android.sabsigan.repository

import android.util.Log
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.viewModel.ChatViewModel
import com.android.sabsigan.viewModel.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat


class ChatFbRepository(val viewModel: ChatViewModel): FirebaseRepository() {
    init {

    }

    fun setOtherName(otherUserID: String) {
        val userRef = db.collection("users").document(otherUserID)
        userRef.get()
            .addOnSuccessListener {
                viewModel.setOtherName(it["name"] as String)
            }
            .addOnFailureListener { Log.w("getUser", "Error getting documents: ", it) }
    }

    fun setMessageList() {
        val items = ArrayList<ChatMessage>()
        val cid = viewModel.getChatID()

        try {
            val messageRef = db.collection("chatRooms").document(cid!!).collection("messages")

            messageRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("getChatRooms", "${document.id} => ${document.data}")

                        val chatMessage = ChatMessage(
                            cid = document["cid"] as String,
                            uid = document["uid"] as String,
                            id = document["id"] as String,
                            userName = document["userName"] as String,
                            text = document["text"] as String,
                            type = document["type"] as String,
                            created_at = document["created_at"] as String,
                            updated_at = document["updated_at"] as String,
                        )

                        items.add(chatMessage)
                    }
                    viewModel.setMessageList(items)
                }
                .addOnFailureListener { exception ->
                    Log.w("getChatRooms", "Error getting documents: ", exception)
                }


            messageRef.addSnapshotListener { snapshots, e ->
                // 오류 발생 시
                if (e != null) {
                    Log.w("fff", "Listen failed: $e")
                    return@addSnapshotListener
                }

                // 원하지 않는 문서 무시
                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

                var cnt = 0
                for (doc in snapshots.documentChanges) {
                    Log.d("firebase", "${doc.document.id} => ${doc.document.data}")

                    val chatMessage = ChatMessage(
                        cid = doc.document["cid"] as String,
                        uid = doc.document["uid"] as String,
                        id = doc.document["id"] as String,
                        userName = doc.document["userName"] as String,
                        text = doc.document["text"] as String,
                        type = doc.document["type"] as String,
                        created_at = doc.document["created_at"] as String,
                        updated_at = doc.document["updated_at"] as String,
                    )

                    // 문서가 추가될 경우 추가
                    if (doc.type == DocumentChange.Type.ADDED)
                        items.add(chatMessage)

                    // 문서가 수정될 경우 수정 처리
                    if (doc.type == DocumentChange.Type.MODIFIED) {
                        items.get(cnt).text = chatMessage.text
                        items.get(cnt).updated_at = chatMessage.updated_at
                    }

                    // 문서가 삭제될 경우 삭제 처리
                    if (doc.type == DocumentChange.Type.REMOVED) {
                    }
                    cnt++
                }
                viewModel.setMessageList(items)
            }
        } catch (exception: Exception) {
            Log.w("getChatRooms", "Error getting documents: ", exception)
        }
    }

    fun sendMessage(message: String, cid: String, name: String) {
        val time = getTime()
        val chatRef = db.collection("chatRooms").document(cid)
        val msgRdf = chatRef.collection("messages")
        val chatMessage = ChatMessage(
            cid = cid,
            uid = uid!!,
            userName = name,
            text = message,
            type = "msg",
            created_at = time,
            updated_at = time,
        )

        msgRdf.add(chatMessage)
            .addOnSuccessListener {
                Log.d("msg", "DocumentSnapshot written with ID: ${it.id}")

                val update1 = hashMapOf<String, Any>(
                    "last_message" to message,
                    "last_message_at" to time,
                )

                val update2 = hashMapOf<String, Any>(
                    "id" to it.id,
                )

                chatRef.update(update1)
                    .addOnSuccessListener {
                        Log.d("chatRoom", "DocumentSnapshot Success")
                    }
                    .addOnFailureListener {e ->
                        Log.w("chatRoom", "Error adding document", e)
                    }

                msgRdf.document(it.id).update(update2)
                    .addOnSuccessListener {
                        Log.d("msg", "DocumentSnapshot written Success")
                    }
                    .addOnFailureListener {e ->
                        Log.w("msg", "Error adding document", e)
                    }
            }
    }
}