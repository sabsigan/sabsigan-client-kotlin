package com.android.sabsigan.wifidirectsample.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDevice.UNAVAILABLE
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.sabsigan.R
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.databinding.ActivityMain3Binding
import com.android.sabsigan.main.chatting.ChatActivity
import com.android.sabsigan.wifidirectsample.WifiDirectReceiver
import com.android.sabsigan.wifidirectsample.event.ConnectionInfoEvent
import com.android.sabsigan.wifidirectsample.event.MyDeviceInfoEvent
import com.android.sabsigan.wifidirectsample.event.ResetDataEvent
import com.android.sabsigan.wifidirectsample.event.StatusChangedEvent
import com.android.sabsigan.wifidirectsample.event.WifiEnable
import com.google.common.net.InetAddresses
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import karrel.kr.co.wifidirectsample.event.*
import karrel.kr.co.wifidirectsample.view.ClientFragment
import karrel.kr.co.wifidirectsample.view.DefaultFragment
import karrel.kr.co.wifidirectsample.view.DiscoverFragment
import karrel.kr.co.wifidirectsample.view.ServerFragment
import java.io.Serializable


class MainActivity3 : AppCompatActivity() {

    private val intentFilter = IntentFilter()
    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null

    private lateinit var wifiInfo: WifiP2pInfo
    private lateinit var binding : ActivityMain3Binding

    private var groupformed : Boolean = false
    private var exitMenuItem: MenuItem? = null
    private val NEARBY_WIFI_PERMISSION_REQUEST_CODE = 123
    private val LOCATION_PERMISSION_REQUEST_CODE = 124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)



        setupWifiP2pManager()
        checkPermission()
        setupBroadcatEvents()
        setupIntentFilter()



        binding.toolbar.title = "WiFi-Direct Chat"
        setSupportActionBar(binding.toolbar)



    }

    @SuppressLint("CheckResult")
    private fun setupBroadcatEvents() {
        // 상태 : 와이파이 연결 가능 상태, Status: Wi-Fi enabled
        StatusChangedEvent.receive().subscribe {
            println("status change : ${it.name}")
            binding.contentMain.status.text =  it.name

            if(it.equals(WifiEnable.ENABLE)) {
                replaceFragment(DiscoverFragment())
            } else{
                replaceFragment(DefaultFragment())
            }

        }
        // 내 디바이스 정보, About My Devices
        MyDeviceInfoEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            //TODO 화면 출력
            binding.contentMain.deviceName.text = it.deviceName
//            binding.contentMain.status.text = getDeviceStatus(it.status)
            Log.d(TAG,"내 디바이스 이름: "+it.deviceName)
//            Log.d(TAG,"내 디바이스 상태: "+it.status)

        }

        // 데이터 초기화, Data initialization
        ResetDataEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
