package com.android.sabsigan.Main.chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.sabsigan.databinding.FragmentChattingBinding

class ChattingFragment : Fragment() {

    private var _binding: FragmentChattingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chattingViewModel =
            ViewModelProvider(this)[ChattingViewModel::class.java]

        _binding = FragmentChattingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textChatting
        chattingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}