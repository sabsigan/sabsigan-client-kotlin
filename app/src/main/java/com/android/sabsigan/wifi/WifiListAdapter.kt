package com.android.sabsigan.wifi

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.R

class WifiListAdapter(val context: Context, var wifiList: MutableList<ScanResult>, var cBSSID: String): RecyclerView.Adapter<WifiListAdapter.MainViewHolder>() {
    private val NETWORK_PASSWORD = "SSID_PASSWORD"

    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val WifiIcon = view.findViewById<ImageView>(R.id.wifiIcon)
        val WifiName = view.findViewById<TextView>(R.id.wifiName)
        val ConnectText = view.findViewById<TextView>(R.id.connect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wifi_list, parent,false)
        val mainViewHolder = MainViewHolder(view)

        mainViewHolder.itemView.setOnClickListener {
//            val position = mainViewHolder.absoluteAdapterPosition
//
//            val SSID = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                wifiList[position].wifiSsid.toString()
//            else
//                wifiList[position].SSID
////            context.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
//
//            val builder = AlertDialog.Builder(context)
//            val builderItem = LayoutInflater.from(parent.context).inflate(R.layout.input_wifi_password, parent, false)
//            val editText = builderItem.findViewById<TextView>(R.id.editText)
//
//            with(builder){
//                setTitle("와이파이 연결")
//                setMessage("비밀번호를 입력해주세요")
//                setView(builderItem.rootView)
//                setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int ->
//                    if(editText.text != null)
//                        changeWifiConfiguration(context, SSID, editText.text.toString(), position)
//                }
//                show()
//            }
        }

        return mainViewHolder
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val wifiName = wifiList[position].SSID
        val wifiLevel = wifiList[position].level

        holder.WifiName.text = wifiName

        // 비밀번호 여부
        if (getSecurityType(wifiList[position]) != "Open")
            holder.WifiIcon.background = ContextCompat.getDrawable(context, R.drawable.baseline_wifi_password_24)
        else
            holder.WifiIcon.background = ContextCompat.getDrawable(context, R.drawable.baseline_wifi_24)

        // 신호 세기
        when (wifiLevel) {
            in -100 until -66 -> holder.WifiIcon.setImageResource(R.drawable.baseline_wifi_1_bar_24)
            in -66 until -33 -> holder.WifiIcon.setImageResource(R.drawable.baseline_wifi_2_bar_24)
            else -> holder.WifiIcon.setImageResource(R.drawable.baseline_wifi_24)
        }

        // 연결된 와이파이
        if (wifiList[position].BSSID == cBSSID) {
            holder.WifiIcon.setColorFilter(ContextCompat.getColor(context, R.color.Blue_700))
            holder.WifiName.setTextColor(ContextCompat.getColor(context, R.color.Blue_700))
            holder.ConnectText.visibility = View.VISIBLE
        } else {
            holder.WifiIcon.setColorFilter(ContextCompat.getColor(context, R.color.test_text))
            holder.WifiName.setTextColor(ContextCompat.getColor(context, R.color.test_text))
            holder.ConnectText.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return wifiList.size
    }

    fun getSecurityType(scanResult: ScanResult): String {
        val security = "Open" // 기본값은 개방된 네트워크로 설정

        if (scanResult.capabilities.contains("WEP")) {
            return "WEP"
        } else if (scanResult.capabilities.contains("PSK") || scanResult.capabilities.contains("WPA")) {
            return "WPA"
        } else if (scanResult.capabilities.contains("EAP")) {
            return "WPA/WPA2 Enterprise"
        }

        return security
    }

    fun changeWifiConfiguration(context: Context, SSID: String, PASSWORD: String, Position: Int) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val WifiConfig = WifiConfiguration()
//
//        // 와이파이 SSID 설정
//        WifiConfig.SSID = "\"" + SSID + "\""
//
//        // 와이파이 비밀번호 설정
//        WifiConfig.preSharedKey = "\"" + PASSWORD + "\""
//
//        // 와이파이를 활성화하고 연결 시도
//        val networkId = WifiManager.addNetwork(WifiConfig)
//        WifiManager.disconnect()
//        WifiManager.enableNetwork(networkId, true)
//        WifiManager.reconnect()

        wifiManager?.addNetwork(WifiConfiguration().apply {
            status = WifiConfiguration.Status.ENABLED

            if(wifiList[Position]?.capabilities?.toUpperCase()?.contains("WEP") ?: false){
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                wepKeys[0] = NETWORK_PASSWORD
            } else if(wifiList[Position]?.capabilities?.toUpperCase()?.contains("WPA") ?: false) {
                allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                preSharedKey = "\"${NETWORK_PASSWORD}\""
            } else {
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                allowedAuthAlgorithms.clear()
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            }

        })

        if (ActivityCompat.checkSelfPermission(
                context,
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
            return
        }

        wifiManager?.configuredNetworks?.forEach {
            if("\"$SSID\"" == "\"${wifiList[Position]?.SSID}\"") {
                wifiManager?.disconnect()
                wifiManager?.enableNetwork(it.networkId, true)
                wifiManager?.reconnect()
                return
            }
        }
    }
}
