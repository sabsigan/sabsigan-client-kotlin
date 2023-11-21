package karrel.kr.co.wifidirectsample.event

import android.net.wifi.p2p.WifiP2pDevice
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Rell on 2018. 11. 6..
 */
class ConnectPeerEvent {
    companion object {
        private val observable = PublishSubject.create<WifiP2pDevice>()

        fun receive(): Observable<WifiP2pDevice> = observable

        fun send(value: WifiP2pDevice) {
            observable.onNext(value)
        }
    }
}