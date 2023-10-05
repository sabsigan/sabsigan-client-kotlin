package com.android.sabsigan.beta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.sabsigan.databinding.ActivityMain2Binding

class Main2Activity : AppCompatActivity() {
    private var mBinding: ActivityMain2Binding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}