package com.android.sabsigan.beta

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ActivityWifiSelectorBinding
import io.reactivex.annotations.NonNull
import java.lang.Math.abs

class WifiSelectorActivity : AppCompatActivity() {
    private var mBinding: ActivityWifiSelectorBinding? = null    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private val viewModel by viewModels<WifiViewModel>()
    private lateinit var adapter: ViewPagerAdapter

    private val RED_50 = "#FFEBEE" // 임시 색상
    private val BLUE_50 = "#E3F2FD"
    private val RED = "#E57373" // 임시 색상
    private val BLUE = "#64B5F6"
    private val RED_SHADOW = "#E53935"
    private val BLUE_SHADOW = "#1E88E5"

    val MULTIPLE_PERMISSIONS = 10 // code you want.

    val listPermissionsNeeded: MutableList<String> = ArrayList()
    // 원하는 권한을 배열로 넣어줍니다.
    var permissions = arrayOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWifiSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.background_1) // 스테이터스 색 변경
        defaultAnimaion() // 와이파이 아이콘 회전

        adapter = ViewPagerAdapter(this)

        if (checkPermissions())
            startPorcess()
        else
            requestPermissions()

        binding.startView.setOnClickListener {
//            startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        }
    }

    override fun onPause() {
        super.onPause()
        stopAnimation()

        if (isReceiverRegistered(this))
            unregisterReceiver(networkReceiver)
    }

    override fun onResume() {
        super.onResume()
        defaultAnimaion()

        if (checkPermissions()) { // 위치 권한 얻었을 때만
            if (!isReceiverRegistered(this))
                registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록

            startAnimation() // 웨이브 애니메이션 시작
            // 웨이브 애니메이션에는 딜레이가 있어서 onResume 때마다 다시 처리해야 함
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

    private fun defaultAnimaion() { // 와이파이 회전 애니메이션
        var rightAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_right)
        var leftAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_left)

        binding.WiFiIconLayout.startAnimation(rightAnimation)
        binding.WiFiIcon.startAnimation(leftAnimation)
    }

    private fun startAnimation() { // 웨이브 애니메이션
        var radiate = AnimationUtils.loadAnimation(this, R.anim.radiate)
        var radiate2 = AnimationUtils.loadAnimation(this, R.anim.radiate)

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

        binding.WiFiIcon.setImageResource(R.drawable.wifi_on)
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
        binding.viewPager.offscreenPageLimit = 1

        binding.indicator.setViewPager2(binding.viewPager) // 인디케이터 뷰페이저 연결

        val nextItemVisibleWidth = resources.getDimension(R.dimen.next_item_visible_width)
        val currentItemMargin = resources.getDimension(R.dimen.viewpager_horizontal_margin)
        val pageTranslation = nextItemVisibleWidth + currentItemMargin

        val itemDecoration = PagerMarginItemDecoration(this, R.dimen.viewpager_horizontal_margin)

        binding.viewPager.setPageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslation * position
            page.scaleY = 1 - (0.25f * abs(position))
//            page.alpha = 0.25f + (1 - abs(position))
        }

        binding.viewPager.addItemDecoration(itemDecoration)
        binding.viewPager.setCurrentItem(1, false)
    }

    private fun checkPermissions(): Boolean {
        for (p in permissions) {
            var result = ContextCompat.checkSelfPermission(this, p)

            if (result != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(p)
        }

        if (!listPermissionsNeeded.isEmpty())
            return false

        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            listPermissionsNeeded.toTypedArray(),
            MULTIPLE_PERMISSIONS
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

                viewModel.increaseValue()


                if (networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    // 와이파이 연결됐을 때 처리
                    setConnectedColor() // 색 변경

                    val currentNetwork = connectivityManager.activeNetwork
                    val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
                    val ttt = connectivityManager.getNetworkCapabilities(currentNetwork)

                    val linkAddresses = linkProperties?.linkAddresses // IP 주소
                    val routeInfoList = linkProperties?.routes // 루트 정보
                    val dnsServers    = linkProperties?.dnsServers // DNS 서버 목록

                    Log.d("current WIFI", "======================================")
                    Log.d("current WIFI", "sss: " + ttt.toString())

                    for (linkAddress in linkAddresses!!)
                        Log.d("current WIFI", "IP Address: " + linkAddress.address.hostAddress)
                    for (routeInfo in routeInfoList!!) {
                        if (routeInfo.isDefaultRoute) {
                            Log.d("current WIFI", "Gateway: " + routeInfo.gateway?.hostAddress)
                            break
                        }
                    }
                    for (dnsServer in dnsServers!!)
                        Log.d("current WIFI", "IP DNS Server: " + dnsServer.hostAddress)
                    Log.d("current WIFI", "======================================")

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