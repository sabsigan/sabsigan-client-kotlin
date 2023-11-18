package com.android.sabsigan.wifidirectsample.view

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ActivityMain3Binding
import com.android.sabsigan.wifidirectsample.event.ConnectionInfoEvent
import com.android.sabsigan.wifidirectsample.event.MyDeviceInfoEvent
import com.android.sabsigan.wifidirectsample.event.ResetDataEvent
import com.android.sabsigan.wifidirectsample.event.StatusChangedEvent
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import karrel.kr.co.wifidirectsample.R
import karrel.kr.co.wifidirectsample.WiFiDirectBroadcastReceiver
import karrel.kr.co.wifidirectsample.event.*
import karrel.kr.co.wifidirectsample.view.DefaultFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity3 : AppCompatActivity() {

    private val intentFilter = IntentFilter()
    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null

    private lateinit var wifiInfo: WifiP2pInfo
    private lateinit var binding : ActivityMain3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupBroadcatEvents()
        setupIntentFilter()
        setupWifiP2pManager()
        checkPermission()

    }

    private fun setupBroadcatEvents() {
        // 상태 : 와이파이 연결 가능 상태, Status: Wi-Fi enabled
        StatusChangedEvent.receive().subscribe {
            println("status change : ${it.name}")
            replaceFragment(DefaultFragment())
        }
        // 내 디바이스 정보, About My Devices
        MyDeviceInfoEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            deviceName.text = it.deviceName
            status.text = getDeviceStatus(it.status)
        }

        // 데이터 초기화, Data initialization
        ResetDataEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            replaceFragment(DefaultFragment())
        }

        // 와이파이 다이레긑 연결, Wi-Fi Direct Connection
        ConnectPeerEvent.receive().subscribe { connect(it) }

        // 와이파이 접속시 정보, About Wi-Fi access
        ConnectionInfoEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            setupConnectInfo(it)
        }
    }


    private fun setupConnectInfo(info: WifiP2pInfo) {
        this.wifiInfo = info

        // 그룹오너 여부
        if (info.isGroupOwner) {
            // 그룹 오너이면 서버를 보여주고
            replaceFragment(ServerFragment(info))
        } else {
            // 게스트 이면 음악 리스트를 송신할 수 있는 화면을 보여준다
            replaceFragment(PictureFragment(info))
        }
    }

    private fun connect(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        config.groupOwnerIntent = 0

        manager!!.connect(channel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                println("connect!!!")
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity3, "Connect failed. Retry.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_discorvery
            -> {
                discoveryPeer()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun discoveryPeer() {
        manager!!.discoverPeers(channel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                Toast.makeText(this@MainActivity3, "Discovery Initiated", Toast.LENGTH_SHORT).show()
                replaceFragment(DiscoverFragment())
            }

            override fun onFailure(reasonCode: Int) {
                Toast.makeText(this@MainActivity3, "Discovery Failed : $reasonCode", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupWifiP2pManager() {
        //WifiP2pManager 를 통해서 주변의 peer 를 탐색하거나 원하는 피어로 연결 요청이 가능합니다.
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager?.initialize(this, mainLooper, null)
    }

    private fun setupIntentFilter() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }


    private fun checkPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION

                )
                .check()
    }

    private val permissionlistener = object : PermissionListener {
        override fun onPermissionGranted() {
            registerReceiver()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            finish()
        }
    }

    private fun registerReceiver() {
        if (receiver != null) return
        receiver = WiFiDirectBroadcastReceiver(manager, channel!!)
        registerReceiver(receiver, intentFilter)
    }

    private fun unregisterRecevier() {
        if (receiver != null) {
            try {
                unregisterReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun getDeviceStatus(deviceStatus: Int): String {
        return when (deviceStatus) {
            WifiP2pDevice.AVAILABLE -> "Available"
            WifiP2pDevice.INVITED -> "Invited"
            WifiP2pDevice.CONNECTED -> "Connected"
            WifiP2pDevice.FAILED -> "Failed"
            WifiP2pDevice.UNAVAILABLE -> "Unavailable"
            else -> "Unknown"
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}
