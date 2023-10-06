package com.android.sabsigan.beta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.sabsigan.R
import com.android.sabsigan.beta.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.databinding.ActivityWifiSelectorBinding

class WifiSelectorActivity : AppCompatActivity() {
    private var mBinding: ActivityWifiSelectorBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
//    private var wifiConnectReceiver = WifiConnectReceiver()

    private val RED = "#E57373" // 임시 색상
    private val BLUE = "#64B5F6"
    private val RED_SHADOW = "#E53935"
    private val BLUE_SHADOW = "#1E88E5"

    private var test = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWifiSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록

        startAnimation() // 와이파이 아이콘 애니메이션

        
        val fragmentlist = listOf(WifiListFragment(), WifiInfoFragment(), WifiRateFragment())
        val adapter = ViewPagerAdapter(this)
        adapter.setFragmentList(fragmentlist)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(1, false)
    }

    override fun onPause() {
        super.onPause()
        stopAnimation() // 애니메이션 제거
        unregisterReceiver(networkReceiver)
    }

    override fun onResume() {
        super.onResume()
        startAnimation() // 애니메이션 재시작
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
        unregisterReceiver(networkReceiver)
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

    fun setConnectedColor() { // 와이파이 연결 됐을 때 색
        binding.WiFiIconLayout.setBackgroundResource(R.drawable.wifi_gradient_blue) // 푸른색
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            Color.parseColor(BLUE_SHADOW).also { binding.WiFiIconLayout.outlineSpotShadowColor = it }

        binding.WiFiIcon.setImageResource(R.drawable.wifi)
        binding.wave1.setColorFilter(Color.parseColor(BLUE))
        binding.wave2.setColorFilter(Color.parseColor(BLUE))
    }

    private fun setUnconnectedColor() {// 와이파이 연결 안 됐을 때 색
        binding.WiFiIconLayout.setBackgroundResource(R.drawable.wifi_gradient_red) // 붉은색
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            Color.parseColor(RED_SHADOW).also { binding.WiFiIconLayout.outlineSpotShadowColor = it }

        binding.WiFiIcon.setImageResource(R.drawable.wifi_off)
        binding.wave1.setColorFilter(Color.parseColor(RED))
        binding.wave2.setColorFilter(Color.parseColor(RED))
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityManager =
                    context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

                if (wifiInfo != null && wifiInfo.isConnected) {
                    // 와이파이 연결됐을 때 처리
                    setConnectedColor()
                    Toast.makeText(context, "와이파이가 연결되었습니다.", Toast.LENGTH_SHORT).show()
                } else if (mobileInfo != null && mobileInfo.isConnected) {
                    // 와이파이 연결됐을 때 처리
                    setUnconnectedColor()
                    Toast.makeText(context, "데이터가 연결되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 와이파이 연결이 끊겼을 때 처리
                    setUnconnectedColor()
                    Toast.makeText(context, "인터넷이 끊겼습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}