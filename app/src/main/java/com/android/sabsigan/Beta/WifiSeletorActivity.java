package com.android.sabsigan.Beta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

import com.android.sabsigan.R;
import com.android.sabsigan.databinding.ActivityWifiSeletorBinding;

public class WifiSeletorActivity extends AppCompatActivity {

    ActivityWifiSeletorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWifiSeletorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 와이파이 아이콘 애니메이션
        Animation rightAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right);
        Animation leftAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_left);

        binding.WiFiLayout.startAnimation(rightAnimation);
        binding.WiFiIcon.startAnimation(leftAnimation);





    }
}