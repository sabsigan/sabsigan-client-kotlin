package com.android.sabsigan.repository

import android.util.Log
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.viewModel.ChatViewModel
import com.android.sabsigan.viewModel.MainViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat


class ChatFbRepository(val viewModel: ChatViewModel): FirebaseRepository() {

    suspend fun getMsgList(cid: String): List<ChatMessage> {
        return try {
            var items = ArrayList<ChatMessage>()
            val msgRef = db.collection("chatRooms")
                .document(cid!!)
                .collection("messages")

            msgRef.get()
                .addOnSuccessListener {
                    it.forEach {
                        Log.d("getMsg", "${it.id} => ${it.data}")

                        val chatMessage = ChatMessage(
                            cid = it["cid"] as String,
                            uid = it["uid"] as String,
                            id = it["id"] as String,
                            userName = it["userName"] as String,
                            text = it["text"] as String,
                            type = it["type"] as String?,
                            created_at = it["created_at"] as String,
                            updated_at = it["updated_at"] as String,
                        )

                        items.add(chatMessage)
                    }
                }
                .addOnFailureListener { Log.w("getMsg", "Error getting documents: ", it) }
                .await()

            items
        } catch (e: FirebaseException) {
            Log.w("getMSG", "Error getting documents: ", e)
            ArrayList<ChatMessage>()
        }
    }

    fun getChanggeMsgList(cid: String) {
        val msgRef = db.collection("chatRooms")
            .document(cid!!)
            .collection("messages")

        msgRef.addSnapshotListener { snapshots, e ->
            // 오류 발생 시
            if (e != null) {
                Log.w("fff", "Listen failed: $e")
                return@addSnapshotListener
            }

            // 원하지 않는 문서 무시
            if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

            for (doc in snapshots.documentChanges) {
                Log.d("msgChange", "${doc.document.id} => ${doc.document.data}")

                val chatMessage = ChatMessage(
                    cid = doc.document["cid"] as String,
                    uid = doc.document["uid"] as String,
                    id = doc.document.id,
                    userName = doc.document["userName"] as String,
                    text = doc.document["text"] as String,
                    type = doc.document["type"] as String?,
                    created_at = doc.document["created_at"] as String,
                    updated_at = doc.document["updated_at"] as String,
                )

                // 문서가 추가될 경우 추가
                if (doc.type == DocumentChange.Type.ADDED)
                    viewModel.addMsgList(chatMessage)
                // 문서가 수정될 경우 수정 처리
                else if (doc.type == DocumentChange.Type.MODIFIED)
                    viewModel.modyfyMsgList(chatMessage)
                // 문서가 삭제될 경우 삭제 처리
                else if (doc.type == DocumentChange.Type.REMOVED)
                    viewModel.removeMsgList(chatMessage)
            }
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

                msgRdf.document(it.id).update(hashMapOf<String, Any>("id" to it.id)) // 자동으로 생성된 문서 이름 id로
                    .addOnSuccessListener { Log.d("msg", "DocumentSnapshot written Success") }
                    .addOnFailureListener { Log.w("msg", "Error adding document", it) }

                val update = hashMapOf<String, Any>(
                    "last_message" to message,
                    "last_message_at" to time,
                )

                chatRef.update(update) // chatroom 업데이트
                    .addOnSuccessListener { Log.d("chatRoom", "DocumentSnapshot Success") }
                    .addOnFailureListener { Log.w("chatRoom", "Error adding document", it) }
            }
    }

    fun updateMessage(message: String, cid: String, id: String) {
        val time = getTime()
        val chatRef = db.collection("chatRooms").document(cid)
        val msgRdf = chatRef.collection("messages").document(id)

        val update = hashMapOf<String, Any>(
            "text" to message,
            "updated_at" to time,
        )

        msgRdf.update(update) // msg 업데이트
            .addOnSuccessListener { Log.d("chatRoom", "DocumentSnapshot Success") }
            .addOnFailureListener { Log.w("chatRoom", "Error adding document", it) }
    }

    fun deleteMessage(cid: String, id: String) {
        val time = getTime()
        val chatRef = db.collection("chatRooms").document(cid)
        val msgRdf = chatRef.collection("messages").document(id)

        val update = hashMapOf<String, Any?>(
            "type" to null,
            "updated_at" to time,
        )

        msgRdf.update(update) // msg 업데이트
            .addOnSuccessListener { Log.d("chatRoom", "DocumentSnapshot Success") }
            .addOnFailureListener { Log.w("chatRoom", "Error adding document", it) }
    }
}