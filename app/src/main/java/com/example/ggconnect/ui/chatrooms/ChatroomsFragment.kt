package com.example.ggconnect.ui.chatrooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ggconnect.databinding.FragmentChatroomsBinding

class ChatroomsFragment : Fragment() {

    private var _binding: FragmentChatroomsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ChatroomsViewModel::class.java)

        _binding = FragmentChatroomsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textChatrooms
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}