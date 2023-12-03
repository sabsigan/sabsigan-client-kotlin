package com.android.sabsigan.viewModel

import com.android.sabsigan.data.User
import com.android.sabsigan.repository.SignFbRepository


class WifiSelectorViewModel: WiFiViewModel() {
    private val fbRepository = SignFbRepository()

    fun getUID() = fbRepository.uid

}