package karrel.kr.co.wifidirectsample.view

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.os.Environment
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
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*

@SuppressLint("ValidFragment")
class ServerFragment @SuppressLint("ValidFragment") constructor(val info: WifiP2pInfo) : Fragment() {

    private var disposable: Disposable? = null
    private lateinit var binding : FragmentServerBinding

    var messageToSend: String? = null
    private lateinit var serverSocket : ServerSocket
    private lateinit var socket : Socket
    private val port = 8988

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        runServer()

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
        disposable?.dispose()
    }

    private fun runServer()  {

        // 기존에 존재하는 disposable을 폐기합니다.
        disposable?.dispose()

        // 빈 문자열을 가진 초기 값을 갖는 Observable을 생성
        disposable = Observable.just("")
            // Observable이 작업을 수행할 쓰레드를 지정(이 경우 IO 쓰레드).
            .subscribeOn(Schedulers.io())
            // 제공된 람다를 사용하여 방출된 항목(빈 문자열)을 새 항목으로 매핑
            .map {
                // 매핑 블록 내부

                // 8988 포트에서 ServerSocket을 생성
                serverSocket = ServerSocket(port)
                // 클라이언트 연결을 수락
                socket = serverSocket.accept()
                // 클라이언트에서 데이터를 읽을 BufferedReader를 생성
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // BufferedReader를 사용하여 클라이언트에서 전송된 텍스트를 읽습니다.
                val receivedText = reader.readLine()

                // ServerSocket을 닫습니다.
                serverSocket.close()

                // 수신된 텍스트를 반환
                receivedText
            }
            // Observable 체인에서 발생하는 모든 오류를 처리
            .doOnError {
                it.printStackTrace()
            }
            // Observer(구독자)가 작업을 수행할 쓰레드를 지정 (이 경우 메인 쓰레드).
            .observeOn(AndroidSchedulers.mainThread())
            // Observable에 구독하고, 발행된 항목을 처리할 람다를 제공
            .subscribe { receivedText ->
                // 구독 블록 내부

                // TextView에 수신된 텍스트를 설정
                binding.sendedText.text = receivedText.toString()

                // runServer()를 재귀적으로 호출하여 서버를 계속 실행
                runServer()
            }

    }

    private fun sendTextToServer(message: String) {
//        val host = info.groupOwnerAddress
//        val socket = Socket()
//        val port = 8988

        println("host : $host, port : $port")

        disposable?.dispose()

        disposable = Observable.just(message)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnNext {
                try {
//                    socket.bind(null)
//                    socket.connect(InetSocketAddress(host, port), 5000)

                    val outputStream = socket.getOutputStream()
                    val writer = BufferedWriter(OutputStreamWriter(outputStream))

                    // 서버로 전송할 텍스트 전달
                    writer.write(message)
                    writer.newLine()
                    writer.flush()

                    //TODO 여기서는 서버로부터의 응답을 받는 코드 추가

                    outputStream.close()
                    writer.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (socket.isConnected) {
                        try {
                            socket.close()
                        } catch (e: IOException) {
                            // Give up
                            e.printStackTrace()
                        }
                    }
                }
            }
            .subscribe()
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
