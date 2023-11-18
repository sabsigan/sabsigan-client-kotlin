package com.android.sabsigan.Wifi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.sabsigan.R
import com.android.sabsigan.wifidirectsample.WifiDirectReceiver

class WifiDirectActivity : AppCompatActivity() {

    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager

    private val intentFilter = IntentFilter()
    private lateinit var receiver : WifiDirectReceiver

    private val peers = mutableListOf<WifiP2pDevice>()
    lateinit var listAdapter: WiFiPeerListAdapter

    var isWifiP2pEnabled: Boolean = false

    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.NEARBY_WIFI_DEVICES
    )
    val listPermissionsNeeded: MutableList<String> = ArrayList()
    val MULTIPLE_PERMISSIONS = 10
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_direct)

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)


        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)




        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    // Code for when the discovery initiation is successful goes here.
                    // No services have actually been discovered yet, so this method
                    // can often be left blank. Code for peer discovery goes in the
                    // onReceive method, detailed below.
                    Toast.makeText(this@WifiDirectActivity,"discoverPeers 시작",Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(reasonCode: Int) {
                    // Code for when the discovery initiation fails goes here.
                    // Alert the user that something went wrong.
                    Toast.makeText(this@WifiDirectActivity,"discoverPeers 실패",Toast.LENGTH_SHORT).show()
                }
            })

//        connect() TODO peer가 찾아졌을때 수행하도록 해야함
    }




    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
            listAdapter.notifyDataSetChanged()

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.

            connect()
        }

        if (peers.isEmpty()) {
            Log.d(ContentValues.TAG, "No devices found")
            return@PeerListListener
        }
    }



    @SuppressLint("MissingPermission")
    fun connect() {
        // Picking the first device found on the network.
        val device = peers[0]

        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        manager.connect(channel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                Toast.makeText(
                    this@WifiDirectActivity,
                    "Connect Success!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@WifiDirectActivity,
                    "Connect failed. Retry.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }





























    //권한 추가
//    private fun checkPermissions(): Boolean {
//        for (p in permissions) {
//            var result = ContextCompat.checkSelfPermission(this, p)
//
//            if (result != PackageManager.PERMISSION_GRANTED)
//                listPermissionsNeeded.add(p)
//        }
//
//        if (!listPermissionsNeeded.isEmpty())
//            return false
//
//        return true
//    }
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this,
//            listPermissionsNeeded.toTypedArray(),
//            MULTIPLE_PERMISSIONS
//        )
//    }

    private fun requestPermissions(activity: Activity) { //권한 설정
        if(ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(activity, permissions, 1)
        }
    }

    //리시버 등록 및 해제
    public override fun onResume() {
        super.onResume()
        requestPermissions(this)
        receiver = WifiDirectReceiver(manager, channel, this)
        registerReceiver(receiver, intentFilter)

    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }


//    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
//        this.isWifiP2pEnabled = isWifiP2pEnabled
//    }


}