package com.android.sabsigan.main.chatting

import android.util.Log
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class SocketHandler(private val port: Int, private val isServer: Boolean,
                    private val hostAddress: InetAddress) : Runnable {
    private var serverSocket: ServerSocket? = null
    private lateinit var Socket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter
//    private lateinit var hostAddress : InetAddress
    private var isRunning = true
    override fun run() {

        if(isServer) {
            Log.d("SocketHandler", "ServerSocket!!")
            try {
                serverSocket = ServerSocket(port)
                Socket = serverSocket!!.accept()
                if(Socket.isConnected) {
                    Log.d("서버측 Socket: ","연결됨")
                }else{
                    Log.d("서버측 Socket: ","연결안됨")
                }
                reader = BufferedReader(InputStreamReader(Socket.getInputStream()))
//                writer = PrintWriter(OutputStreamWriter(Socket.getOutputStream()), true)

                while (isRunning) {
                    val message = reader.readLine() ?: break // 연결이 종료되면 루프 종료
                    Log.d("receivedmsg: ", message)
//                    onMessageReceived.invoke(message)
                    notifyMessageReceived(message)
                }
            } catch (e: Exception) {
                Log.d("Server", "socket Connection Failed")
                e.printStackTrace()
            }
//            finally {
//                // 스레드가 종료되면 소켓을 닫음
//                closeSocket()
//            }

        }else {
            Log.d("SocketHandler", "Socket!!")
            try {
//                val hostAddress = getOwnerAddress()?.blockingFirst() // Observable을 동기적으로 호출
                hostAddress.hostAddress?.let { Log.d("getOwnerAddress", it) }

                Socket = Socket(hostAddress, port)
                if(Socket.isConnected) {
                    Log.d("클라측 Socket: ","연결됨")
                }else{
                    Log.d("클라측 Socket: ","연결안됨")
                }

                reader = BufferedReader(InputStreamReader(Socket.getInputStream()))
//                writer = PrintWriter(OutputStreamWriter(Socket.getOutputStream()), true)

                Log.d("SocketHandler","Connected to Server")

                // 서버에서 데이터를 계속 읽어오기
                while (isRunning) {
                    val message = reader.readLine() ?: break // 연결이 종료되면 루프 종료
                    Log.d("receivedmsg: ", message)
//                    onMessageReceived.invoke(message)
                    notifyMessageReceived(message) // 받은 텍스트를 리스너를 통해 전달
                }

            } catch (e: Exception) {
                Log.d("Client", "socket Connection Failed")
                e.printStackTrace()
            }
//            finally {
//                // 스레드가 종료되면 소켓을 닫음
//                closeSocket()
//            }

        }
    }

    private var messageListener: MessageListener? = null
    interface MessageListener {
        fun onMessageReceived(message: String)
    }

    fun setMessageListener(listener: ChatActivity) {
        this.messageListener = listener
    }

    private fun notifyMessageReceived(message: String) {
        messageListener?.onMessageReceived(message)
    }





//    fun sendMessage(message: String) {
//        writer.println(message)
//    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    fun sendMessage(message: String) {
//        writer = PrintWriter(OutputStreamWriter(Socket.getOutputStream()), true)
//        Log.d("SocketHandler", "Sending message: $message")
//            writer.println(message)
        try {
            if (!Socket.isClosed) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        writer = PrintWriter(OutputStreamWriter(Socket.getOutputStream()), true)
                        Log.d("SocketHandler", "Sending message: $message")
                        writer.println(message)
                    }
                }
            } else {
                Log.d("SocketHandler", "Socket is closed. Cannot send message.")
            }
        } catch (e: Exception) {
            Log.e("SocketHandler", "Error sending message: $e")
        }
    }




    fun closeSocket() {
        try {
            reader?.close()
            writer?.close()
            serverSocket?.close()
            Socket?.close()
        }catch (e: SocketException){
            e.printStackTrace()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopThread() {
        isRunning = false
    }



}
