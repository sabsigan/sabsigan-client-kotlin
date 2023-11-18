package com.android.sabsigan.wifidirectsample.event

import android.net.wifi.p2p.WifiP2pDeviceList
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Rell on 2018. 11. 6..
 */
class ResetDataEvent {
    companion object {
        private val observable = PublishSubject.create<Boolean>()

        fun receive(): Observable<Boolean> = observable

        fun send(value: Boolean) {
            observable.onNext(value)
        }
    }
}