package karrel.kr.co.wifidirectsample.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.sabsigan.databinding.FragmentServerBinding
import com.android.sabsigan.wifidirectsample.event.ConnectionInfoEvent
import com.android.sabsigan.wifidirectsample.event.SendMessageEvent
import com.android.sabsigan.wifidirectsample.view.MainActivity3
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.*
import java.util.concurrent.Executors

@SuppressLint("ValidFragment")
class ServerFragment @SuppressLint("ValidFragment") constructor(val info: WifiP2pInfo) : Fragment() {

    private lateinit var binding : FragmentServerBinding

    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter

    private val threadPool = Executors.newFixedThreadPool(1) // 스레드 풀
    private val port = 8988

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentServerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()

        // 서버를 자동으로 시작
        Thread {
            serverSocket = ServerSocket(port)
            clientSocket = serverSocket.accept()
            reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            writer = PrintWriter(OutputStreamWriter(clientSocket.getOutputStream()), true)

            while (true) {
                val message = reader.readLine()
                activity?.runOnUiThread {
                    binding.sendedText.text ="Client: $message\n"
                }
            }
        }.start()

        binding.send.setOnClickListener {
            threadPool.execute {
                val message = binding.sendingText.text.toString()
                writer.println(message)
                binding.sendingText.text.clear()
//            activity?.runOnUiThread { //내가 보낸 텍스트를 텍스트 뷰에 표시하는 부분
//                binding.sendedText.text = "Server: $message\n"
//            }
            }
        }

    }

    private fun setupData() {
        binding.groupOwner.text = "yes"

        Observable.just(info)
                .subscribeOn(Schedulers.io())
                .map { it.groupOwnerAddress.hostName }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // 호스트 이름
                    binding.hostName.text = it
                }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            serverSocket.close()
            clientSocket.close()
            reader.close()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        threadPool.shutdown() // 앱 종료 시 스레드 풀을 종료

    }

//    private fun runServer() {
//        disposable?.dispose()
//        disposable = Observable.just("")
//                .subscribeOn(Schedulers.io())
//                .map {
//                    val serverSocket = ServerSocket(8988)
//                    val client = serverSocket.accept()
//
//
//
//                    val savePath = "${Environment.getExternalStorageDirectory()}/${activity?.packageName}/${System.currentTimeMillis()}.png"
//                    val f = File(savePath)
//
//                    val dirs = File(f.parent)
//                    if (!dirs.exists())
//                        dirs.mkdirs()
//                    f.createNewFile()
//
//                    val inputstream = client.getInputStream()
//                    copyFile(inputstream, FileOutputStream(f))
//                    serverSocket.close()
//                    f
//                }
//                .doOnError {
//                    it.printStackTrace()
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    runServer()
////                    textView.text = "${it.path} 로 파일을 저장하는데 성공하였습니다.\n${Calendar.getInstance().time}}"
//                }
//
//    }
}
