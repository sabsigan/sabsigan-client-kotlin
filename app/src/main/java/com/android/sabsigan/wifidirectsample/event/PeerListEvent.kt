package com.android.sabsigan.wifidirectsample.event

import android.net.wifi.p2p.WifiP2pDeviceList
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Rell on 2018. 11. 6..
 */
class PeerListEvent {
    companion object {
        private val observable = PublishSubject.create<WifiP2pDeviceList>()

        fun receive(): Observable<WifiP2pDeviceList> = observable

        fun send(value: WifiP2pDeviceList) {
            observable.onNext(value)
        }
    }
}