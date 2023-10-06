package com.android.sabsigan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.RecyclerListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccessPointerAdapter(val itemList: ArrayList<AccessPoint>):
RecyclerView.Adapter<AccessPointerAdapter.AccessPointViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccessPointerAdapter.AccessPointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pointitem_recycler_view,parent,false)
        return AccessPointViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AccessPointerAdapter.AccessPointViewHolder,
        position: Int
    ) {
        holder.ssid.text = "SSID :" +itemList[position].ssid
        holder.bssid.text = "BSSID : " + itemList[position].bssid
        holder.rssi.text = "RSSI : " + itemList[position].rssi
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }



    inner class AccessPointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ssid = itemView.findViewById<TextView>(R.id.ssid)
        val bssid = itemView.findViewById<TextView>(R.id.bssid)
        val rssi = itemView.findViewById<TextView>(R.id.rssi)
    }
}




data class AccessPoint(var ssid: String, var bssid: String, var rssi: String)