package com.android.sabsigan.main.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.android.sabsigan.R
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.ActivityCreateChatBinding
import com.android.sabsigan.main.user.SearchUserAdapter
import com.android.sabsigan.main.user.SelectedUserAdapter
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

        binding.selectRecyclerView.adapter = SelectedUserAdapter(viewModel)
        binding.userRecyclerView.adapter = SearchUserAdapter(viewModel)

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchUser(newText)
                return true
            }
        })

//        viewModel.inputTxt.observe(this, Observer {
//            (binding.userRecyclerView.adapter as SelectUserAdapter).filter.filter(it)
//        })

        viewModel.selectedList.observe(this, Observer {
            (binding.selectRecyclerView.adapter as SelectedUserAdapter).setUserList(it)
        })

        viewModel.searchedList.observe(this, Observer {
            (binding.userRecyclerView.adapter as SearchUserAdapter).setUserList(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()

//        val intent = Intent()
//        intent.putExtra("selectedList", viewModel.selectedList.value as ArrayList<User>)
//        setResult(RESULT_OK, intent)
    }
}