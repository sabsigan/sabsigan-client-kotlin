package com.android.sabsigan.beta

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.R

class WifiListAdapter(val wifiList: List<ScanResult>, val BSSID: String): RecyclerView.Adapter<WifiListAdapter.MainViewHolder>() {
    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val WifiIcon = view.findViewById<ImageView>(R.id.wifiIcon)
        val WifiName = view.findViewById<TextView>(R.id.wifiName)
        val ConnectText = view.findViewById<TextView>(R.id.connect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wifi_list, parent,false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
//        holder.WifiIcon
        holder.WifiName.text = wifiList[position].SSID

//        if (wifiList[position].BSSID == BSSID)
//            holder.ConnectText.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return wifiList.size
    }

}
