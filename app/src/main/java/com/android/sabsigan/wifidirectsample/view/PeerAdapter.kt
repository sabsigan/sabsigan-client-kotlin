package karrel.kr.co.wifidirectsample.view

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.R
import com.android.sabsigan.databinding.ItemPeerBinding
import karrel.kr.co.wifidirectsample.event.ConnectPeerEvent

/**
 * Created by Rell on 2018. 11. 6..
 */
class PeerAdapter : RecyclerView.Adapter<PeerAdapter.PeerViewHolder>() {

    private var deviceArrayList = arrayListOf<WifiP2pDevice>()
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PeerViewHolder {
        val binding = ItemPeerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (deviceArrayList == null) return 0

        return deviceArrayList!!.size
    }

    override fun onBindViewHolder(holder: PeerViewHolder, position: Int) {
        holder.setData(deviceArrayList[position])
    }

    fun setData(deviceList: Collection<WifiP2pDevice>) {
        this.deviceArrayList = arrayListOf()

        for (device in deviceList) {
            deviceArrayList.add(device)
        }

        notifyDataSetChanged()
    }


    class PeerViewHolder(val binding: ItemPeerBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var device: WifiP2pDevice
        val name: TextView = binding.name
        val address: TextView = binding.address

        init {

            binding.root.setOnClickListener {
                ConnectPeerEvent.send(device)
            }
        }

        fun setData(device: WifiP2pDevice) {
            this.device = device
            name.text = device.deviceName
            address.text = device.deviceAddress
        }
    }
}