package com.android.sabsigan.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.android.sabsigan.viewModel.WifiSelectorViewModel
import com.android.sabsigan.databinding.FragmentWifiInfoBinding

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
    private val viewModel by activityViewModels<WifiSelectorViewModel>() //뷰모델 생성

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var info: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getwifiInfo().observe(requireActivity(), Observer {
            if (it != "mobileInfo.isConnected" && it != "wifiInfo.null") {
                val message = "환영합니다!\n 현재 연결된 와이파이\n $it}\n"
                binding.tvName1.text = message
            } else {
                binding.tvName1.text = "와이파이가 꺼졌습니다\n 와이파이를 연결해주세요"
            }
        })
    }


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
}