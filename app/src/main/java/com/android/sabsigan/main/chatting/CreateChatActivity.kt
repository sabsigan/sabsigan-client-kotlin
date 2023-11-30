package com.android.sabsigan.main.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.android.sabsigan.R
import com.android.sabsigan.broadcastReceiver.WifiConnectReceiver
import com.android.sabsigan.data.SimpleUser
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

        binding.selectRecyclerView.adapter = SelectedUserAdapter(viewModel)
        binding.userRecyclerView.adapter = SearchUserAdapter(viewModel)

        binding.backButton.setOnClickListener { finish() }

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

        binding.nextBtn.setOnClickListener {
            if (binding.nextBtn.text.equals("다음") && viewModel.selectedList.value != null) {
                binding.nextBtn.text = "확인"

                binding.setChatNameLayout.visibility = View.VISIBLE
                binding.userRecyclerView.visibility = View.GONE
                // editText 포커싱
                binding.inputChatName.requestFocus()
                // 키보드 올리기
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.inputChatName, InputMethodManager.SHOW_IMPLICIT)
            } else { // 확인
                val text = viewModel.inputTxt.value

                if (text != null && text.isNotEmpty() && !text.isNullOrBlank()) {
                    val intent = Intent()
                    val list = viewModel.selectedList.value!!.map { user -> User(id = user.id, name = user.name) }
                    intent.putExtra("selectedList", list as ArrayList<User>)
                    intent.putExtra("chatRoomName", text)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

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
    }
}