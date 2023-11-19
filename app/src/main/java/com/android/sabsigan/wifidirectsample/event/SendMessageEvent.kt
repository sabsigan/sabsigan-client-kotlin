package com.android.sabsigan.wifidirectsample.event

import android.net.wifi.p2p.WifiP2pInfo
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SendMessageEvent {
    companion object {
        private val observable = PublishSubject.create<String>()

        fun receive(): Observable<String> = observable

        fun send(value: String) {
            observable.onNext(value)
        }
    }
}