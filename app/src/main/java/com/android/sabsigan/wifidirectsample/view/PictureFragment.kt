package karrel.kr.co.wifidirectsample.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.sabsigan.databinding.FragmentMusicListBinding
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.Socket

@SuppressLint("ValidFragment")
class PictureFragment @SuppressLint("ValidFragment") constructor(val info: WifiP2pInfo) : Fragment() {

//    private var galleryLoader: GalleryLoader? = null
    private var disposable: Disposable? = null
    private lateinit var binding: FragmentMusicListBinding

    private var message : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()

        setupButtons()

//        setupButtons()
//        setupGalleryLoader()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
    }

    private fun setupGalleryLoader() {
//        galleryLoader = GalleryLoader.Builder(activity!!.applicationContext)
//                .setOnImageSelectedListener(onImageSelectedListener)
//                .create()
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


    private fun setupButtons() {
//        selectPicture.setOnClickListener {
//
//            galleryLoader?.show(activity!!.supportFragmentManager)
//        }
//        binding.send.setOnClickListener{
//            message = if (message?.toString().isNullOrBlank()) " " else binding.sendingText.text.toString()
//            sendTextToServer(message!!)
//        }
        disposable = Observable.create<String> { emitter ->
            sendTextToServer(binding.sendingText.text.toString(), emitter)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { receivedText ->
                    Log.d(TAG, "Received Text from Server: $receivedText")
                    binding.sendedText.text = receivedText
                },
                { error ->
                    error.printStackTrace()
                }
            )


    }


//    private fun startProgressActivity() {
//        activity?.startActivity(Intent(activity, ProgressActivity::class.java))
//    }


    private fun sendTextToServer(text: String, emitter: ObservableEmitter<String>) {
        val host = info.groupOwnerAddress
        val socket = Socket()
        val port = 8988

        try {
            socket.bind(null)
            socket.connect(InetSocketAddress(host, port), 5000)

            val outputStream = socket.getOutputStream()
            val writer = BufferedWriter(OutputStreamWriter(outputStream))

            // 서버로 전송할 텍스트를 전달합니다.
            writer.write(text)
            writer.newLine()
            writer.flush()

            // 서버로부터 메시지를 받는 코드
            val inputStream = socket.getInputStream()
            val reader = BufferedReader(InputStreamReader(inputStream))
            val receivedText = reader.readLine()

            // 받은 메시지를 Observable을 통해 전달
            emitter.onNext(receivedText)

            // 소켓 및 스트림 닫기
            outputStream.close()
            writer.close()
            inputStream.close()
            reader.close()
        } catch (e: IOException) {
            emitter.onError(e)
        } finally {
            if (socket.isConnected) {
                try {
                    socket.close()
                } catch (e: IOException) {
                    emitter.onError(e)
                }
            }
        }
    }



//    private fun sendPictureFile(uri: Uri) {
//        val host = info.groupOwnerAddress
//        val socket = Socket()
//        val port = 8988
//
//        println("fileUri : $uri, host : $host, port : $port")
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
////                    SendFilePercentEvent.send(total, sum)
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
//            startProgressActivity()
//
//            disposable = Observable.just(uri)
//                    .subscribeOn(Schedulers.io())
//                    .doOnNext { sendPictureFile(it) }
//                    .subscribe()
//
//            println("onImageSelected : $uri")
//        }
//    }
}
