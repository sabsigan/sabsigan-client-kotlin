package com.android.sabsigan.main.chatting

import ChatListAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sabsigan.R
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.ChatRoom
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.FragmentChattingBinding
import com.android.sabsigan.main.user.UserListAdapter
import com.android.sabsigan.viewModel.MainViewModel

class ChattingFragment : Fragment() {
    private var _binding: FragmentChattingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatting, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.recyclerView.adapter = ChatListAdapter(viewModel)

        viewModel.chatList.observe(viewLifecycleOwner, Observer {
            (binding.recyclerView.adapter as ChatListAdapter).setChatList(it)
        })

        viewModel.temp.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showUserDialog(it)
                viewModel.temp.value = null
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showUserDialog(chatRoom: ChatRoom) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.chatroom_popup, null)

        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val defaultLayout = view.findViewById<LinearLayout>(R.id.defaultLayout)
        val inputLayout = view.findViewById<LinearLayout>(R.id.inputLayout)

        val editName = view.findViewById<TextView>(R.id.edit_chatname)
        val exitChat =  view.findViewById<TextView>(R.id.exit_chat)
        val inputTxt = view.findViewById<EditText>(R.id.inputName)
        val btn =  view.findViewById<TextView>(R.id.send)
        val cancel = view.findViewById<TextView>(R.id.cancel)

        editName.setOnClickListener {
            defaultLayout.visibility = View.GONE
            inputLayout.visibility = View.VISIBLE
        }

        exitChat.setOnClickListener {
            viewModel.exitChatRoom(chatRoom)
            viewModel.removeChatList(chatRoom)
            alertDialog.dismiss()
        }

        btn.setOnClickListener {
            Log.d("클릭", "보내기")

            if ((inputTxt.text.toString()).isNotBlank()) {
                Log.d("클릭", "${inputTxt.text}")
                viewModel.changeChatRoomName(chatRoom, inputTxt.text.toString())
            }

            alertDialog.dismiss()
        }

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialog.show()
    }
}