//            replaceFragment(DefaultFragment())
        }

        // 와이파이 다이렉트  연결, Wi-Fi Direct Connection
        ConnectPeerEvent.receive().subscribe { connect(it) }

        // 와이파이 접속시 정보, About Wi-Fi access
        ConnectionInfoEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            setupConnectInfo(it)

        }
    }


    private fun setupConnectInfo(info: WifiP2pInfo) {
        this.wifiInfo = info
        groupformed = info.groupFormed // 그룹 형성 여부
        val chatRoom = ChatRoom(
            name = null,
            users = arrayListOf()
        )

        if (groupformed) {
            // Intent에 Bundle 추가
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatRoom", chatRoom as Serializable) //chatRoom
            intent.putExtra("groupOwnerAddress",info.groupOwnerAddress.hostAddress)
            Log.d("메인->chat 오너_주소: ", info.groupOwnerAddress.hostAddress!!)
            if(info.isGroupOwner) {// 그룹 오너
                intent.putExtra("myName", "owner")
                intent.putExtra("chatName", "direct")
            }else{  // 클라이언트
                intent.putExtra("myName","client" )
                intent.putExtra("chatName","direct")
            }
            // 화면 전환
            startActivity(intent)

//            if (info.isGroupOwner) {
//                // 그룹 오너이면 서버 화면을 보여주고
//                replaceFragment(ServerFragment(info))
//            } else {
//                // 게스트 이면 클라이언트 화면을 보여준다
//                replaceFragment(ClientFragment(info))
//            }
        } else {
            Log.d("setupConnectInfo", groupformed.toString() )
        }
    }

    private fun connect(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        config.groupOwnerIntent = 0

        Toast.makeText(this@MainActivity3,device.deviceName+" 장치로 연결중",Toast.LENGTH_LONG).show()

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
                Log.d(TAG,"connect!!!")
                setExitMenuVisible(true) // 나가기 버튼 생김
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity3, "Connect failed. Retry.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_direct, menu)

        exitMenuItem = menu?.findItem(R.id.action_Exit)
        setExitMenuVisible(false)
        return true
    }

    fun setExitMenuVisible(b : Boolean) {
        //나가기 버튼 조건에 따라 보이기/숨기기
        exitMenuItem?.isVisible = b

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { // 탐색
            R.id.action_discorvery -> {
                discoveryPeer()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
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




    private val permissionlistener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(this@MainActivity3,"위치 권한 완료",Toast.LENGTH_SHORT).show()
            registerReceiver()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(this@MainActivity3,"위치 권한 실패",Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
        Log.d("onResume:","registerReceiver")
    }

    override fun onPause() {
        super.onPause()
//        unregisterRecevier()
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterRecevier()
    }

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
        if(!isDestroyed) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
        }
    }




    // 권한을 확인하고 요청하는 메서드
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // S 이상에서만 NEARBY_WIFI_DEVICES 권한을 요청합니다.
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES),
                    NEARBY_WIFI_PERMISSION_REQUEST_CODE
                )
            } else {
                // NEARBY_WIFI_DEVICES 권한이 이미 허용되어 있는 경우 처리할 작업을 여기에 추가합니다.
                discoveryPeer()
            }
        } else {
            // S 미만 버전에서는 NEARBY_WIFI_DEVICES 권한을 요청하지 않습니다.

            // LOCATION 권한을 확인하고 요청합니다.
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                // ACCESS_FINE_LOCATION 권한이 이미 허용되어 있는 경우 처리할 작업을 여기에 추가합니다.
                discoveryPeer()
            }
        }
    }


    // 권한 요청 결과를 처리하는 메서드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NEARBY_WIFI_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // NEARBY_WIFI_DEVICES 권한이 허용된 경우 처리할 작업을 여기에 추가합니다.
                    discoveryPeer()
                } else {
                    // NEARBY_WIFI_DEVICES 권한이 거부된 경우 사용자에게 알림을 표시하거나 다른 처리를 수행할 수 있습니다.
                    Toast.makeText(this@MainActivity3,"NEARBY_WIFI 권한 허용 실패",Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ACCESS_FINE_LOCATION 권한이 허용된 경우 처리할 작업을 여기에 추가합니다.
                    discoveryPeer()
                } else {
                    // ACCESS_FINE_LOCATION 권한이 거부된 경우 사용자에게 알림을 표시하거나 다른 처리를 수행할 수 있습니다.
                    Toast.makeText(this@MainActivity3,"ACCESS_FINE 권한 허용 실패",Toast.LENGTH_SHORT).show()
                }
            }
            // 다른 권한 요청에 대한 처리를 추가할 수 있습니다.
        }
    }


//    private fun checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            TedPermission.create()
//                .setPermissionListener(permissionlistener)
//                .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
//                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
//                //                .setDeniedCloseButtonText("닫기")
//                //                .setGotoSettingButtonText("설정")
//                //                .setRationaleTitle("HELLO")
//                .setPermissions(
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE, // 외부 저장소에 데이터를 쓰거나 읽을 수 있는 권한을 제공
////                    Manifest.permission.ACCESS_COARSE_LOCATION, //대략적인 위치 정보에 액세스할 수 있는 권한을 제공
////                    Manifest.permission.NEARBY_WIFI_DEVICES //근처의 Wi-Fi 디바이스에 대한 정보에 액세스할 수 있는 권한을 제공
//                    Manifest.permission.ACCESS_FINE_LOCATION //정확한 위치 정보에 액세스할 수 있는 권한을 제공
//                ).check()
//        }
//    }




}
