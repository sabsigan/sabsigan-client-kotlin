package com.android.sabsigan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sabsigan.databinding.ActivitySetWiFiBinding
import io.reactivex.annotations.NonNull

@SuppressLint("MissingPermission")
class SetWiFiActivity : AppCompatActivity() { //초기화면 1 : 와이파이 확인 및 변경

    private var mBinding: ActivitySetWiFiBinding? = null
    private val binding get() = mBinding!!

    var itemList: ArrayList<AccessPoint>? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var accessPointAdapter: AccessPointerAdapter? = null
    var wifiManager: WifiManager? = null
    var scanResult: List<ScanResult>? = null
//    var binding: ActivitySetWiFiBinding? = null
    val MULTIPLE_PERMISSIONS = 10 // code you want.


    // 원하는 권한을 배열로 넣어줍니다.
    var permissions = arrayOf<String>(
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /* Location permission 을 위한 필드 */
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                finish()
            }
        }
    }
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_set_wi_fi)
        mBinding = ActivitySetWiFiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rv_ap = binding.rvAp
        var itemList = ArrayList<AccessPoint>()

//        itemList.add(AccessPoint("aaaa","bbbb","cccc"))
//        itemList.add(AccessPoint("dddd","eeee","ffff"))
//        itemList.add(AccessPoint("gggg","hhhh","iiii"))

        val AccessPointerAdapter = AccessPointerAdapter(itemList)
        AccessPointerAdapter.notifyDataSetChanged()

        rv_ap.adapter = AccessPointerAdapter


        val wifiManager = application.getSystemService(WIFI_SERVICE) as WifiManager
        rv_ap.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        mBinding!!.rvAp.layoutManager = linearLayoutManager
        itemList = ArrayList<AccessPoint>()
        if (wifiManager != null) {
            if (!wifiManager!!.isWifiEnabled) {
                wifiManager!!.isWifiEnabled = true
            }
            val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            registerReceiver(mWifiScanReceiver, filter)
            wifiManager!!.startScan()
        }




        //requestPermission(this)

//        val wifiManager = application.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        //val result: List<ScanResult> = wifiManager.scanResults
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //api 버전이 29 이상일 경우 (전송속도 사용시 필요)
            binding.myWifi.text ="ssid: ${ wifiInfo.ssid} bssid: ${wifiInfo.bssid} ip주소: ${wifiInfo.ipAddress} " +
                    "패스포인트: ${wifiInfo.networkId} mac주소: ${wifiInfo.macAddress}" +
                    "전송속도: ${wifiInfo.rxLinkSpeedMbps}"
        }
//       result.forEach{
//           binding.myWifi.text = "와이파이 이름: ${it.SSID} 공유기 mac: ${it.BSSID} ip주소: ${it.ip}"
//        }
    }

    //이전
    private fun requestPermission(activity: Activity) { //
        if(ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(activity, permissions, 1)
        }
    }

    private val mWifiScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                if (action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    wIFIScanResult
                    wifiManager!!.startScan()
                } else if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                    context.sendBroadcast(Intent("wifi.ON_NETWORK_STATE_CHANGED"))
                }
            }
        }
    }


    val wIFIScanResult: Unit
        get() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            scanResult = wifiManager!!.scanResults
            if (itemList!!.size != 0) {
                itemList!!.clear()
            }
            for (i in (scanResult as ArrayList<ScanResult>?)?.indices!!) {
                val result: ScanResult = (scanResult as ArrayList<ScanResult>?)!!.get(i)
                if (result.frequency < 3000) {
                    Log.d(
                        ". SSID : " + result.SSID,
                        result.level.toString() + ", " + result.BSSID
                    )
                    itemList!!.add(
                        AccessPoint(
                            result.SSID,
                            result.BSSID,
                            java.lang.String.valueOf(result.level)
                        )
                    )
                }
            }
            accessPointAdapter = AccessPointerAdapter(itemList!!)
            binding?.rvAp?.setAdapter(accessPointAdapter)
            accessPointAdapter!!.notifyDataSetChanged()
        }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mWifiScanReceiver)
    }
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this@SetWiFiActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this@SetWiFiActivity,
                listPermissionsNeeded.toTypedArray(),
                MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted")
                }
            }
        }
    } /* Location permission 을 위한 메서드들 */

    companion object {
        /* Location permission 을 위한 필드 */
        const val MULTIPLE_PERMISSIONS = 10 // code you want.
    }
}