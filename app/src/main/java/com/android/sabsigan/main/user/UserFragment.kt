package com.android.sabsigan.main.user

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sabsigan.R
import com.android.sabsigan.viewModel.UserViewModel
import com.android.sabsigan.databinding.FragmentUserBinding

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

//        val textView: TextView = binding.textUser
//        viewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupAdapter() {
        binding.recyclerView.adapter = UserListAdapter(viewModel)
    }
}