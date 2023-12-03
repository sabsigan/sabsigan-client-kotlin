package com.android.sabsigan.main.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.android.sabsigan.R
import com.android.sabsigan.data.User
import com.android.sabsigan.databinding.FragmentUserBinding
import com.android.sabsigan.viewModel.MainViewModel

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.recyclerView.adapter = UserListAdapter(viewModel)

        viewModel.userList.observe(viewLifecycleOwner, Observer {
            (binding.recyclerView.adapter as UserListAdapter).setUserList(it)
        })

        viewModel.clickUser.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showUserDialog(it)
                viewModel.setUserNull()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showUserDialog(user: User) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.profile_popup, null)

        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val imgView = view.findViewById<ImageView>(R.id.UserImg)
        val textTitle = view.findViewById<TextView>(R.id.Title)
        val textState =  view.findViewById<TextView>(R.id.state)
        val buttonConfirm =  view.findViewById<Button>(R.id.Button)

        imgView.setImageBitmap(viewModel.generateAvatar(user.id))
        textTitle.text = user.name
        textState.text = user.state
        buttonConfirm.text = "채팅하기"
        buttonConfirm.setOnClickListener {
            viewModel.createChat(arrayListOf(user))
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

}