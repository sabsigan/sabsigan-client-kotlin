package com.android.sabsigan.main.chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.android.sabsigan.databinding.FragmentChattingBinding
import com.android.sabsigan.viewModel.MainViewModel

class ChattingFragment : Fragment() {

    private var _binding: FragmentChattingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChattingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textChatting
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}