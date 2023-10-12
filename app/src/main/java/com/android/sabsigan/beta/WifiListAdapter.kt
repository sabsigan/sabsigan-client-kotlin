package com.android.sabsigan.beta

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.R
import com.android.sabsigan.databinding.InputWifiPasswordBinding


class WifiListAdapter(val context: Context, var wifiList: MutableList<ScanResult>, var cBSSID: String): RecyclerView.Adapter<WifiListAdapter.MainViewHolder>() {
    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val WifiIcon = view.findViewById<ImageView>(R.id.wifiIcon)
        val WifiName = view.findViewById<TextView>(R.id.wifiName)
        val ConnectText = view.findViewById<TextView>(R.id.connect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wifi_list, parent,false)
        val mainViewHolder = MainViewHolder(view)

        mainViewHolder.itemView.setOnClickListener {
            val position = mainViewHolder.absoluteAdapterPosition
            val SSID = wifiList[position].SSID

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
//                        changeWifiConfiguration(context, SSID, editText.text.toString())
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

        if (getSecurityType(wifiList[position]) != "Open")
            holder.WifiIcon.background = ContextCompat.getDrawable(context, R.drawable.baseline_wifi_password_24)
        else
            holder.WifiIcon.background = ContextCompat.getDrawable(context, R.drawable.baseline_wifi_24)

        when (wifiLevel) {
            in -100 until -66 -> holder.WifiIcon.setImageResource(R.drawable.baseline_wifi_1_bar_24)
            in -66 until -33 -> holder.WifiIcon.setImageResource(R.drawable.baseline_wifi_2_bar_24)
            else -> holder.WifiIcon.setImageResource(R.drawable.baseline_wifi_24)
        }

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

    fun changeWifiConfiguration(context: Context, SSID: String, PASSWORD: String) {
        val wifiConfig = WifiConfiguration()

        // 새로운 와이파이 구성 설정
        wifiConfig.SSID = "\"" + SSID + "\""
        wifiConfig.preSharedKey = "\"" + PASSWORD + "\""

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.disconnect()

        val netId: Int = wifiManager!!.addNetwork(wifiConfig)
        wifiManager!!.enableNetwork(netId, true)
        wifiManager.reconnect()

        Log.d("dddd", "ddddde3")

    }
}
