package com.android.sabsigan.ViewModel

import androidx.databinding.ObservableField

class WifiSelectorViewModel: WiFiViewModel() {
    var iconColor = ObservableField<String>()
    var shadowColor = ObservableField<String>()
}