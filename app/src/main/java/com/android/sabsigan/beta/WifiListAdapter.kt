package com.android.sabsigan.beta

import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.R

class WifiListAdapter(val context: Context, val wifiList: MutableList<ScanResult>, var cBSSID: String): RecyclerView.Adapter<WifiListAdapter.MainViewHolder>() {
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
        holder.WifiName.text = wifiList[position].SSID

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
}
