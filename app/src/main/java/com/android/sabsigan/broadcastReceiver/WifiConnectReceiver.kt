package com.android.sabsigan.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.android.sabsigan.viewModel.WiFiViewModel

class WifiConnectReceiver(private val viewModel: WiFiViewModel) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // 와이파이 연결됐을 때 처리
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo: WifiInfo = wifiManager.connectionInfo
                val dhcpInfo = wifiManager.dhcpInfo

                viewModel.updateWiFiData(wifiInfo, dhcpInfo)
            } else if (networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                // 데이터 연결됐을 때 처리
                viewModel.updateWiFiData("mobileInfo.isConnected")
            } else {
                // 와이파이 연결이 끊겼을 때 처리
                viewModel.updateWiFiData("wifiInfo.null")
            }

//            val currentNetwork = connectivityManager.activeNetwork
//            val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
//            val ttt = connectivityManager.getNetworkCapabilities(currentNetwork)
//
//            val linkAddresses = linkProperties?.linkAddresses // IP 주소
//            val routeInfoList = linkProperties?.routes // 루트 정보
//            val dnsServers    = linkProperties?.dnsServers // DNS 서버 목록
//
//            Log.d("current WIFI", "======================================")
//            Log.d("current WIFI", "sss: " + ttt.toString())
//
//            for (linkAddress in linkAddresses!!)
//                Log.d("current WIFI", "IP Address: " + linkAddress.address.hostAddress)
//            for (routeInfo in routeInfoList!!) {
//                if (routeInfo.isDefaultRoute) {
//                    Log.d("current WIFI", "Gateway: " + routeInfo.gateway?.hostAddress)
//                    break
//                }
//            }
//            for (dnsServer in dnsServers!!)
//                Log.d("current WIFI", "IP DNS Server: " + dnsServer.hostAddress)
//            Log.d("current WIFI", "======================================")
        }

    }
}