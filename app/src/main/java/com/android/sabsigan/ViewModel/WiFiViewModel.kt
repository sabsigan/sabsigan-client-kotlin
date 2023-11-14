package com.android.sabsigan.ViewModel

import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class WiFiViewModel: ViewModel() {
    private var _ssid = MutableLiveData<String>()
    private var _bssid = MutableLiveData<String>()
    private var _ess = MutableLiveData<String>()
    private var _macAdress = MutableLiveData<String>()
    private var _networkId = MutableLiveData<Int>()
    private var _linkSpeed = MutableLiveData<Int>()
    private var _ipAdress = MutableLiveData<String>()

    fun updateWiFiData(ssid: String) {
        _ssid.value = ssid
        _bssid.value = ""
        _macAdress.value = ""
        _networkId.value = 0
        _linkSpeed.value = 0
        _ipAdress.value = ""
    }

    fun updateWiFiData(wifiInfo: WifiInfo, dhcpInfo:DhcpInfo) {
        _ssid.value = wifiInfo.ssid
        _bssid.value = wifiInfo.bssid
        _macAdress.value = wifiInfo.macAddress
        _networkId.value = wifiInfo.networkId
        _linkSpeed.value = wifiInfo.linkSpeed
        val wIp = dhcpInfo.ipAddress
        _ipAdress.value = String.format(
            "%d.%d.%d.%d",
            wIp and 0xff,
            wIp shr 8 and 0xff,
            wIp shr 16 and 0xff,
            wIp shr 24 and 0xff
        )
    }

    fun getwifiInfo(): LiveData<String> {
        return _ssid
    }

}