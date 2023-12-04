package com.android.sabsigan.main.chatting

import android.util.Log
import com.android.sabsigan.wifidirectsample.event.ConnectionInfoEvent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class SocketHandler(private val port: Int, private val isServer: Boolean,
                    private val hostAddress: InetAddress, private val onMessageReceived: (String) -> Unit) : Runnable {
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
                writer = PrintWriter(OutputStreamWriter(Socket.getOutputStream()), true)

                while (isRunning) {
                    val message = reader.readLine() ?: break // 연결이 종료되면 루프 종료
                    Log.d("receivedmsg: ", message)
                    onMessageReceived.invoke(message)
                }
            } catch (e: Exception) {
                Log.d("Server", "socket Connection Failed")
                e.printStackTrace()
            } finally {
                // 스레드가 종료되면 소켓을 닫음
                closeSocket()
            }

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
                writer = PrintWriter(OutputStreamWriter(Socket.getOutputStream()), true)

                Log.d("SocketHandler","Connected to Server")

                // 서버에서 데이터를 계속 읽어오기
                while (isRunning) {
                    val message = reader.readLine() ?: break // 연결이 종료되면 루프 종료
                    Log.d("receivedmsg: ", message)
                    onMessageReceived.invoke(message)
                }

            } catch (e: Exception) {
                Log.d("Client", "socket Connection Failed")
                e.printStackTrace()
            } finally {
                // 스레드가 종료되면 소켓을 닫음
                closeSocket()
            }

        }
    }

//    fun sendMessage(message: String) {
//        writer.println(message)
//    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun sendMessage(message: String) {
        Log.d("SocketHandler", "Sending message: $message")
        coroutineScope.launch {
            writer.println(message)
        }
    }

//    private fun getOwnerAddress(): Observable<InetAddress>? {
//        return ConnectionInfoEvent.receive()
//            .subscribeOn(Schedulers.io())
//            .filter { it.groupFormed }
//            .map { it.groupOwnerAddress }
//            .doOnNext{
//                hostAddress = it
//                Log.d("getOwnerAddress: ","$hostAddress")
//            }
//    }

//    private fun getOwnerAddress() {
//        Observable.just("")
//            .subscribeOn(Schedulers.io())
//            .map { it.groupOwnerAddress.hostName }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                // 호스트 이름
//                binding.hostName.text = it
//            }
//    }


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
