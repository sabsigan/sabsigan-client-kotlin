package com.android.sabsigan.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class WiFiViewModel: ViewModel() {
    private var _wifiInfo = MutableLiveData<String>()
    // 와이파이 세부정보

    fun updateWiFiData(newData: String){
        _wifiInfo.value = newData
        Log.d("_wifiInfo",newData)
    }
    fun getwifiInfo(): LiveData<String> {
        return _wifiInfo
    }

}