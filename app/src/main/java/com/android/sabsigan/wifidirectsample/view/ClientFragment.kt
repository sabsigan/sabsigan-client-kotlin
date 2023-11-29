package karrel.kr.co.wifidirectsample.view

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.sabsigan.databinding.FragmentClientBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors

@SuppressLint("ValidFragment")
class ClientFragment @SuppressLint("ValidFragment") constructor(val info: WifiP2pInfo) : Fragment() {

//    private var galleryLoader: GalleryLoader? = null
    private var disposable: Disposable? = null
    private lateinit var binding: FragmentClientBinding

    private val host = info.groupOwnerAddress
    private val port = 8988

    private lateinit var socket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter
    private lateinit var handler: Handler

    private var clientThread: Thread? = null
    private val threadPool = Executors.newFixedThreadPool(1) // 스레드 풀

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentClientBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("host : $host, port : $port")
        setupData()

        handler = Handler(Looper.getMainLooper())

        clientThread = Thread {

            try {
                socket = Socket(host, port)
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                writer = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)

                Log.d("ClientFragment","Connected to Server")
//                handler.post {
                    // 연결 성공 시 UI 업데이트 등 필요한 작업 수행
//                    connectStatusTextView.text = "Connected to Server"
//                }

                // 서버에서 데이터를 계속 읽어오기
                while (true) {
                    val message = reader.readLine() ?: break // 연결이 종료되면 루프 종료
                    handler.post {
                        binding.sendedText.text = "Server: $message\n"
                    }
                }
            } catch (e: Exception) {
                // 연결 실패
//                handler.post {
//                    connectStatusTextView.text = "Connection Failed"
//                }
                Log.d("ClientFragment", "socket Connection Failed")
                e.printStackTrace()
            }
        }

        clientThread?.start()

        binding.send.setOnClickListener {
            // 전송 버튼 클릭 시 메시지 서버로 전송
            threadPool.execute {
                val message = binding.sendingText.text.toString()
                writer.println(message)
                binding.sendingText.text.clear()
                Log.d("ClientFragment", "Message sent to server: $message")
//                handler.post { //내가 보낸 텍스트를 텍스트 뷰에 표시하는 부분
//                    receivedMessagesTextView.append("Client: $message\n")
//                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            socket.close()
            reader.close()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            threadPool.shutdown() // 앱 종료 시 스레드 풀을 종료
            clientThread?.interrupt()
            try {
                clientThread?.join() //스레드 종료까지 기다림
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

    }




    private fun setupData() {
        binding.groupOwner.text = "no"

        Observable.just(info)
                .subscribeOn(Schedulers.io())
                .map { it.groupOwnerAddress.hostName }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // 호스트 이름
                    binding.hostName.text = it
                }
    }


//    private fun sendPictureFile(message: String) {
//        val host = info.groupOwnerAddress
//        val socket = Socket()
//        val port = 8988
//
//        println("fileUri : $uri, host : $host, port : $port")
//
//        disposable?.dispose()
//        disposable = Observable.just(message)
//            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .doOnNext { sendTextToServer(it) }
//            .subscribe()
//
//        try {
//            socket.bind(null)
//            socket.connect(InetSocketAddress(host, port), 5000)
//
//            val outputStream = socket.getOutputStream()
//            val cr = activity?.contentResolver
//            var inputStream: InputStream? = null
//            try {
//                inputStream = cr?.openInputStream(uri)
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            }
//
//
//            val buf = ByteArray(1024)
//
//            val total = File(uri.path).length()
//            var sum: Long = 0
//
//            try {
//                var len = inputStream!!.read(buf)
//                sum += len
//                while (len != -1) {
//                    outputStream.write(buf, 0, len)
//                    len = inputStream.read(buf)
//                    sum += len
//                    SendFilePercentEvent.send(total, sum)
//                    println("copyFile len : $len")
//                }
//                outputStream.close()
//                inputStream.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            if (socket != null) {
//                if (socket.isConnected) {
//                    try {
//                        socket.close()
//                    } catch (e: IOException) {
//                        // Give up
//                        e.printStackTrace()
//                    }
//
//                }
//            }
//        }
//    }
//
//    private val onImageSelectedListener = object : GalleryLoader.OnImageSelectedListener {
//        override fun onImageSelected(uri: Uri) {
//
//
//            disposable = Observable.just(uri)
//                .subscribeOn(Schedulers.io())
//                .doOnNext { sendPictureFile(it) }
//                .subscribe()
//
//            println("onImageSelected : $uri")
//        }
//    }
}
