package karrel.kr.co.wifidirectsample.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.sabsigan.R
class DefaultFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // 연결되었을때 첫 화면
        return inflater.inflate(R.layout.fragment_default, container, false)
    }

}
