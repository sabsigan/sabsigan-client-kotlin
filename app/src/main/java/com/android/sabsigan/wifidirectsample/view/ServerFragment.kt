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
import java.net.ServerSocket
import java.util.*

@SuppressLint("ValidFragment")
class ServerFragment @SuppressLint("ValidFragment") constructor(val info: WifiP2pInfo) : Fragment() {

    private var disposable: Disposable? = null
    private lateinit var binding : FragmentServerBinding

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

    private fun runServer() {
        disposable?.dispose()
        disposable = Observable.create<String> { emitter ->
            try {
                val serverSocket = ServerSocket(8988)
                val client = serverSocket.accept()

                val inputstream = client.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputstream))
                val receivedText = reader.readLine()

                // 여기서 receivedText를 활용하여 필요한 작업을 수행할 수 있습니다.
                // 예를 들어, UI 업데이트 또는 다른 비즈니스 로직을 수행할 수 있습니다.
                // 이 예제에서는 받은 텍스트를 onNext로 emit 합니다.
                emitter.onNext(receivedText)

                // 클라이언트에게 메시지를 보내기 위해 OutputStream을 사용합니다.
                val outputStream = client.getOutputStream()
                val writer = BufferedWriter(OutputStreamWriter(outputStream))
                val messageToSend = "Hello from server!"
                writer.write(messageToSend)
                writer.newLine()
                writer.flush()

                serverSocket.close()
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { receivedText ->
                    // TODO: 받은 텍스트를 활용하여 UI 업데이트 또는 다른 작업 수행
                    binding.textView.text = "Received Text: $receivedText"
                    runServer() // 서버를 계속 실행할지 여부를 결정하고, 필요에 따라 제거할 수 있습니다.
                },
                { error ->
                    error.printStackTrace()
                }
            )
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
