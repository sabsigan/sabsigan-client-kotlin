package com.android.sabsigan.beta

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.android.sabsigan.R
import com.android.sabsigan.ViewModel.WiFiViewModel
import com.android.sabsigan.databinding.FragmentWifiInfoBinding
import com.android.sabsigan.databinding.FragmentWifiListBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WifiInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WifiInfoFragment : Fragment() {
    private var mBinding: FragmentWifiInfoBinding? = null
    private val binding get() = mBinding!!
    private val viewModel by activityViewModels<WiFiViewModel>() //뷰모델 생성

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var info: String? = null

    private val wifiStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                // 와이파이 상태 변경 액션을 받았을 때
                val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val ssid = wifiInfo.ssid
                val bssid = wifiInfo.bssid
                val linkSpeed = wifiInfo.linkSpeed

//                Log.d("NETWORK_STATE_CHANGED!!",ssid)

                val message = "현재 연결된 와이파이: $ssid\n bssid: $bssid\n 전송속도: $linkSpeed"
                binding.tvName1.text = message

            } else if (intent?.action == "wifi.ACTION_WIFI_OFF") {
                // 와이파이가 꺼진 액션을 받았을 때 처리할 작업
                binding.tvName1.text = "와이파이가 꺼졌습니다."
                //TODO 와이파이 설정 페이지로 이동하게 할까
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wifiStateFilter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        wifiStateFilter.addAction("wifi.ACTION_WIFI_OFF")
        context?.registerReceiver(wifiStateReceiver, wifiStateFilter)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentWifiInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WifiInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WifiInfoFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.unregisterReceiver(wifiStateReceiver)
    }

}