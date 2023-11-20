package com.android.sabsigan.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sabsigan.viewModel.WifiSelectorViewModel
import com.android.sabsigan.databinding.FragmentWifiListBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WifiListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WifiListFragment : Fragment() {
    private var mBinding: FragmentWifiListBinding? = null
    private val binding get() = mBinding!!
    private val viewModel by activityViewModels<WifiSelectorViewModel>()

    private var currentBSSID: String? = null
    private var mWifiList: MutableList<ScanResult>? = null
    private val wifiList get() = mWifiList
    private var adapter: WifiListAdapter? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WifiListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WifiListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

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
        mBinding = FragmentWifiListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { getWifiList(it) }
        setRecyclerView()

        viewModel.getwifiInfo().observe(requireActivity(), Observer {
            Log.d("WifiListFragment!!", viewModel.getwifiInfo().value!!)

            currentBSSID = viewModel.getBSSID().value
            context?.let { it1 -> getWifiList(it1) }
        })
    }

    override fun onResume() {
        super.onResume()

        context?.let { getWifiList(it) }
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    @SuppressLint("MissingPermission")
    private fun getWifiList(context: Context) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val scanResults = wifiManager.scanResults
        val comparator : Comparator<ScanResult> = compareByDescending { it.level }

        mWifiList = scanResults.sortedWith(comparator).toMutableList()
        var temp: ScanResult? = null
        // ssid 기준으로 중복 제거
        Log.w("WifiListFragment", "------------------------")
        for (scanResult in wifiList!!) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.w("WifiListFragment", "ssid: ${scanResult.wifiSsid}")
//                Log.w("WifiListFragment", "apMldMacAddress: ${scanResult.}")
            }
            else
                Log.w("WifiListFragment", "ssid: ${scanResult.SSID}")
            Log.w("WifiListFragment", "bssid: ${scanResult.BSSID}")
            Log.w("WifiListFragment", "level: ${scanResult.level}")
            Log.w("WifiListFragment", "capabilities: ${scanResult.capabilities}")
            Log.w("WifiListFragment", "hashCode: ${scanResult.hashCode()}")


            if (scanResult.BSSID == currentBSSID) {
                Log.w("WifiListFragment", "현재 연결된 Wi-Fi")
                temp = scanResult
                // break;
            }
            Log.w("WifiListFragment", "------------------------")
        }

        if (temp != null) {
            wifiList!!.remove(temp)
            wifiList!!.add(0, temp)
        }

        if (adapter == null) {
            adapter = WifiListAdapter(context, wifiList!!, currentBSSID ?: "")
        } else {
            adapter!!.wifiList = wifiList as MutableList<ScanResult>
            adapter!!.cBSSID = currentBSSID ?: ""
            adapter!!.notifyDataSetChanged()
        }
    }
}