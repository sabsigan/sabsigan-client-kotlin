package com.android.sabsigan.Wifi

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.sabsigan.Main.MainActivity2
import com.android.sabsigan.R
import androidx.lifecycle.Observer
import com.android.sabsigan.ViewModel.WiFiViewModel
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.databinding.ActivityWifiSelectorBinding
import io.reactivex.annotations.NonNull
import java.lang.Math.abs

class WifiSelectorActivity : AppCompatActivity() {
    private var mBinding: ActivityWifiSelectorBinding? = null    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private val viewModel by viewModels<WiFiViewModel>()
    private lateinit var wifiConnectReceiver: WifiConnectReceiver
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

        wifiConnectReceiver = WifiConnectReceiver(viewModel)

        window.statusBarColor = ContextCompat.getColor(this, R.color.background_1) // 스테이터스 색 변경
        defaultAnimaion() // 와이파이 아이콘 회전

        if (checkPermissions())
            startPorcess()
        else
            requestPermissions()

        viewModel.getwifiInfo().observe(this, Observer { wifidata ->
            Log.d("엑티비티에서 데이터 변경 감지:",wifidata)

            if (wifidata == "wifiInfo.isConnected" ) // 와이파이 연결
                setConnectedColor() // 색 변경
            else
                setUnconnectedColor()
        })

        binding.startView.setOnClickListener {
            signIn()
//            startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        }
    }

    override fun onPause() {
        super.onPause()
//        stopAnimation()

        if (isReceiverRegistered(this))
            unregisterReceiver(wifiConnectReceiver)
    }

    override fun onResume() {
        super.onResume()
        defaultAnimaion()

        if (checkPermissions()) { // 위치 권한 얻었을 때만
            if (!isReceiverRegistered(this))
                registerReceiver(wifiConnectReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록

            startAnimation() // 웨이브 애니메이션 시작
            // 웨이브 애니메이션에는 딜레이가 있어서 onResume 때마다 다시 처리해야 함
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isReceiverRegistered(this))
            unregisterReceiver(wifiConnectReceiver)
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

    private fun startPorcess() {
        registerReceiver(wifiConnectReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // 리시버 등록
        startAnimation() // 와이파이 아이콘 애니메이션 시작
        setFragment() // 프래그먼트 호출
    }

    private fun setFragment() {
        val fragmentlist = listOf(WifiListFragment(), WifiInfoFragment(), WifiRateFragment())
        adapter = ViewPagerAdapter(this)
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

    private fun signIn() {
        val userID = 0 // sharedpreferences 같은 값으로 user 키 가져오기
        val state = viewModel.getwifiInfo().value

        if (userID > 0 && state == "wifiInfo.isConnected") {
            // 자동 로그인
        } else if (state == "wifiInfo.isConnected") {
            val layoutInflater = LayoutInflater.from(this)
            val view = layoutInflater.inflate(R.layout.signin_popup2, null)

            val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setView(view)
                .create()

            val textTitle = view.findViewById<TextView>(R.id.Title)
            val inputNickname =  view.findViewById<EditText>(R.id.inputNickname)
            val inputTemp =  view.findViewById<EditText>(R.id.inputTemp)
            val buttonConfirm =  view.findViewById<Button>(R.id.Button)

            textTitle.text = "환영합니다"
            inputNickname.hint = "닉네임을 입력하세요"
            inputTemp.hint = "소개글...?"
            buttonConfirm.text = "LOGIN"
            buttonConfirm.setOnClickListener {
                val nickName = inputNickname.text
                val temp = inputTemp.text
                Log.d("로그인 테스트", "id: $nickName, temp: $temp")
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                alertDialog.dismiss()
                finish()
            }

            alertDialog.show()
        } else {
            Toast.makeText(this, "와이파이를 연결해주세요", Toast.LENGTH_SHORT).show()
        }
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

