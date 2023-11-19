package com.android.sabsigan.wifidirectsample.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ActivityMain3Binding
import com.android.sabsigan.wifidirectsample.WifiDirectReceiver
import com.android.sabsigan.wifidirectsample.event.ConnectionInfoEvent
import com.android.sabsigan.wifidirectsample.event.MyDeviceInfoEvent
import com.android.sabsigan.wifidirectsample.event.ResetDataEvent
import com.android.sabsigan.wifidirectsample.event.StatusChangedEvent
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import karrel.kr.co.wifidirectsample.event.*
import karrel.kr.co.wifidirectsample.view.DefaultFragment
import karrel.kr.co.wifidirectsample.view.DiscoverFragment
import karrel.kr.co.wifidirectsample.view.PictureFragment
import karrel.kr.co.wifidirectsample.view.ServerFragment

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

    @SuppressLint("CheckResult")
    private fun setupBroadcatEvents() {
        // 상태 : 와이파이 연결 가능 상태, Status: Wi-Fi enabled
        StatusChangedEvent.receive().subscribe {
            println("status change : ${it.name}")
            replaceFragment(DefaultFragment())
        }
        // 내 디바이스 정보, About My Devices
        MyDeviceInfoEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            //TODO 화면 출력
//            deviceName.text = it.deviceName
//            status.text = getDeviceStatus(it.status)
            binding.contentMain.deviceName.text = it.deviceName
            binding.contentMain.status.text = getDeviceStatus(it.status)
            Log.d(TAG,"내 디바이스 이름: "+it.deviceName)
            Log.d(TAG,"내 디바이스 상태: "+it.status)

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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this@MainActivity3,"권한 실패로 인한 연결 실패",Toast.LENGTH_LONG).show()
            return
        }
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
            R.id.action_discorvery -> {
                discoveryPeer()
                return true
            }
//            R.id.action_reset -> {
//                ResetDataEvent.send(true)
//                return true
//            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun discoveryPeer() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this@MainActivity3,"권한 실패로 인한 주변 기기찾기 실패 ",Toast.LENGTH_LONG).show()
            return
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
    //                .setDeniedCloseButtonText("닫기")
    //                .setGotoSettingButtonText("설정")
    //                .setRationaleTitle("HELLO")
                    .setPermissions(
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 외부 저장소에 데이터를 쓰거나 읽을 수 있는 권한을 제공
//                            Manifest.permission.ACCESS_COARSE_LOCATION, //대략적인 위치 정보에 액세스할 수 있는 권한을 제공
                            Manifest.permission.ACCESS_FINE_LOCATION, //정확한 위치 정보에 액세스할 수 있는 권한을 제공
//                            Manifest.permission.NEARBY_WIFI_DEVICES //근처의 Wi-Fi 디바이스에 대한 정보에 액세스할 수 있는 권한을 제공

                    )
                    .check()
        }
    }

    private val permissionlistener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(this@MainActivity3,"위치,저장소 권한 완료",Toast.LENGTH_SHORT)
            registerReceiver()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(this@MainActivity3,"위치,저장소 권한 실패",Toast.LENGTH_SHORT)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterRecevier()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        ResetDataEvent.send(true)
//        unregisterRecevier()
//    }

    private fun registerReceiver() {
        if (receiver != null) return
        receiver = WifiDirectReceiver(manager, channel!!)
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
