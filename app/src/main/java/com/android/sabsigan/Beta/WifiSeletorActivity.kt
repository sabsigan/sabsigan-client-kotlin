package com.android.sabsigan.Beta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ActivityWifiSeletorBinding

class WifiSeletorActivity : AppCompatActivity() {
    private var mBinding: ActivityWifiSeletorBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWifiSeletorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 와이파이 아이콘 애니메이션
        var rightAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_right)
        var leftAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_left)
        var radiate = AnimationUtils.loadAnimation(this, R.anim.radiate)
        var radiate2 = AnimationUtils.loadAnimation(this, R.anim.radiate)


//        binding.WiFiLayout.setBackgroundResource(R.drawable.wifi_gradient_red) // 붉은색
//        binding.WiFiIcon.setImageResource(R.drawable.wifi_off)
//        binding.wave1.setImageResource(R.drawable.ring_red)
//        binding.wave2.setImageResource(R.drawable.ring_red)
//
//        binding.WiFiLayout.setBackgroundResource(R.drawable.wifi_gradient_blue) // 푸른색
//        binding.WiFiIcon.setImageResource(R.drawable.wifi)
//        binding.wave1.setImageResource(R.drawable.ring_blue)
//        binding.wave2.setImageResource(R.drawable.ring_blue)

        binding.WiFiLayout.startAnimation(rightAnimation)
        binding.WiFiIcon.startAnimation(leftAnimation)
        binding.wave1.startAnimation(radiate)


        Handler(Looper.getMainLooper()).postDelayed({
            binding.wave2.startAnimation(radiate2)
        }, 600)

        //------------------------------------- 나중에 메서드로
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}