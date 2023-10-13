package com.android.sabsigan.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WiFiViewModel(): ViewModel() {
    private var _wifiInfo = MutableLiveData<String>()

    fun updateWiFiData(newData: String){
        _wifiInfo.value = newData
        Log.d("_wifiInfo",newData)
    }
    fun getwifiInfo(): LiveData<String> {
        return _wifiInfo
    }

}