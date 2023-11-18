package com.android.sabsigan.wifidirectsample.event

import android.net.wifi.p2p.WifiP2pInfo
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Rell on 2018. 11. 6..
 */
class ConnectionInfoEvent {
    companion object {
        private val observable = PublishSubject.create<WifiP2pInfo>()

        fun receive(): Observable<WifiP2pInfo> = observable

        fun send(value: WifiP2pInfo) {
            observable.onNext(value)
        }
    }
}