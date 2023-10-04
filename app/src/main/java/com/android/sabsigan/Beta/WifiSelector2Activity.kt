package com.android.sabsigan.Beta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ActivityWifiSelector2Binding

class WifiSelector2Activity : AppCompatActivity() {
    private var mBinding: ActivityWifiSelector2Binding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWifiSelector2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 와이파이 아이콘 애니메이션
        var radiate = AnimationUtils.loadAnimation(this, R.anim.radiate);

        binding.wave.startAnimation(radiate);
    }
}