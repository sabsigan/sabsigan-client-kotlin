package karrel.kr.co.wifidirectsample.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sabsigan.databinding.FragmentDiscoverBinding
import com.android.sabsigan.wifidirectsample.event.PeerListEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class DiscoverFragment : Fragment() {

    private val peerAdapter = PeerAdapter()

    private lateinit var disposiable : Disposable
    private lateinit var binding: FragmentDiscoverBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentDiscoverBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPeerListView()
        setupBroadCastEvent()
    }

    private fun setupBroadCastEvent() {
        // 탐색된 디자이스 리스트
        disposiable = PeerListEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            println("MainActivity : ${it.deviceList.size}")
            peerAdapter.setData(it.deviceList)

            for (i in it.deviceList) {
                println("검색된 기기 : ${i.deviceName}")
            }
        }
    }

    private fun setupPeerListView() {
        binding.searchedList.layoutManager = LinearLayoutManager(requireContext())
        binding.searchedList.adapter = peerAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        disposiable?.dispose()
    }
}
