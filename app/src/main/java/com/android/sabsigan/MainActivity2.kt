package com.android.sabsigan

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.sabsigan.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val drawerLayout = binding.mainActivityLayout



        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_user, R.id.navigation_chatting
            )
//            navController.graph
        )
        setSupportActionBar(binding.toolbar) //엑션바 등록
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 왼쪽 상단에 버튼 만들기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24) // 왼쪽 상단 버튼 아이콘 지정


//        binding.toolbar.setupWithNavController(navController,drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration) //appBarConfiguration 대신에 drawerLayout 으로 하면 아이콘은 보여짐
        navView.setupWithNavController(navController)

        binding.navDrawer.setupWithNavController(navController) //drawerNavigation 설정하여 동기화


    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.nav_host_fragment_activity_main2)
//        return NavigationUI.navigateUp(navController,drawerLayout)
//    }


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