package karrel.kr.co.wifidirectsample.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.android.sabsigan.databinding.FragmentServerBinding
import com.android.sabsigan.wifidirectsample.MessageType
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.Executors

@SuppressLint("ValidFragment")
class ServerFragment @SuppressLint("ValidFragment") constructor(val info: WifiP2pInfo) : Fragment() {

    private lateinit var binding : FragmentServerBinding

    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter

    private var serverThread: Thread? = null
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
        serverThread = Thread {

            try {
                serverSocket = ServerSocket(port)
                clientSocket = serverSocket.accept()
                reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                writer = PrintWriter(OutputStreamWriter(clientSocket.getOutputStream()), true)

                while (true) {
                    val message = reader.readLine() ?: break // 연결이 종료되면 루프 종료
                    activity?.runOnUiThread {
                        binding.sendedText.text ="Client: $message\n"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        serverThread?.start()

        binding.send.setOnClickListener {
            threadPool.execute {
                val messageType = MessageType.TEXT
                val message = binding.sendingText.text.toString()

//                writer.println(messageType.name)

                writer.println(message)
                binding.sendingText.text.clear()
                Log.d("ServerFragment", "Message sent to client: $message")

//              activity?.runOnUiThread { //내가 보낸 텍스트를 텍스트 뷰에 표시하는 부분
//                  binding.sendedText.text = "Server: $message\n"
//              }
            }
        }

//        binding.btnImg.setOnClickListener {
//            threadPool.execute {
//                val messageType = MessageType.IMAGE
//
//                // 이미지 파일을 바이트 배열로 변환하여 전송
//                val imageFile = File("path/to/image.jpg")
////                val imageBytes = Files.readAllBytes(imageFile.toPath())
////                clientSocket.getOutputStream().write(imageBytes, 0, imageBytes.size)
//
//                Log.d("ServerFragment", "Image sent to client")
//            }
//        }

        binding.btnImg.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }




    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {

        if(it.resultCode == RESULT_OK && it.data != null) {
            val url = it.data!!.data
            val inputStream = requireContext().contentResolver.openInputStream(url!!)
            val imageBytes = inputStream?.readBytes()

            // 이미지 데이터를 서버로 전송
            threadPool.execute {
                val messageType = MessageType.IMAGE
                writer.println(messageType.name)

                // 이미지 데이터를 서버로 전송
                imageBytes?.let {
                    clientSocket.getOutputStream().write(it, 0, it.size)
                    Log.d("ClientActivity", "Image sent to server")
                }
            }

            Glide.with(this)
                .load(url)
                .into(binding.imgview)
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
        }catch (e: SocketException){
            e.printStackTrace()
        }catch (e: Exception) {
            e.printStackTrace()
        }finally {
            // 스레드 풀 종료
            threadPool.shutdown()

            // 서버 스레드 중지
            serverThread?.interrupt()
            try {
                serverThread?.join() //스레드 종료까지 기다림
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }


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
