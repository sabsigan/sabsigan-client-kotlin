package com.android.sabsigan.beta

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sabsigan.R
import com.android.sabsigan.ViewModel.WiFiViewModel
import com.android.sabsigan.beta.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.databinding.ActivityWifiSelectorBinding

class WifiSelectorActivity : AppCompatActivity() {
    private var mBinding: ActivityWifiSelectorBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

//    val viewModel by viewModels<WiFiViewModel>() //뷰모델 생성
//    private var wifiConnectReceiver = WifiConnectReceiver(viewModel)
    private lateinit var viewModel: WiFiViewModel


    private val RED = "#E57373" // 임시 색상
    private val BLUE = "#64B5F6"
    private val RED_SHADOW = "#E53935"
    private val BLUE_SHADOW = "#1E88E5"

    private var test = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWifiSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(WiFiViewModel::class.java)
        viewModel.getwifiInfo().observe(this, Observer { wifidata ->
            Log.d("엑티비티에서 데이터 변경 감지:",wifidata)
        })

        var wifiConnectReceiver = WifiConnectReceiver(viewModel)

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
//        filter.addAction(WifiManager.EXTRA_NETWORK_INFO)
        registerReceiver(wifiConnectReceiver,filter ) // 리시버 등록

        startAnimation() // 와이파이 아이콘 애니메이션

        
        val fragmentlist = listOf(WifiListFragment(), WifiInfoFragment(), WifiRateFragment())
        val adapter = ViewPagerAdapter(this)
        adapter.setFragmentList(fragmentlist)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(1, false)

        requestPermission(this)
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
//                    Toast.makeText(context, "와이파이가 연결되었습니다.", Toast.LENGTH_SHORT).show()
                } else if (mobileInfo != null && mobileInfo.isConnected) {
                    // 와이파이 연결됐을 때 처리
                    setUnconnectedColor()
//                    Toast.makeText(context, "데이터가 연결되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 와이파이 연결이 끊겼을 때 처리
                    setUnconnectedColor()
//                    Toast.makeText(context, "인터넷이 끊겼습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            if(intent?.action == WifiManager.NETWORK_STATE_CHANGED_ACTION){
                //와이파이 상태가 변경된 경우
                val wifiStateChangedIntent = Intent("wifi.ACTION_WIFI_STATE_CHANGED")
                context?.sendBroadcast(wifiStateChangedIntent)
            }
            val networkInfo = intent?.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if(networkInfo?.state == NetworkInfo.State.DISCONNECTED){
                //와이파이가 꺼진 경우
                val wifiOffIntent = Intent("wifi.ACTION_WIFI_OFF")
                context?.sendBroadcast(wifiOffIntent)
            }
        }
    }

    private fun requestPermission(activity: Activity) { //권한 설정
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
}

