package com.android.sabsigan.wifidirectsample.event

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Rell on 2018. 11. 5..
 */
class StatusChangedEvent {

    companion object {
        //PublishSubject 객체의 경우 구독 이후에 갱신된 값에 대해서만 값을 받는다.
        //과거에 데이터를 무시하고 새로 갱신되는 값만 보고 싶은 경우 사용하기 유용하다.
        private val observable = PublishSubject.create<WifiEnable>()

        open fun receive(): Observable<WifiEnable> = observable

        open fun send(value: WifiEnable) {
            observable.onNext(value)
        }
    }
}

enum class WifiEnable {
    ENABLE, DISABLE
}