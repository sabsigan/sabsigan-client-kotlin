package com.android.sabsigan.broadcastReceiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.Tag
import android.util.Log
import androidx.core.app.ActivityCompat
import com.android.sabsigan.R
import com.android.sabsigan.Wifi.WifiDirectActivity

class WifiDirectReceiver(private val manager : WifiP2pManager,private val channel : WifiP2pManager.Channel,private val activity: WifiDirectActivity) : BroadcastReceiver() {


    private val peerListListener: WifiP2pManager.PeerListListener = activity.peerListListener



    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                if(activity.isWifiP2pEnabled){
                    // Wi-Fi P2P is enabled
                }else{
                    // Wi-Fi P2P is disabled
                }
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {

                // The peer list has changed! We should probably do something about
                // that.
                manager.requestPeers(channel, peerListListener)
                Log.d(TAG, "P2P peers changed")

            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

                // Connection state changed! We should probably do something about
                // that.
                // Request available peers from the wifi p2p manager. This is an        // asynchronous call and the calling activity is notified with a        // callback on PeerListListener.onPeersAvailable()       

                // Connection state changed! We should probably do something about
                // that.

                manager?.let { manager ->

                    val networkInfo: NetworkInfo? = intent
                        .getParcelableExtra( WifiP2pManager.EXTRA_NETWORK_INFO) as? NetworkInfo

                    if (networkInfo?.isConnected == true) {

                        // We are connected with the other device, request connection
                        // info to find group owner IP

                        manager.requestConnectionInfo(channel, connectionListener)
                    }
                }


            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                (context.supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment)
                    .apply {
                        updateThisDevice(
                            intent.getParcelableExtra(
                                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE
                            ) as WifiP2pDevice
                        )
                    }
            }
        }


    }
    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->

        // InetAddress from WifiP2pInfo struct.
        val groupOwnerAddress: String = info.groupOwnerAddress.hostAddress

        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }



//    private fun checkPermissions(context: Context): Boolean {
//        val permissionsToCheck = arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.NEARBY_WIFI_DEVICES
//        )
//
//        return permissionsToCheck.all {
//            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }





}
