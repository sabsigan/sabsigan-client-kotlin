package com.android.sabsigan.Beta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        var rightAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_right);
        var leftAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_left);

        binding.WiFiLayout.startAnimation(rightAnimation);
        binding.WiFiIcon.startAnimation(leftAnimation);
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}