package com.android.sabsigan.main.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.android.sabsigan.R
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.ActivityCreateChatBinding
import com.android.sabsigan.main.user.SelectUserAdapter
import com.android.sabsigan.viewModel.CreateChatViewModel

class CreateChatActivity : AppCompatActivity() {
    private var _binding: ActivityCreateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateChatViewModel>()
    private lateinit var wifiConnectReceiver: WifiConnectReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_create_chat)

        val userList = intent.getSerializableExtra("userList") as List<User>
        viewModel.setUserList(userList)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.backButton.setOnClickListener { finish() }

        binding.userRecyclerView.adapter = SelectUserAdapter(viewModel)



//        var searchViewTextListener: SearchView.OnQueryTextListener =
//            object : SearchView.OnQueryTextListener {
//                //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
//                override fun onQueryTextSubmit(s: String): Boolean {
//                    return false
//                }
//
//                //텍스트 입력/수정시에 호출
//                override fun onQueryTextChange(s: String): Boolean {
//                    return false
//                }
//            }
//
//        binding.searchView.setOnQueryTextListener(searchViewTextListener)




            viewModel.userList.observe(this, Observer {
            (binding.userRecyclerView.adapter as SelectUserAdapter).setUserList(it)
        })

//        val myName = intent.array

//        intent.
    }

    override fun onDestroy() {
        super.onDestroy()

//        val intent = Intent()
//        intent.putExtra("selectedList", viewModel.selectedList.value as ArrayList<User>)
//        setResult(RESULT_OK, intent)
    }
}