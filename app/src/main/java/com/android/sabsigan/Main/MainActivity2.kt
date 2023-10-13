package com.android.sabsigan.Main

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //엑션바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 왼쪽 상단에 버튼 만들기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24) // 왼쪽 상단 버튼 아이콘 지정

        val navView: BottomNavigationView = binding.navView //바텀 네비게이션
        val drawer = binding.navDrawer // 왼쪽 드로우어
        val navController = findNavController(R.id.nav_host_fragment_activity_main2) // 네비 컨트롤러

        //네비와 연결 셋업
        navView.setupWithNavController(navController)
        drawer.setupWithNavController(navController) //drawerNavigation 설정하여 동기화

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) { //앱바 왼쪽 상단 매뉴 선택 (home자리임)
            android.R.id.home -> binding.mainActivityLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(binding.mainActivityLayout.isDrawerOpen(binding.navDrawer))
            binding.mainActivityLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}