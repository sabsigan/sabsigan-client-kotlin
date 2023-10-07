package com.android.sabsigan.beta

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ActivityWifiSelectorBinding
import io.reactivex.annotations.NonNull
import java.lang.Math.abs

class WifiSelectorActivity : AppCompatActivity() {
    private var mBinding: ActivityWifiSelectorBinding? = null    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private lateinit var adapter: ViewPagerAdapter

    private val RED_50 = "#FFEBEE" // 임시 색상
    private val BLUE_50 = "#E3F2FD"
    private val RED = "#E57373" // 임시 색상
    private val BLUE = "#64B5F6"
    private val RED_SHADOW = "#E53935"
    private val BLUE_SHADOW = "#1E88E5"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWifiSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ViewPagerAdapter(this)

        if (checkPermissions())
            startPorcess()
        else
            requestPermissions()

        binding.startView.setOnClickListener {
            startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        }
    }

    override fun onPause() {
        super.onPause()

        if (isReceiverRegistered(this))
            unregisterReceiver(networkReceiver)

        stopAnimation() // 애니메이션 제거
    }

    override fun onResume() {
        super.onResume()

        if (checkPermissions()) {
            if (!isReceiverRegistered(this))
                registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록

            startAnimation() // 애니메이션 시작
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isReceiverRegistered(this))
            unregisterReceiver(networkReceiver)
    }

    private fun startPorcess() {
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록
        startAnimation() // 와이파이 아이콘 애니메이션 시작
        setFragment() // 프래그먼트 호출
    }

    private fun startAnimation() {
        var rightAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_right)
        var leftAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_left)
        var radiate = AnimationUtils.loadAnimation(this, R.anim.radiate)
        var radiate2 = AnimationUtils.loadAnimation(this, R.anim.radiate)

        binding.WiFiIconLayout.startAnimation(rightAnimation)
        binding.WiFiIcon.startAnimation(leftAnimation)
        binding.wave1.startAnimation(radiate)

        Handler(Looper.getMainLooper()).postDelayed({ // wave2는 0.6초 후 실행
            binding.wave2.startAnimation(radiate2)
        }, 600)
    }

    private fun stopAnimation() {
        binding.WiFiIconLayout.clearAnimation()
        binding.WiFiIcon.clearAnimation()
        binding.wave1.clearAnimation()
        binding.wave2.clearAnimation()
    }

    private fun setConnectedColor() { // 와이파이 연결 됐을 때 색
//        binding.defaultLayout.setBackgroundColor(Color.parseColor(BLUE_50))
        binding.WiFiIconLayout.setBackgroundResource(R.drawable.wifi_gradient_blue) // 푸른색
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            Color.parseColor(BLUE_SHADOW).also { binding.WiFiIconLayout.outlineSpotShadowColor = it }

        binding.WiFiIcon.setImageResource(R.drawable.wifi)
        binding.wave1.setColorFilter(Color.parseColor(BLUE))
        binding.wave2.setColorFilter(Color.parseColor(BLUE))
    }

    private fun setUnconnectedColor() {// 와이파이 연결 안 됐을 때 색
//        binding.defaultLayout.setBackgroundColor(Color.parseColor(RED_50))
        binding.WiFiIconLayout.setBackgroundResource(R.drawable.wifi_gradient_red) // 붉은색
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            Color.parseColor(RED_SHADOW).also { binding.WiFiIconLayout.outlineSpotShadowColor = it }

        binding.WiFiIcon.setImageResource(R.drawable.wifi_off)
        binding.wave1.setColorFilter(Color.parseColor(RED))
        binding.wave2.setColorFilter(Color.parseColor(RED))
    }

    private fun setFragment() {
        val fragmentlist = listOf(WifiListFragment(), WifiInfoFragment(), WifiRateFragment())
        adapter.setFragmentList(fragmentlist)

        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 2
        binding.indicator.setViewPager2(binding.viewPager) // 인디케이터 뷰페이저 연결

        val nextItemVisibleWidth = resources.getDimension(R.dimen.next_item_visible_width)
        val currentItemMargin = resources.getDimension(R.dimen.viewpager_horizontal_margin)
        val pageTranslation = nextItemVisibleWidth + currentItemMargin

        val itemDecoration = PagerMarginItemDecoration(
            this,
            R.dimen.viewpager_horizontal_margin
        )

        binding.viewPager.setPageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslation * position
            page.scaleY = 1 - (0.25f * abs(position))
//            page.alpha = 0.25f + (1 - abs(position))
        }

        binding.viewPager.addItemDecoration(itemDecoration)
        binding.viewPager.setCurrentItem(1, false)
    }

    private fun checkPermissions(): Boolean {
        val locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            // 승인된 상태
            return true
        }

        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 10
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            10 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted")
                    startPorcess()
                } else {
                    finish()
                }
            }
        }
    }

    private fun isReceiverRegistered(context: Context): Boolean {
        val pm = context.packageManager
        val intent = Intent(ConnectivityManager.CONNECTIVITY_ACTION)
        val receivers = pm.queryBroadcastReceivers(intent, 0)

        for (receiver in receivers) {
            if (receiver.activityInfo.packageName == context.packageName) {
                return true // 리시버가 현재 등록되어 있음
            }
        }

        return false // 리시버가 현재 등록되어 있지 않음
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo

                if (networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    // 와이파이 연결됐을 때 처리
                    setConnectedColor() // 색 변경

                    val currentNetwork = connectivityManager.activeNetwork
                    val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
                    val linkAddresses = linkProperties?.linkAddresses // IP 주소
                    val routeInfoList = linkProperties?.routes // 루트 정보
                    val dnsServers    = linkProperties?.dnsServers // DNS 서버 목록

                    Log.d("와이파이 정보", "================================================")
                    Log.d("와이파이 정보", "Domains: " + linkProperties?.domains)
                    Log.d("와이파이 정보", "InterfaceName: " + linkProperties?.interfaceName)
                    for (linkAddress in linkAddresses!!)
                        Log.d("와이파이 정보", "IP Address: " + linkAddress.address.hostAddress)
                    for (routeInfo in routeInfoList!!) {
                        if (routeInfo.isDefaultRoute) {
                            Log.d("와이파이 정보", "Gateway: " + routeInfo.gateway?.hostAddress)
                            break
                        }
                    }
                    for (dnsServer in dnsServers!!)
                        Log.d("와이파이 정보", "IP DNS Server: " + dnsServer.hostAddress)
                    Log.d("와이파이 정보", "================================================")


                    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo = wifiManager.connectionInfo
                    val getSsid = wifiInfo.ssid

                    val dhcpInfo = wifiManager.dhcpInfo
                    val wIp = dhcpInfo.ipAddress

                    val getIpAddress = String.format(
                        "%d.%d.%d.%d",
                        wIp and 0xff,
                        wIp shr 8 and 0xff,
                        wIp shr 16 and 0xff,
                        wIp shr 24 and 0xff
                    )

                    Log.v("NetworkInfo", "================================================")
                    Log.v("NetworkInfo", "SSID: $getSsid")
                    Log.v("NetworkInfo", "hiddenSSID: ${wifiInfo.hiddenSSID}")
                    Log.v("NetworkInfo", "networkId: " + wifiInfo.networkId.toString())
                    Log.v("NetworkInfo", "bssid: " + wifiInfo.bssid)
                    Log.v("NetworkInfo", "ipAddress: " + wifiInfo.ipAddress)
                    Log.v("NetworkInfo", "ipAddress2: " + getIpAddress)
                    Log.v("NetworkInfo", "macAddress: " + wifiInfo.macAddress)
                    Log.v("NetworkInfo", "linkSpeed: " + wifiInfo.linkSpeed)
                    Log.v("NetworkInfo", "================================================")
                } else if (networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    // 와이파이 연결됐을 때 처리
                    setUnconnectedColor()
//                    Toast.makeText(context, "데이터가 연결되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 와이파이 연결이 끊겼을 때 처리
                    setUnconnectedColor()
//                    Toast.makeText(context, "인터넷이 끊겼습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}