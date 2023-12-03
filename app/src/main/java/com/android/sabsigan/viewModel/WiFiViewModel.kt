package com.android.sabsigan.viewModel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

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

    fun getBSSID() = _bssid;

    fun getTime() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

    fun customHash(ids: ArrayList<String>) : String {
        // 두 문자열을 정렬하여 순서에 상관없이 같은 문자열을 생성
        val sortedStrings = ids.sorted()
        // 정렬된 문자열을 이용하여 고유한 해시 값을 생성

        var combinedString = ""
        sortedStrings.forEach { combinedString += it}
        combinedString += getwifiInfo().value // 채팅방에 포함된 유저 id와 wifi 이름으로 해시키 생성

        // SHA-256 해시 함수 사용
        val bytes = MessageDigest.getInstance("SHA-256").digest(combinedString.toByteArray())

        // 바이트 배열의 2/3 사용하여 16진수 문자열로 변환하여 반환
        val halfLength = (bytes.size / 3) * 2
        val halfBytes = bytes.copyOfRange(0, halfLength)

        // 바이트 배열을 16진수 문자열로 변환하여 반환
        return halfBytes.joinToString("") { "%02x".format(it) }
    }

    fun generateAvatar(id: String): Bitmap {
        val hash = customHash(arrayListOf(id))
        val size = 100

        val avatarBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(avatarBitmap)
        val paint = Paint()

        // Set a random background color
        canvas.drawColor(getColorFromHash(hash))

        val cx = hash.substring(0, 2).toInt(16).toFloat() % size
        val cy = hash.substring(2, 4).toInt(16).toFloat() % size
        val left = hash.substring(4, 6).toInt(16).toFloat() % size
        val top = hash.substring(6, 8).toInt(16).toFloat() % size
        val startX = hash.substring(8, 10).toInt(16).toFloat() % size
        val startY = hash.substring(10, 12).toInt(16).toFloat() % size

        for (i in 0 until 4) {
            when (hash[i % hash.length].toInt() % 3) {
                0 -> canvas.drawCircle(cx, cy, (size / 4).toFloat(), paint)
                1 -> canvas.drawRect(left, top, (size / 2).toFloat(), (size / 2).toFloat(), paint)
                2 -> canvas.drawLine(startX, startY, (size / 2).toFloat(), (size / 2).toFloat(), paint)
            }
        }

        return avatarBitmap
    }

    private fun getColorFromHash(hash: String): Int {
        return Color.rgb(hash.substring(0, 2).toInt(16), hash.substring(2, 4).toInt(16), hash.substring(4, 6).toInt(16))
    }
}